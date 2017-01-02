package lt.ltrp.vehicle.event.handlers

import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.Timer
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent
import net.gtaun.util.event.EventHandler

/**
 * Created by Bebras on 2016-12-29.

 */
class VehicleDeathEventHandler(private val lastUsedVehicles: MutableMap<Player, LtrpVehicle>,
                               private val engineTimers: MutableMap<LtrpVehicle, Timer>):
        EventHandler<VehicleDeathEvent> {

    override fun handleEvent(event: VehicleDeathEvent) {
        val vehicle = event.vehicle
        val ltrpVehicle = LtrpVehicle.getByVehicle(vehicle)
        if(ltrpVehicle != null) {
            for((p, v) in lastUsedVehicles) {
                if(v == ltrpVehicle) {
                    lastUsedVehicles.remove(p)
                    break
                }
            }

            engineTimers.remove(ltrpVehicle)
        }
    }
}