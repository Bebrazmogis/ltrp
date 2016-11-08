package lt.ltrp.trucker;

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import lt.ltrp.DatabasePlugin
import lt.ltrp.JobPlugin
import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.event.PaydayEvent
import lt.ltrp.event.vehicle.VehicleEngineStartEvent
import lt.ltrp.resource.DependentPlugin
import lt.ltrp.trucker.`object`.TruckerJob
import lt.ltrp.trucker.`object`.impl.TruckerJobImpl
import lt.ltrp.trucker.command.GeneralTruckerCommands
import lt.ltrp.trucker.command.TruckerCargoGroupCommands
import lt.ltrp.trucker.command.TruckerTrailerGroupCommands
import lt.ltrp.trucker.controller.IndustryStockController
import lt.ltrp.trucker.controller.impl.IndustryStockControllerImpl
import lt.ltrp.trucker.dao.IndustryStockDao
import lt.ltrp.trucker.dao.TruckerJobDao
import lt.ltrp.trucker.dao.impl.*
import lt.maze.injector.resource.BindingPlugin
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.shoebill.event.resource.ResourceEnableEvent
import net.gtaun.shoebill.resource.Plugin
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

/**
 * @author Bebras
 *         2016.06.19.
 */
class TruckerJobPlugin: DependentPlugin(), BindingPlugin {

    private lateinit var eventManagerNode: EventManagerNode
    private lateinit var playerCommandManager: PlayerCommandManager
    private lateinit var truckerJobDao: TruckerJobDao
    lateinit var truckerJob: TruckerJob
        get
        private set
    private lateinit var industryStockDao: IndustryStockDao
    private lateinit var truckerHolder: TruckerPlayerLifecycleHolder
    private lateinit var truckerVehicleHolder: TruckerVehicleLifecycleHolder

    init {
        addDependency(DatabasePlugin::class)
        addDependency(JobPlugin::class)
    }

    override fun onEnable() {
        super.onEnable()
        eventManagerNode = eventManager.createChildNode()
    }

    override fun onDisable() {
        super.onDisable()
        eventManagerNode.cancelAll()
        playerCommandManager.destroy()
    }

    override fun onDependenciesLoaded() {
        val db = ResourceManager.get().getPlugin(DatabasePlugin::class.java)
        val cargoDao = MySqlCargoDaoImpl(db.dataSource)
        val industryProductionMaterialDao = MySqlIndustryProductionMaterialDaoImpl(db.dataSource, cargoDao)
        val  productionDao = MySqlProductionDaoImpl(db.dataSource, industryProductionMaterialDao)
        industryStockDao = MySqlIndustryStockDaoImpl(db.dataSource, cargoDao)
        val  industryDao = MySqlIndustryDaoImpl(db.dataSource, eventManagerNode, productionDao, industryStockDao)
        truckerJobDao  =  MySqlTruckerJobImpl(db.dataSource, eventManagerNode)

        truckerJob = TruckerJobImpl(JobPlugin.JobId.Trucker.id, eventManager)

        IndustryContainer.industries.addAll(industryDao.getFull())

        truckerHolder = TruckerPlayerLifecycleHolder(eventManagerNode)
        truckerVehicleHolder = TruckerVehicleLifecycleHolder(eventManagerNode)

        registerCommands()
        addEventHandlers()
    }

    fun getVehicleHolder(): TruckerVehicleLifecycleHolder {
        return truckerVehicleHolder
    }

    override fun getKodeinModule(): Kodein.Module {
        return Kodein.Module {
            bind<IndustryStockController>() with singleton { IndustryStockControllerImpl(industryStockDao) }
        }
    }

    private fun registerCommands() {
        playerCommandManager = PlayerCommandManager(eventManagerNode)
        playerCommandManager.registerCommands(GeneralTruckerCommands(this, eventManagerNode))
        playerCommandManager.registerChildGroup(TruckerCargoGroupCommands(truckerJob, eventManagerNode, truckerHolder, truckerVehicleHolder),
                "cargo")
        playerCommandManager.registerChildGroup(TruckerTrailerGroupCommands(eventManager), "trailer")
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL)
    }

    private fun addEventHandlers() {
        eventManagerNode.registerHandler(PaydayEvent::class.java, { onPayDay() })
        eventManagerNode.registerHandler(VehicleEngineStartEvent::class.java, { onVehicleEngineStart(it) })
    }

    private fun onPayDay() {
        Thread(IndustryUpdateTask()).start()
    }
    private fun onVehicleEngineStart(event: VehicleEngineStartEvent) {
        // Checking whether there is a [TruckerVehicle] object associated with the vehicle or its trailer
        var truckerVehicle = truckerVehicleHolder.getObject(event.vehicle)
        if(truckerVehicle == null && event.vehicle.isTrailerAttached) {
            truckerVehicle = truckerVehicleHolder.getObject(LtrpVehicle.getByVehicle(event.vehicle.trailer))
        }

        if(truckerVehicle != null && truckerVehicle.isBeingLoaded) {
            event.player.sendErrorMessage("Sunkveþimis kraunamas, niekur vaþiuot negalite!")
            event.deny()
        }
    }

}
