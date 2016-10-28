package lt.ltrp.player.settings

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.player.settings.dao.PlayerSettingsDao
import lt.ltrp.player.settings.data.PlayerSettings
import lt.ltrp.player.settings.event.PlayerEditSettingsEvent
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-15.
 * Provides implementation for [PlayerSettingsController] interface
 */
class PlayerSettingsControllerImpl(private val settingsDao: PlayerSettingsDao, private val eventManager: EventManager):
        PlayerSettingsController() {


    override fun update(settings: PlayerSettings) {
        settingsDao.update(settings)
        eventManager.dispatchEvent(PlayerEditSettingsEvent(settings.player as? LtrpPlayer, settings))
    }

}