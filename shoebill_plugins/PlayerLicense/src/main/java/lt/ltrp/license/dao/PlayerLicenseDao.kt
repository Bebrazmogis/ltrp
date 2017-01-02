package lt.ltrp.player.licenses.dao

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.licenses.data.PlayerLicense
import lt.ltrp.player.licenses.data.PlayerLicenses

/**
 * Created by Bebras on 2016-10-08.
 */
interface PlayerLicenseDao {

    fun insert(license: PlayerLicense): Int
    fun getAll(player: LtrpPlayer): PlayerLicenses
    fun remove(playerLicense: PlayerLicense)
    fun update(playerLicense: PlayerLicense)


}