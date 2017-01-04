package lt.ltrp.player.settings

import lt.ltrp.player.settings.data.PlayerSettings

/**
 * Created by Bebras on 2016-10-15.
 * A set of methods available for managing player settings
 */
abstract class PlayerSettingsController protected constructor() {

    init {
        instance = this
    }

    abstract fun update(settings: PlayerSettings)



    companion object {
        lateinit var instance: PlayerSettingsController
    }

}