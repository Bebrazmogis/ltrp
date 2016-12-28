package lt.ltrp.stats

import net.gtaun.shoebill.entities.Player

/**
 * Created by Bebras on 2016-12-22.
 * Represents a stat object for a player
 */
interface PlayerStats {

    val player: Player

    fun show(player: Player)
    fun show()

}
