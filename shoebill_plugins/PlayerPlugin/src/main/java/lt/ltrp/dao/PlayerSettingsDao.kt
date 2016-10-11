package lt.ltrp.dao

import lt.ltrp.data.PlayerSettings

/**
 * Created by Bebras on 2016-10-07.
 */
interface PlayerSettingsDao {

    fun update(settings: PlayerSettings)
}