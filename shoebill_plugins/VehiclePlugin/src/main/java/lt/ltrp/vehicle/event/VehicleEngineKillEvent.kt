package lt.ltrp.vehicle.event


import lt.ltrp.Deniable
import lt.ltrp.vehicle.`object`.LtrpVehicle
import net.gtaun.shoebill.entities.Player

/**
 * @author Bebras
 * *         2016.02.14.
 */
class VehicleEngineKillEvent(vehicle: LtrpVehicle, val player: Player?) : VehicleEvent(vehicle), Deniable {
    private var denied: Boolean = false

    init {
        this.denied = false
    }

    override fun deny() {
        this.denied = true
    }

    override fun isDenied(): Boolean {
        return denied
    }
}
