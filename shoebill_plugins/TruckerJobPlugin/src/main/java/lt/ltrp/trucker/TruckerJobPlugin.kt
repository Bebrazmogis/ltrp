package lt.ltrp.trucker;

import lt.ltrp.DatabasePlugin
import lt.ltrp.JobPlugin
import lt.ltrp.trucker.`object`.TruckerJob
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
class TruckerJobPlugin: Plugin() {

    private val eventManagerNode: EventManagerNode = eventManager.createChildNode()
    private val playerCommandManager = PlayerCommandManager(eventManagerNode);
    private var truckerJobDao: TruckerJobDao? = null;
    var truckerJob: TruckerJob? = null
        get
        private set

    override fun onEnable() {

        var dependencies = ArrayList<Class<out Plugin>>()
        dependencies.add(DatabasePlugin::class.java)
        dependencies.add(JobPlugin::class.java)
        var missing = 0;
        dependencies.forEach {
            if(ResourceManager.get().getPlugin(it) == null) {
                missing++
            } else
                dependencies.remove(it)
        }
        if(missing > 0) {
            eventManagerNode.registerHandler(ResourceEnableEvent::class.java, {
                val r = it.resource
                if(r is Plugin && dependencies.contains(r.javaClass)) {
                    dependencies.remove(r.javaClass)
                    if(dependencies.size == 0)
                        load()
                }
            })
        } else load();
    }

    override fun onDisable() {
        eventManagerNode.cancelAll();
        playerCommandManager.destroy();
    }

    private fun load() {
        eventManagerNode.cancelAll();
        val db = ResourceManager.get().getPlugin(DatabasePlugin::class.java);
        val commodityDao = MySqlCommodityDaoImpl(db.dataSource);
        val industryProductionCommodityDao = MySqlIndustryProductionCommodityDaoImpl(db.dataSource);
        val  productionDao = MySqlProductionDaoImpl(db.dataSource, industryProductionCommodityDao);
        val  industryCommodityDao = MySqlIndustryCommodityDaoImpl(db.dataSource);
        val  industryDao = MySqlIndustryDaoImpl(db.dataSource, productionDao, industryCommodityDao);
        truckerJobDao  =  MySqlTruckerJobImpl(db.dataSource, eventManagerNode);
        truckerJob = (truckerJobDao as MySqlTruckerJobImpl).get(JobPlugin.JobId.Trucker.id)
        registerCommands();
    }

    private fun registerCommands() {
        playerCommandManager.registerCommands(GeneralTruckerCommands(this));
        playerCommandManager.registerChildGroup(TruckerCargoGroupCommands(), "cargo");
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
    }

}
