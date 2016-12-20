package lt.ltrp.resource

import net.gtaun.shoebill.event.resource.ResourceEvent

/**
 * Created by Bebras on 2016-11-09.
 * Event is dispatched whenever a [DependentPlugin] dependencies are loaded
 */
class PluginDependencyLoadEvent(var plugin: DependentPlugin) : ResourceEvent(plugin) {

}