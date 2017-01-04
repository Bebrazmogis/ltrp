package lt.ltrp.vehicle.event.handlers

import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.event.VehicleDestroyEvent
import net.gtaun.shoebill.entities.Timer
import net.gtaun.util.event.EventHandler

/**
 * Created by Bebras on 2016-12-29.

 */
internal class VehicleDestroyEventHandler(private val engineTimers: MutableMap<LtrpVehicle, Timer>):
        EventHandler<VehicleDestroyEvent> {

    override fun handleEvent(event: VehicleDestroyEvent) {
        val vehicle = event.vehicle

        engineTimers[vehicle]?.destroy()
        engineTimers.remove(vehicle)
    }
}