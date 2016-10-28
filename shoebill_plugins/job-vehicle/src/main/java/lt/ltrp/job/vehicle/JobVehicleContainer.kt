package lt.ltrp.job.vehicle

import lt.ltrp.job.`object`.JobVehicle

/**
 * Created by Bebras on 2016-10-25.
 */
object JobVehicleContainer {

    private var vehicles = mutableListOf<JobVehicle>()

    fun get(): List<JobVehicle> {
        return vehicles
    }

    fun add(jobVehicle: JobVehicle) {
        vehicles.add(jobVehicle)
    }

    fun remove(jobVehicle: JobVehicle) {
        vehicles.remove(jobVehicle)
    }
}