package lt.ltrp.stats

import lt.ltrp.BankPlugin
import lt.ltrp.SpawnPlugin
import lt.ltrp.player.job.PlayerJobPlugin
import lt.ltrp.resource.DependentPlugin
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2016-12-22.
 *
 */
class PlayerStatsPlugin() : DependentPlugin() {

    private lateinit var eventManagerNode: EventManagerNode
    private lateinit var spawnPlugin: SpawnPlugin
    private lateinit var bankPlugin: BankPlugin
    private lateinit var playerJobPlugin: PlayerJobPlugin

    init {
        addDependency(SpawnPlugin::class)
        addDependency(BankPlugin::class)
        addDependency(PlayerJobPlugin::class)
    }

    override fun onEnable() {
        eventManagerNode = eventManager.createChildNode()
    }

    override fun onDependenciesLoaded() {
        val rm = ResourceManager.get()
        spawnPlugin = rm.getPlugin(SpawnPlugin::class.java) as SpawnPlugin
        bankPlugin = rm.getPlugin(BankPlugin::class.java) as BankPlugin
        playerJobPlugin = rm.getPlugin(PlayerJobPlugin::class.java) as PlayerJobPlugin

    }

    override fun onDisable() {
        eventManagerNode.cancelAll()
    }


    fun showStats(p: Player, toPlayer: Player, gui: Boolean = false) {
        val stats = PlayerStatFactory.create(p, gui, eventManagerNode, spawnPlugin, bankPlugin, playerJobPlugin)
        // TODO log
        stats.show(toPlayer)
    }
}