package lt.ltrp.player.licenses

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.licenses.constant.LicenseType
import lt.ltrp.player.licenses.data.PlayerLicense

/**
 * Created by Bebras on 2016-10-15.
 * Methods for interacting with player licenses
 */
abstract class PlayerLicenseController protected constructor(){

    init {
        instance = this
    }

    abstract fun insert(player: LtrpPlayer, type: LicenseType, stage: Int = 1)
    abstract fun update(license: PlayerLicense)
    abstract fun remove(license: PlayerLicense)
    abstract fun insertWarning(license: PlayerLicense, body: String, issuedBy: LtrpPlayer)

    companion object {
        lateinit var instance: PlayerLicenseController
    }

}