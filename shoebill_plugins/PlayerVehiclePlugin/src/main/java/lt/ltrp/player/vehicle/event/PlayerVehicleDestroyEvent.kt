package lt.ltrp.player.vehicle.event

import lt.ltrp.player.vehicle.`object`.PlayerVehicle
import lt.ltrp.vehicle.event.VehicleEvent

/**
 * @author Bebras
 * *         2016.05.31.
 */
class PlayerVehicleDestroyEvent(override val vehicle: PlayerVehicle) : VehicleEvent(vehicle) {

}
