package lt.ltrp.resource

import net.gtaun.shoebill.event.resource.ResourceEnableEvent
import net.gtaun.shoebill.resource.Resource
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.HandlerEntry
import kotlin.reflect.KClass
import net.gtaun.shoebill.resource.Plugin
/**
 * Created by Bebras on 2016-10-11.
 */
abstract class DependentPlugin: Plugin() {

    val dependencies = mutableSetOf<KClass<out Plugin>>()
    val loadedDependencies = mutableSetOf<Plugin>()
    var resourceEnableEntry: HandlerEntry? = null
    var dependencyLoadEntry: HandlerEntry? = null
    var isDependenciesLoaded = false

    abstract fun onDependenciesLoaded()
    open fun onDependenciesLoaded(set: Set<Plugin>) {

    }

    override fun onEnable() {
        // First we should see how many dependencies are already loaded and how many are missing
        var missing = 0
        dependencies.forEach {
            val plugin = ResourceManager.get().getPlugin(it.java)
            // if plugin is not found or it a dependent plugin which is not loaded yet, we wait
            if(plugin == null || plugin is DependentPlugin && !plugin.isDependenciesLoaded) {
                missing++
            } else
                loadedDependencies.add(plugin)
        }
        // If some dependencies are still missing, we need to wait for them
        if(missing > 0) {
            resourceEnableEntry = eventManager.registerHandler(ResourceEnableEvent::class.java, { onResourceEnabled(it.resource) })
            dependencyLoadEntry = eventManager.registerHandler(PluginDependencyLoadEvent::class.java, { onPluginDependencyLoad(it.plugin) })
        } else {
            onLoad()
        }
    }

    open fun <T> addDependency(clz: KClass<T>) where T: Plugin {
        dependencies.add(clz)
    }

    private fun onResourceEnabled(resource: Resource) {
        if(resource is Plugin && resource !is DependentPlugin && dependencies.contains(resource.javaClass.kotlin)) {
            loadedDependencies.add(resource)
            logger.debug("Loaded dependency from resource event:" + resource)
            if(loadedDependencies.size == dependencies.size) {
                onLoad()
            }
        }
    }

    private fun onPluginDependencyLoad(plugin: DependentPlugin) {
        if(dependencies.contains(plugin.javaClass.kotlin)) {
            loadedDependencies.add(plugin)
            logger.debug("Loaded dependency from dependency event: "+ plugin)
            if(loadedDependencies.size == dependencies.size)
                onLoad()
        }
    }

    private fun onLoad() {
        isDependenciesLoaded = true
        logger.info("Dependencies loaded " + loadedDependencies.map { it.javaClass.name }.joinToString(":"))
        onDependenciesLoaded()
        onDependenciesLoaded(loadedDependencies)
        // Plugin might get disabled and thus destroy the eventManager
        if(eventManager != null)
            eventManager.dispatchEvent(PluginDependencyLoadEvent(this))
        dependencyLoadEntry?.cancel()
        resourceEnableEntry?.cancel()
    }

    override fun onDisable() {
        dependencies.clear()
        loadedDependencies.clear()
        dependencyLoadEntry?.cancel()
        resourceEnableEntry?.cancel()
    }
}
/*
*
* final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            node.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();
* */