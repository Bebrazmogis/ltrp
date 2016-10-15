package lt.ltrp.player.settings

import lt.ltrp.player.settings.dao.PlayerSettingsDao
import lt.ltrp.player.settings.data.PlayerSettings

/**
 * Created by Bebras on 2016-10-15.
 * Provides implementation for [PlayerSettingsController] interface
 */
class PlayerSettingsControllerImpl(private val settingsDao: PlayerSettingsDao): PlayerSettingsController() {


    override fun update(settings: PlayerSettings) {
        settingsDao.update(settings)
    }

}