package lt.ltrp.dao.impl

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.player.licenses.PlayerLicenseController
import lt.ltrp.player.licenses.constant.LicenseType
import lt.ltrp.player.licenses.dao.PlayerLicenseDao
import lt.ltrp.player.licenses.dao.PlayerLicenseWarningDao
import lt.ltrp.player.licenses.data.LicenseWarning
import lt.ltrp.player.licenses.data.PlayerLicense
import java.time.LocalDateTime

/**
 * Created by Bebras on 2016-10-15.
 * A concrete implementation of [PlayerLicenseController]
 */
class PlayerLicenseControllerImpl(private val licenseDao: PlayerLicenseDao,
                                  private val licenseWarningDao: PlayerLicenseWarningDao):
        PlayerLicenseController() {

    override fun insert(player: LtrpPlayer, type: LicenseType, stage: Int) {
        val license = PlayerLicense(type, stage, player)
        val id = licenseDao.insert(license)
        license.id = id
        player.licenses.add(license)
    }

    override fun update(license: PlayerLicense) {
        licenseDao.update(license)
    }

    override fun remove(license: PlayerLicense) {
        licenseDao.remove(license)
        license.player.licenses.remove(license)
    }

    override fun insertWarning(license: PlayerLicense, body: String, issuedBy: LtrpPlayer) {
        val warning = LicenseWarning()
        warning.body = body
        warning.issuedBy = issuedBy.name
        warning.date = LocalDateTime.now()
        warning.license = license
        val id = licenseWarningDao.insert(warning)
        warning.id = id
    }

}