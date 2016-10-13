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
    var handlerEntry: HandlerEntry? = null
    var isDependenciesLoaded = false

    abstract fun onDependenciesLoaded()
    open fun onDependenciesLoaded(set: Set<Plugin>) {

    }

    override fun onEnable() {
        // First we should see how many dependencies are already loaded and how many are missing
        var missing = 0
        dependencies.forEach {
            if(ResourceManager.get().getPlugin(it.java) == null) {
                missing++
            }
        }
        // If some dependencies are still missing, we need to wait for them
        if(missing > 0)
            handlerEntry = eventManager.registerHandler(ResourceEnableEvent::class.java, { onResourceEnabled(it.resource) })
    }

    open fun <T> addDependency(clz: KClass<T>) where T: Plugin {
        dependencies.add(clz)
    }

    private fun onResourceEnabled(resource: Resource) {
        if(resource is Plugin && dependencies.contains(resource.javaClass.kotlin)) {
            loadedDependencies.add(resource)
            if(loadedDependencies.size == dependencies.size) {
                onDependenciesLoaded()
                onDependenciesLoaded(loadedDependencies)
                handlerEntry?.cancel()
                isDependenciesLoaded = true
            }
        }
    }

    override fun onDisable() {
        dependencies.clear()
        loadedDependencies.clear()
        handlerEntry?.cancel()
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