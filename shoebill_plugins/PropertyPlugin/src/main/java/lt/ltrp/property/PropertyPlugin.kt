package lt.ltrp.property

import lt.ltrp.BusinessController
import lt.ltrp.GarageController
import lt.ltrp.property.command.PropertyAcceptCommands
import lt.ltrp.property.command.PropertyCommands
import lt.ltrp.house.HouseController
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.`object`.Property
import net.gtaun.shoebill.common.command.CommandGroup
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.shoebill.resource.Plugin
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority

import java.util.ArrayList

/**
   * @author Bebras
  * *         2016.04.19.
 */
class PropertyPlugin : Plugin(), PropertyController {

    private lateinit var eventManagerNode: EventManagerNode

    override fun onEnable() {
        eventManagerNode = eventManager.createChildNode()

        val commandManager = PlayerCommandManager(eventManagerNode)
        commandManager.registerCommands(PropertyCommands(eventManagerNode))

        val acceptGroup = CommandGroup()
        acceptGroup.registerCommands(PropertyAcceptCommands(eventManagerNode))
        commandManager.registerChildGroup(acceptGroup, "/accept")

        commandManager.installCommandHandler(HandlerPriority.NORMAL)
        logger.info(description.name!! + " loaded")
    }

    override fun onDisable() {
        eventManagerNode.cancelAll()
    }

    override fun getProperties(): Collection<Property> {
        val cl = ArrayList<Property>()
        cl.addAll(HouseController.get().getAll())
        cl.addAll(BusinessController.get().getBusinesses())
        cl.addAll(GarageController.get().getGarages())
        return cl
    }

    override fun get(player: LtrpPlayer) : Property? {
        return Property.get().filter { it.exit != null && it.exit.distance(player.location) < 200 }.first()
    }
}
