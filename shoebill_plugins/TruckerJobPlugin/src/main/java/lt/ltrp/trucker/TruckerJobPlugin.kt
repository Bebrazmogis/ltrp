package lt.ltrp.trucker;

import lt.ltrp.DatabasePlugin
import lt.ltrp.JobPlugin
import lt.ltrp.resource.DependentPlugin
import lt.ltrp.trucker.`object`.TruckerJob
import lt.ltrp.trucker.`object`.impl.TruckerJobImpl
import lt.ltrp.trucker.command.GeneralTruckerCommands
import lt.ltrp.trucker.command.TruckerCargoGroupCommands
import lt.ltrp.trucker.dao.TruckerJobDao
import lt.ltrp.trucker.dao.impl.*
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.shoebill.event.resource.ResourceEnableEvent
import net.gtaun.shoebill.resource.Plugin
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority
import java.util.*

/**
 * @author Bebras
 *         2016.06.19.
 */
class TruckerJobPlugin: DependentPlugin() {

    private lateinit var eventManagerNode: EventManagerNode
    private val playerCommandManager = PlayerCommandManager(eventManagerNode)
    private var truckerJobDao: TruckerJobDao? = null
    var truckerJob: TruckerJob? = null
        get
        private set

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
        val commodityDao = MySqlCommodityDaoImpl(db.dataSource)
        val industryProductionCommodityDao = MySqlIndustryProductionCommodityDaoImpl(db.dataSource)
        val  productionDao = MySqlProductionDaoImpl(db.dataSource, industryProductionCommodityDao)
        val  industryCommodityDao = MySqlIndustryCommodityDaoImpl(db.dataSource)
        val  industryDao = MySqlIndustryDaoImpl(db.dataSource, productionDao, industryCommodityDao)
        truckerJobDao  =  MySqlTruckerJobImpl(db.dataSource, eventManagerNode)

        truckerJob = TruckerJobImpl(JobPlugin.JobId.Trucker.id, eventManager)
        registerCommands()
    }

    private fun registerCommands() {
        playerCommandManager.registerCommands(GeneralTruckerCommands(this))
        playerCommandManager.registerChildGroup(TruckerCargoGroupCommands(), "cargo")
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL)
    }

}
