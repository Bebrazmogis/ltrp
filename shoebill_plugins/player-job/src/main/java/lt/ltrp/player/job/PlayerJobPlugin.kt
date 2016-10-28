package lt.ltrp.player.job

import lt.ltrp.DatabasePlugin
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.event.PaydayEvent
import lt.ltrp.event.player.PlayerLogInEvent
import lt.ltrp.player.PlayerController
import lt.ltrp.player.job.dao.PlayerJobDao
import lt.ltrp.player.job.dao.impl.MySqlPlayerJobDaoImpl
import lt.ltrp.resource.DependentPlugin
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2016-10-23.
 */
class PlayerJobPlugin: DependentPlugin() {

    private lateinit var eventManager: EventManagerNode
    private lateinit var jobDao: PlayerJobDao
    private lateinit var jobController: PlayerJobController

    init {
        addDependency(DatabasePlugin::class)
    }

    override fun onEnable() {
        super.onEnable()
        eventManager = getEventManager().createChildNode()

    }

    override fun onDisable() {
        super.onDisable()
    }

    override fun onDependenciesLoaded() {
        jobDao = MySqlPlayerJobDaoImpl(DatabasePlugin.get(DatabasePlugin::class.java).dataSource)
        jobController = PlayerJobControllerImpl(jobDao, eventManager)
        addEventHandlers()
        PlayerController.instance.getPlayers().forEach { loadJob(it) }
    }

    private fun addEventHandlers() {
        eventManager.registerHandler(PlayerLogInEvent::class.java, { loadJob(it.player) })
        eventManager.registerHandler(PaydayEvent::class.java, { onPayDay() })
    }


    private fun loadJob(player: LtrpPlayer) {
        Thread({
            val data = jobDao.get(player)
            player.jobData = data
        }).start()
    }

    private fun onPayDay() {
        PlayerController.instance.getPlayers()
            .filter { it.jobData != null }
            .forEach { p ->
                val jobData = p.jobData
                if(jobData != null) {
                    if(jobData.remainingContract > 0)
                        jobData.remainingContract--
                }
            }
    }
}