package lt.ltrp.vehicle.event

import lt.ltrp.vehicle.`object`.LtrpVehicle

/**
 * Created by Bebras on 2017-01-02.
 * Dispatched whenever a spawned instance of [LtrpVehicle] is created
 */
class VehicleCreateEvent(vehicle: LtrpVehicle): VehicleEvent(vehicle) {
}