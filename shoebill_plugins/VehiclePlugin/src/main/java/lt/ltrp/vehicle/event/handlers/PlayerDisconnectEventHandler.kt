package lt.ltrp.vehicle.event.handlers

import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.Vehicle
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent
import net.gtaun.util.event.EventHandler

/**
 * Created by Bebras on 2016-12-29.
 *
 */
class PlayerDisconnectEventHandler(private val lastUsedVehicles : MutableMap<Player, LtrpVehicle>):
        EventHandler<PlayerDisconnectEvent> {

    override fun handleEvent(event: PlayerDisconnectEvent) {
        val player = event.player

        lastUsedVehicles.remove(player)
    }
}