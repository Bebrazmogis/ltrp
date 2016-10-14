package lt.ltrp.house.weed

import lt.ltrp.DatabasePlugin
import lt.ltrp.house.HouseController
import lt.ltrp.house.`object`.House
import lt.ltrp.house.event.HouseLoadedEvent
import lt.ltrp.house.weed.dao.HouseWeedDao
import lt.ltrp.house.weed.dao.MySqlHouseWeedDaoImpl
import lt.ltrp.resource.DependentPlugin
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2016-10-14.
 * This plugin manages house weed
 * It loads the weed and adds it to [House#weedSplings] collection
 * It also implements the [HouseWeedControllerImpl] for public methods for interacting with house weed
 *
 * <b>This plugin supports being reloaded at runtime</b>
 */
class HouseWeedPlugin: DependentPlugin() {

    private lateinit var eventManager: EventManagerNode
    private lateinit var houseWeedDao: HouseWeedDao
    private lateinit var houseWeedController: HouseWeedControllerImpl

    init {
        addDependency(DatabasePlugin::class)
    }

    override fun onEnable() {
        eventManager = getEventManager().createChildNode()
    }

    override fun onDependenciesLoaded() {
        houseWeedDao = MySqlHouseWeedDaoImpl(DatabasePlugin.get(DatabasePlugin::class.java).dataSource, eventManager)
        houseWeedController = HouseWeedControllerImpl(houseWeedDao, eventManager)

        addEventsListeners()
        HouseController.get().all.forEach { it.weedSaplings.addAll(houseWeedDao.getWeed(it)) }
    }

    override fun onDisable() {
        eventManager.destroy()
        HouseController.get().all.forEach {
            it.weedSaplings.forEach { it.destroy() }
            it.weedSaplings.clear()
        }
    }

    private fun addEventsListeners() {
        eventManager.registerHandler(HouseLoadedEvent::class.java, { onHouseLoad(it.property) })
    }

    private fun onHouseLoad(house: House) {
        house.weedSaplings.addAll(houseWeedDao.getWeed(house))
    }

}