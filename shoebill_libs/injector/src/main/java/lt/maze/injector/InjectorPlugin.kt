package lt.maze.injector

import com.github.salomonbrys.kodein.Kodein
import lt.maze.injector.resource.BindingPlugin
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.event.resource.ResourceLoadEvent
import net.gtaun.shoebill.resource.Plugin
import net.gtaun.shoebill.resource.Resource
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.HandlerEntry

/**
 * Created by Bebras on 2016-10-28.
 * This plugin provides available dependency injections
 * On every new [BindingPlugin] load, it adds them to the [Kodein] container
 * To use get the instances, get [InjectorPlugin] instance via the [ResourceManager]
 *
 * This plugin should be loaded before any other plugins as it does <b>not</b> support being reloaded
 * Meaning it will ignore plugins that are already loaded and not get their injections
 */
@ShoebillMain("Dependency Injector Plugin", "Bebras")
class InjectorPlugin: Plugin() {

    private lateinit var handlerEntry: HandlerEntry
    var kodein: Kodein? = null

    override fun onEnable() {
        handlerEntry = eventManager.registerHandler(ResourceLoadEvent::class.java, { onResourceLoaded(it.resource) })
        logger.info("Kodein injector plugin loaded")
    }

    override fun onDisable() {
        handlerEntry.cancel()
        logger.info("Kodein injector plugin unloaded")
    }

    private fun onResourceLoaded(resource: Resource) {
        if(resource is BindingPlugin) {
            kodein = Kodein {
                getBindingPlugins().forEach {
                    import(it.getKodeinModule())
                }
            }
        }
    }

    private fun getBindingPlugins(): Collection<BindingPlugin> {
        return ResourceManager.get().plugins.filterIsInstance<BindingPlugin>()
    }

}