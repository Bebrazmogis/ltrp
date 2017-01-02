package lt.ltrp.vehicle.speedometer.event

import lt.ltrp.vehicle.`object`.LtrpVehicle


/**
 * @author Bebras
 * *         2015.12.14.
 * Dispatched whenever a speedometer is updated
 * **This event is dispatched frequently**
 */
class SpeedometerTickEvent(vehicle: LtrpVehicle, val speed: Float) : SpeedometerEvent(vehicle) {

}
