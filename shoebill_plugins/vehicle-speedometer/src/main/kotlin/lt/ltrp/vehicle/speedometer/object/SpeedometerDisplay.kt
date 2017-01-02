package lt.ltrp.vehicle.speedometer.`object`

import lt.ltrp.vehicle.data.FuelTank
import net.gtaun.shoebill.entities.Destroyable
import net.gtaun.shoebill.entities.Player

/**
 * Created by Bebras on 2017-01-02.
 * A display for a speedometer
 */
interface SpeedometerDisplay: Destroyable {

    val player: Player

    fun show()
    fun hide()

    fun updateSpeed(speed: Float)
    fun updateMileage(mileage: Float)
    fun updateFuel(fuelTank: FuelTank)

}