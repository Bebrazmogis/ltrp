package lt.ltrp.player.dao

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.`object`.PlayerData

/**
 * Created by Bebras on 2016-10-07.
 */
interface PlayerDao {

    fun get(name: String): PlayerData?
    fun get(uuid: Int): PlayerData
    fun update(playerData: PlayerData)


    fun updateLastLogin(player: LtrpPlayer)
    fun getUsername(uuid: Int): String
}