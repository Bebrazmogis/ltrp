package lt.ltrp.trucker.dao.impl

import lt.ltrp.dao.impl.MySqlJobDaoImpl
import lt.ltrp.trucker.`object`.TruckerJob
import lt.ltrp.trucker.`object`.impl.TruckerJobImpl
import lt.ltrp.trucker.dao.TruckerJobDao
import net.gtaun.util.event.EventManager
import javax.sql.DataSource

/**
 * @author Bebras
 * 2016.06.19.
 */
class MySqlTruckerJobImpl(ds: DataSource, eventManager: EventManager): TruckerJobDao, MySqlJobDaoImpl(ds, null, null, eventManager) {


    override fun get(uuid: Int): TruckerJob {
        val job = TruckerJobImpl(eventManager)
        super.load(job)
        return job
    }
}