package lt.ltrp.vehicle.event.handlers

import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.constant.PlayerState
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent
import net.gtaun.util.event.EventHandler

/**
 * Created by Bebras on 2016-12-29.
 *
 */
class PlayerStateChangeEventHandler(private val lastUsedVehicles: MutableMap<Player, LtrpVehicle>) :
        EventHandler<PlayerStateChangeEvent> {
    override fun handleEvent(event: PlayerStateChangeEvent) {
        val player = event.player
        val vehicle = player.vehicle
        val ltrpVehicle = vehicle?.let { LtrpVehicle.getByVehicle(it) }
        val newState = player.state
        val oldState = event.oldState


        if(vehicle != null && ltrpVehicle != null) {
            if(newState == PlayerState.DRIVER) {
                ltrpVehicle.driver = player
                lastUsedVehicles[player] = ltrpVehicle
            } else if(oldState == PlayerState.DRIVER) {
                lastUsedVehicles[player]?.driver = null
            }
        }
        // TODO move to player module
        /*if(newState == PlayerState.ONFOOT) {
            if(player.isSeatbelt()) {
                player.sendActionMessage("iðlipdamas atsisega saugos dirþus");
                player.setSeatbelt(false);
            }
        }*/

    }
}