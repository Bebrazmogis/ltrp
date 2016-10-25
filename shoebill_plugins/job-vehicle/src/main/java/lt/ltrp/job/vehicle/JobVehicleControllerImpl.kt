package lt.ltrp.job.vehicle

import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.job.JobVehicleController
import lt.ltrp.job.`object`.Job
import lt.ltrp.job.`object`.JobRank
import lt.ltrp.job.`object`.JobVehicle
import lt.ltrp.job.dao.JobVehicleDao
import lt.ltrp.job.vehicle.`object`.impl.JobVehicleImpl
import net.gtaun.shoebill.`object`.Vehicle
import net.gtaun.shoebill.data.AngledLocation
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-25.
 * [JobVehicleController] implementation
 */
class JobVehicleControllerImpl(private val vehicleDao: JobVehicleDao, private val eventManager: EventManager):
        JobVehicleController() {


    override fun create(uuid: Int, job: Job, modelId: Int, location: AngledLocation, color1: Int, color2: Int, requiredRank: JobRank, license: String, mileage: Float): JobVehicle {
        val vehicle = JobVehicleImpl(uuid, job, modelId, location, color1, color2, requiredRank, license, mileage, eventManager)
        vehicleDao.insert(vehicle)
        JobVehicleContainer.add(vehicle)
        return vehicle
    }

    override fun update(jobVehicle: JobVehicle) {
        vehicleDao.update(jobVehicle)
    }

    override fun remove(jobVehicle: JobVehicle) {
        jobVehicle.destroy()
        vehicleDao.delete(jobVehicle)
        JobVehicleContainer.remove(jobVehicle)
    }

    override fun get(): List<JobVehicle> {
        return JobVehicleContainer.get()
    }

    override fun get(uuid: Int): JobVehicle? {
        return JobVehicleContainer.get().firstOrNull{ it.UUID == uuid }
    }

    override fun get(vehicle: LtrpVehicle): JobVehicle? {
        return JobVehicleContainer.get().firstOrNull{ it.primitive == vehicle.primitive }
    }

    override fun get(vehicle: Vehicle): JobVehicle? {
        return JobVehicleContainer.get().firstOrNull{ it.primitive == vehicle }
    }
}