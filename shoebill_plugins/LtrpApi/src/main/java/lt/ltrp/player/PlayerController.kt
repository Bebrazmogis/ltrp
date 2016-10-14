package lt.ltrp.player

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.PlayerData
import net.gtaun.shoebill.`object`.Destroyable
import net.gtaun.shoebill.data.Location

/**
 * @author Bebras
 *         2016.04.08.
 */
abstract class PlayerController protected constructor(): Destroyable {

    init {
        instance = this
    }

    companion object {

        lateinit var instance: PlayerController

        fun get(): PlayerController {
            return instance
        }

        val MINUTES_FOR_PAYDAY = 20
        val GYM_LOCATION = Location()
    }

    abstract fun getPlayers(): Collection<LtrpPlayer>
    abstract fun getUsernameByUUID(uuid: Int): String?
    abstract fun getData(uuid: Int): PlayerData?
    abstract fun update(player: LtrpPlayer)


}
