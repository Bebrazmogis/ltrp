package lt.ltrp.vehicle.speedometer.event


import lt.ltrp.event.player.PlayerEvent
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.`object`.LtrpVehicle

/**
 * @author Bebras
 * *         2015.12.14.
 */
class SpeedometerTickEvent(player: LtrpPlayer, var vehicle: LtrpVehicle?, var speed: Float) : PlayerEvent(player) {

    val player: LtrpPlayer
        get() = super.getPlayer() as LtrpPlayer
}
