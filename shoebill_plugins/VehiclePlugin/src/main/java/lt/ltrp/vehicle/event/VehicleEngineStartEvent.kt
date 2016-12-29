package lt.ltrp.vehicle.event


import lt.ltrp.Deniable
import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.entities.Player

/**
 * @author Bebras
 * *         2015.12.18.
 */
class VehicleEngineStartEvent(vehicle: LtrpVehicle, val player: Player, val isSuccess: Boolean) : VehicleEvent(vehicle), Deniable {
    private var denied: Boolean = false


    override fun deny() {
        denied = true
    }

    override fun isDenied(): Boolean {
        return denied
    }
}
