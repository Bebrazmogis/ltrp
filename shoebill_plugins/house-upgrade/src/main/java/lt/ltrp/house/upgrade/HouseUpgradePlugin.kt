package lt.ltrp.house.upgrade

import lt.ltrp.DatabasePlugin
import lt.ltrp.HousePlugin
import lt.ltrp.house.HouseController
import lt.ltrp.house.`object`.House
import lt.ltrp.house.event.HouseLoadedEvent
import lt.ltrp.house.upgrade.command.HouseOwnerCommands
import lt.ltrp.house.upgrade.command.UpgradeCommands
import lt.ltrp.house.upgrade.dao.HouseUpgradeDao
import lt.ltrp.house.upgrade.dao.impl.MySqlHouseUpgradeDaoImpl
import lt.ltrp.house.upgrade.data.HouseUpgrade
import lt.ltrp.resource.DependentPlugin
import net.gtaun.shoebill.common.command.CommandGroup
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority

/**
 * Created by Bebras on 2016-10-14.
 * This plugin manages house upgrades
 * It loads them at startup, clears them when shutting down
 * It also instantiates the [HouseUpgradeController] implementation which allows the world to manage house upgrades
 *
 * <b>This plugin supports being reloaded at runtime</b>
 */
class HouseUpgradePlugin: DependentPlugin() {

    private lateinit var eventManager: EventManagerNode
    private lateinit var upgradeDao: HouseUpgradeDao
    private lateinit var upgradeController: HouseUpgradeController
    private lateinit var playerCommandManager: PlayerCommandManager

    init {
        addDependency(DatabasePlugin::class)
        addDependency(HousePlugin::class)
    }

    override fun onDependenciesLoaded() {
        eventManager = getEventManager().createChildNode()
        upgradeDao = MySqlHouseUpgradeDaoImpl(DatabasePlugin.get(DatabasePlugin::class.java).dataSource)
        upgradeController = HouseUpgradeControllerImpl(upgradeDao)

        registerEvents()
        registerCommands()

        HouseController.get().all.forEach { onHouseLoad(it) }
    }

    override fun onDisable() {
        // Otherwise nothing was created and the controller is null
        if(isDependenciesLoaded) {
            HouseController.get().all.forEach { it.upgrades.clear() }
        }
        eventManager.cancelAll()
        playerCommandManager.destroy()
    }

    private fun registerEvents() {
        eventManager.registerHandler(HouseLoadedEvent::class.java, { onHouseLoad(it.property) })
    }

    private fun onHouseLoad(house: House) {
        house.upgrades.addAll(upgradeDao.get(house))
    }

    private fun registerCommands() {
        playerCommandManager = PlayerCommandManager(eventManager)
        val group = CommandGroup()
        playerCommandManager.registerCommands(HouseOwnerCommands(eventManager, group))
        playerCommandManager.registerChildGroup(group, "hu")
        playerCommandManager.registerCommands(UpgradeCommands())
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL)
    }

}