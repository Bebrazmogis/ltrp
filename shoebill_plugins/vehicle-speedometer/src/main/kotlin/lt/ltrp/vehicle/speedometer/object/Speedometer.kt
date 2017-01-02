package lt.ltrp.vehicle.speedometer.`object`

import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.entities.Player

/**
 * Created by Bebras on 2017-01-02.
 * This interfaces defines an in-game vehicle speedometer
 * Each vehicle should have a speedometer
 * A speedometer might have multiple displays
 */
interface Speedometer {

    val vehicle: LtrpVehicle

    fun show(player: Player)
    fun show(player: Player, speedometerDisplay: SpeedometerDisplay)

    fun hide(player: Player)
    fun hide()

    fun isShown(player: Player): Boolean
    fun update()
}