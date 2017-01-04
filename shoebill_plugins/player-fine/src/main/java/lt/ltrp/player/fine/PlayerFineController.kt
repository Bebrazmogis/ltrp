package lt.ltrp.player.fine

import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.player.fine.data.PlayerFine

/**
 * Created by Bebras on 2016-10-28.
 * A set of methods to interact with player fines
 */
abstract class PlayerFineController protected constructor() {

    init {
        instance = this
    }


    abstract fun get(playerData: PlayerData): Collection<PlayerFine>
    abstract fun create(fine: PlayerFine)
    abstract fun update(fine: PlayerFine)

    companion object {
        private lateinit var instance: PlayerFineController

        fun get(): PlayerFineController {
            return instance
        }
    }
}