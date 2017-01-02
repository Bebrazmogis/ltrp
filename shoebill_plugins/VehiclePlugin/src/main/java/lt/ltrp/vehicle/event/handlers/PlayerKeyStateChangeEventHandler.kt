package lt.ltrp.vehicle.event.handlers

import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.constant.PlayerKey
import net.gtaun.shoebill.constant.PlayerState
import net.gtaun.shoebill.entities.VehicleParam
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent
import net.gtaun.util.event.EventHandler

/**
 * Created by Bebras on 2016-12-29.
 *
 */
internal class PlayerKeyStateChangeEventHandler: EventHandler<PlayerKeyStateChangeEvent> {

    override fun handleEvent(event: PlayerKeyStateChangeEvent) {
        val player = event.player
        val ltrpVehicle = player.vehicle?.let { LtrpVehicle.getByVehicle(it) }
        val vehicle = ltrpVehicle?.vehicle

        if(vehicle != null && player.keyState.isKeyPressed(PlayerKey.ACTION) && player.state == PlayerState.DRIVER) {
            when(vehicle.state.lights) {
                VehicleParam.PARAM_ON -> vehicle.state.lights = VehicleParam.PARAM_OFF
                else -> vehicle.state.lights = VehicleParam.PARAM_ON

            }
        }
    }

}