package lt.ltrp.radio

import lt.ltrp.DatabasePlugin
import lt.ltrp.radio.dao.RadioStationDao
import lt.ltrp.radio.dao.impl.SqlRadioStationDao
import lt.ltrp.radio.data.RadioStation
import lt.ltrp.resource.DependentPlugin
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManagerNode


/**
 * @author Bebras
 *         2016.04.13.
 *
 *         Radio station management plugin, provides abstract classes for implementing an in-game radio
 *
 *         Future plans:
 *         <ul>
 *            <li>Dialogs for CRUD</li>
 *         </ul>
 */
@ShoebillMain("LTRP Radio plugin", "Bebras")
class RadioPlugin : DependentPlugin() {

    private lateinit var eventManagerNode: EventManagerNode
    lateinit var stationDao: RadioStationDao
        private set

    init {
        addDependency(DatabasePlugin::class)
    }

    override fun onEnable() {
        eventManagerNode = eventManager.createChildNode()
    }

    override fun onDisable()  {
        eventManagerNode.cancelAll()
    }

    override fun onDependenciesLoaded() {
        val ds = ResourceManager.get().getPlugin(DatabasePlugin::class.java)?.dataSource
        this.stationDao = SqlRadioStationDao(ds, logger)
        RadioStation.stations = stationDao.get()
        logger.info("Loaded " + RadioStation.stations.size + " radio stations")
    }

}
