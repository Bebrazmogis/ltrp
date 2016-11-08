package lt.ltrp.player.settings.dao

import lt.ltrp.`object`.PlayerData
import lt.ltrp.player.settings.data.PlayerSettings

/**
 * Created by Bebras on 2016-10-07.
 * Methods for interacting with the database
 */
interface PlayerSettingsDao {

    fun update(settings: PlayerSettings)
    fun get(player: PlayerData): PlayerSettings?
    fun insert(settings: PlayerSettings)
}