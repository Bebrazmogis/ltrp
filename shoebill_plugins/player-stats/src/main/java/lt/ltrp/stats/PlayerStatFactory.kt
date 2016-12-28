package lt.ltrp.stats

import lt.ltrp.BankPlugin
import lt.ltrp.SpawnPlugin
import lt.ltrp.player.job.PlayerJobPlugin
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-12-22.
 * A factory for creating user stats
 */
object PlayerStatFactory {

    fun create(p: Player, gui: Boolean, eventManager: EventManager, spawnPlugin: SpawnPlugin, bankPlugin: BankPlugin,
               playerJobPlugin: PlayerJobPlugin):
            PlayerStats {
        if(gui) {
            return PlayerStatsDialog(p, eventManager, spawnPlugin, bankPlugin, playerJobPlugin)
        } else {
            return PlayerStatsText(p, eventManager, spawnPlugin, bankPlugin, playerJobPlugin)
        }
    }
}

