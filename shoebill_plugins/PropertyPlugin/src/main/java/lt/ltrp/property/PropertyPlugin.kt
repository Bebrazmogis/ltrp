package lt.ltrp.property

import lt.ltrp.property.`object`.Property
import lt.ltrp.property.command.PropertyCommands
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.shoebill.resource.Plugin
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority


/**
   * @author Bebras
  * *         2016.04.19.
 */
@ShoebillMain("Property plugin", "Bebras")
class PropertyPlugin : Plugin() {

    private lateinit var eventManagerNode: EventManagerNode

    override fun onEnable() {
        eventManagerNode = eventManager.createChildNode()

        val commandManager = PlayerCommandManager(eventManagerNode)
        commandManager.registerCommands(PropertyCommands(eventManagerNode))

        commandManager.installCommandHandler(HandlerPriority.NORMAL)
        logger.info(description.name + " loaded")
    }

    override fun onDisable() {
        eventManagerNode.cancelAll()
    }

    fun getProperties(): Collection<Property> {
        return Property.propertyList
    }
}
