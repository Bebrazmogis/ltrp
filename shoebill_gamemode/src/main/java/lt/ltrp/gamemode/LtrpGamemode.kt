package lt.ltrp.gamemode

import lt.ltrp.resource.DependentPlugin
import lt.maze.injector.resource.BindingPlugin
import net.gtaun.shoebill.resource.Gamemode
import net.gtaun.shoebill.resource.ResourceManager

/**
 * Created by Bebras on 2016-10-28.
 */
class LtrpGamemode: Gamemode() {


    override fun onEnable() {
        logger.info("Gamemode started")
        val plugins = ResourceManager.get().plugins
        logger.info("" + plugins.size + " plugins loaded")

        val dependentPlugins = plugins.filterIsInstance(DependentPlugin::class.java)
        logger.info("" + dependentPlugins.size + " plugins are dependent")

        val unloadedDependentPlugins = dependentPlugins.filter { !it.isDependenciesLoaded }
        logger.info("Unloaded dependent plugins(" + unloadedDependentPlugins.size + "):")
        unloadedDependentPlugins.forEach { plugin ->
            logger.info(plugin.javaClass.name + " dependencies: " + plugin.dependencies.joinToString(":"))
        }

        val bindingPlugins = dependentPlugins.filterIsInstance<BindingPlugin>()
        logger.info("" + bindingPlugins.size + " binding plugins loaded");
        logger.info("Available bindings: ")
        bindingPlugins.forEach {
            logger.info(it.getKodeinModule().toString())
        }
    }

    override fun onDisable() {
        logger.info("Gamemode disabled")
    }


}