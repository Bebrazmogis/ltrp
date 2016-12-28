package lt.ltrp.stats;

import lt.ltrp.BankPlugin
import lt.ltrp.SpawnPlugin
import lt.ltrp.player.job.PlayerJobPlugin
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-12-22.
 */
class PlayerStatsDialog(override val player: Player,
                        eventManager: EventManager,
                        spawnPlugin: SpawnPlugin,
                        bankPlugin: BankPlugin,
                        playerJobPlugin: PlayerJobPlugin) :
        PlayerStats {

    override fun show(player: Player) {

    }

    override fun show() {
    }

}