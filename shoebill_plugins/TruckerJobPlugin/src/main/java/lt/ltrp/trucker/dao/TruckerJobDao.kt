 package lt.ltrp.trucker.dao

 import lt.ltrp.dao.JobDao
 import lt.ltrp.trucker.`object`.TruckerJob

 /**
 * @author Bebras
 * 2016.06.19.
 */
interface TruckerJobDao: JobDao {

     override fun get(uuid: Int): TruckerJob
 }