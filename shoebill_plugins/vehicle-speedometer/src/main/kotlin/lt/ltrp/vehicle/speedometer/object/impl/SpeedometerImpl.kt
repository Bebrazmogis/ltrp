package lt.ltrp.vehicle.speedometer.`object`.impl

import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.speedometer.`object`.Speedometer
import lt.ltrp.vehicle.speedometer.`object`.SpeedometerDisplay
import lt.ltrp.vehicle.speedometer.event.SpeedometerHideEvent
import lt.ltrp.vehicle.speedometer.event.SpeedometerShowEvent
import lt.ltrp.vehicle.speedometer.event.SpeedometerTickEvent
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2017-01-02.
 * A basic [Speedometer] implementation
 */
class SpeedometerImpl(override val vehicle: LtrpVehicle,
                      private val eventManager: EventManager):
        Speedometer {

    private val playerDisplays = mutableMapOf<Player, SpeedometerDisplay>()

    override fun show(player: Player) {
        show(player, LtrpClassicSpeedometerDisplay(player))
    }

    override fun show(player: Player, speedometerDisplay: SpeedometerDisplay) {
        playerDisplays.put(player, speedometerDisplay)
        eventManager.dispatchEvent(SpeedometerShowEvent(vehicle, player))
    }

    override fun hide(player: Player) {
        playerDisplays.remove(player)
        eventManager.dispatchEvent(SpeedometerHideEvent(vehicle, player))
    }

    override fun hide() {
        playerDisplays.keys.forEach { hide(it) }
    }

    override fun isShown(player: Player): Boolean {
        return playerDisplays.containsKey(player)
    }

    override fun update() {
        val fuel = vehicle.fuelTank
        val speed = vehicle.speed
        val mileage = vehicle.mileage
        for((player, display) in playerDisplays) {
            display.updateFuel(fuel)
            display.updateSpeed(speed)
            display.updateMileage(mileage)
        }
        eventManager.dispatchEvent(SpeedometerTickEvent(vehicle, speed))
    }

}