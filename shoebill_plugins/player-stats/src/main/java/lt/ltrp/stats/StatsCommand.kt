package lt.ltrp.stats

import lt.ltrp.`object`.LtrpPlayer
import net.gtaun.shoebill.common.command.*
import net.gtaun.shoebill.entities.Player

/**
 * Created by Bebras on 2016-12-22.
 */
class StatsCommand(private val statPlugin: PlayerStatsPlugin) : CommandGroup() {


    init {
        setNotFoundHandler { player, commandGroup, command ->
            text(player)
        }
    }

    @BeforeCheck
    fun bc(p: Player, cmd: String, params: String): Boolean {
        val player = LtrpPlayer.get(p)
        return player != null && player.isLoggedIn
    }

    @Command
    fun text(p: Player): Boolean {
        statPlugin.showStats(p, p, false)
        return true
    }

    @Command
    @CommandHelp("Parodo þaidëjo informacijà GUI formatu")
    fun gui(p: Player): Boolean {
        statPlugin.showStats(p, p, true)
        return true
    }
}