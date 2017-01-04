package lt.ltrp.vehicle.event.handlers

import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.entities.VehicleParam
import net.gtaun.shoebill.event.vehicle.VehicleUpdateDamageEvent
import net.gtaun.util.event.EventHandler

/**
 * Created by Bebras on 2016-12-29.
 *
 */
class VehicleUpdateDamageEventHandler : EventHandler<VehicleUpdateDamageEvent> {

    override fun handleEvent(event: VehicleUpdateDamageEvent) {
        val vehicle = event.vehicle
        val ltrpVehicle = LtrpVehicle.getByVehicle(vehicle)
        if(vehicle != null && ltrpVehicle != null && vehicle.health < 300f) {
            vehicle.state.engine = VehicleParam.PARAM_OFF
            ltrpVehicle.sendStateMessage("Automobilio variklis iðsijungia")
        }
    }
}