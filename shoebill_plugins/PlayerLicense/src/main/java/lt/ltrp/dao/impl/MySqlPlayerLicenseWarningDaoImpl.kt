package lt.ltrp.dao.impl

import lt.ltrp.player.licenses.dao.PlayerLicenseWarningDao
import lt.ltrp.player.licenses.data.LicenseWarning
import lt.ltrp.player.licenses.data.PlayerLicense
import javax.sql.DataSource

/**
 * Created by Bebras on 2016-10-08.
 */
class MySqlPlayerLicenseWarningDaoImpl(var dataSource: DataSource): PlayerLicenseWarningDao {

    override fun get(uuid: Int): LicenseWarning? {

    }

    override fun getByLicense(playerLicense: PlayerLicense): Set<LicenseWarning> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(licenseWarning: LicenseWarning): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(licenseWarning: LicenseWarning) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(licenseWarning: LicenseWarning) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}