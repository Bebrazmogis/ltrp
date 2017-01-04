package lt.ltrp.vehicle.speedometer.event

import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.entities.Player

/**
 * Created by Bebras on 2017-01-02.
 * Dispatched whenever a speedometer is hidden for a [Player]
 */
class SpeedometerHideEvent(ltrpVehicle: LtrpVehicle,val player: Player):
    SpeedometerEvent(ltrpVehicle) {
}