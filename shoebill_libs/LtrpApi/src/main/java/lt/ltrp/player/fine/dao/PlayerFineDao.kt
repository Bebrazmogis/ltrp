package lt.ltrp.player.fine.dao

import lt.ltrp.`object`.PlayerData
import lt.ltrp.player.fine.data.PlayerFine

/**
 * Created by Bebras on 2016-10-28.
 * CRUD methods for managing [PlayerFine] objects
 */
interface PlayerFineDao {

    fun get(playerData: PlayerData): Collection<PlayerFine>
    fun insert(playerFine: PlayerFine): Int
    fun update(playerFine: PlayerFine)
    fun remove(playerFine: PlayerFine)

}