package lt.ltrp.player.licenses.dao

import lt.ltrp.player.licenses.data.LicenseWarning
import lt.ltrp.player.licenses.data.PlayerLicense

/**
 * Created by Bebras on 2016-10-08.
 */
interface PlayerLicenseWarningDao {

    fun get(uuid: Int): LicenseWarning?
    fun getByLicense(playerLicense: PlayerLicense): Set<LicenseWarning>
    fun update(licenseWarning: LicenseWarning)
    fun insert(licenseWarning: LicenseWarning): Int
    fun remove(licenseWarning: LicenseWarning)

}