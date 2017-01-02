package lt.ltrp.vehicle.speedometer.event

import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.event.VehicleEvent

/**
 * Created by Bebras on 2017-01-02.
 * Base event for speedometer related events
 */
abstract class SpeedometerEvent(vehicle: LtrpVehicle): VehicleEvent(vehicle) {
}