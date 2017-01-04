package lt.ltrp.vehicle.event


import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.util.event.Event

/**
 * @author Bebras
 * *         2016.04.19.
 * Base event for [LtrpVehicle]
 */
open class VehicleEvent(open val vehicle: LtrpVehicle) : Event() {

}
