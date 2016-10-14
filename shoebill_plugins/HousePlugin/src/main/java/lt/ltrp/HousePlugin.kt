package lt.ltrp;

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.command.HouseCommands
import lt.ltrp.command.HouseOwnerCommands
import lt.ltrp.command.HouseUpgradeCommands
import lt.ltrp.dao.impl.MySqlHouseDaoImpl
import lt.ltrp.event.property.HouseEvent
import lt.ltrp.house.`object`.House
import lt.ltrp.resource.DependentPlugin
import lt.maze.streamer.`object`.DynamicPickup
import lt.maze.streamer.event.PlayerDynamicPickupEvent
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority

/**
 * @author Bebras
 *         2016.05.18.
 */
class HousePlugin: DependentPlugin() {

    private lateinit var eventManager: EventManagerNode
    private lateinit var houseController: HouseControllerImpl
    private lateinit var playerCommandManager: PlayerCommandManager

    override fun onEnable() {
        eventManager = getEventManager().createChildNode()

        addDependency(PropertyPlugin::class)
        addDependency(DatabasePlugin::class)
    }

    override fun onDependenciesLoaded() {
        val dao = MySqlHouseDaoImpl(DatabasePlugin.get(DatabasePlugin::class.java).dataSource, eventManager)
        houseController = HouseControllerImpl(HouseContainer.instance, dao, eventManager)

        addEventHandlers()
        addCommands()
    }

    private fun addCommands() {
        playerCommandManager = PlayerCommandManager(eventManager)
        playerCommandManager.registerCommands(HouseCommands(eventManager))
        playerCommandManager.registerCommands(HouseOwnerCommands(eventManager))
        val group = HouseUpgradeCommands(eventManager).group
        playerCommandManager.registerChildGroup(group, "hu")

        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL)

    }

    private fun addEventHandlers() {
        /*
        eventManager.registerHandler(PlayerPlantWeedEvent::class.java, {
        houseDao.insert(e.getWeedSapling());
    });

        node.registerHandler(WeedGrowEvent.class, e -> {
        getHouseDao().update(e.getSapling());

        LtrpPlayer owner = LtrpPlayer.get(e.getSapling().getPlantedByUser());
        if(owner != null && e.isFullyGrown()) {
            owner.sendMessage(net.gtaun.shoebill.data.Color.DARKGREEN, "..Galbût þolë namie jau uþaugo?");
        }
    });*/

        eventManager.registerHandler(PlayerDynamicPickupEvent::class.java,{ onPlayerPickUpDynamicPickup(LtrpPlayer.get(it.player), it.pickup) })
        eventManager.registerHandler(HouseEvent::class.java, { onHouseEvent(it.house) })
    }

    private fun onPlayerPickUpDynamicPickup(player: LtrpPlayer, pickup: DynamicPickup) {
        val house = houseController.all.firstOrNull { it.pickup == pickup }
        if (house != null) {
            var s = " Namo numeris: " + house.UUID
            if (house.rentPrice > 0) {
                s += String.format("~n~Nuomos mokestis: %d~n~Nuomavimuisi - /rentroom", house.rentPrice)
            }
            player.sendInfoText(s, 15);
        }
    }

    private fun onHouseEvent(house: House) {
        logger.info("Updating house " + house.UUID + " asynchronously")
        Thread({
            houseController.update(house)
        }).start()
    }

    override fun onDisable() {
        eventManager.cancelAll()
        playerCommandManager.destroy()
        houseController.clear()
    }

}
