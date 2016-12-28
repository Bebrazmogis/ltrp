package lt.ltrp.job

import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.job.`object`.Job
import lt.ltrp.job.`object`.JobRank
import lt.ltrp.job.`object`.JobVehicle
import net.gtaun.shoebill.data.AngledLocation
import net.gtaun.shoebill.entities.Vehicle

/**
 * Created by Bebras on 2016-10-25.
 * A set of methods for interacting with the job-vehicle module
 */
abstract class JobVehicleController protected constructor() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: JobVehicleController
    }


    abstract fun create(uuid: Int, job: Job, modelId: Int, location: AngledLocation, color1: Int, color2: Int, requiredRank: JobRank, license: String, mileage: Float): JobVehicle
    abstract fun create(job: Job, modelId: Int, location: AngledLocation, color1: Int, color2: Int): JobVehicle
    abstract fun create(job: Job, modelId: Int, location: AngledLocation, color1: Int, color2: Int, requiredRank: JobRank): JobVehicle

    abstract fun update(jobVehicle: JobVehicle)
    abstract fun remove(jobVehicle: JobVehicle)

    abstract fun get(): List<JobVehicle>
    abstract fun get(uuid: Int): JobVehicle?
    abstract fun get(vehicle: LtrpVehicle): JobVehicle?
    abstract fun get(vehicle: Vehicle): JobVehicle?
}