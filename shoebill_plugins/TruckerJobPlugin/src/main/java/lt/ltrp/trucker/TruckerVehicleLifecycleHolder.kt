package lt.ltrp.trucker

import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.trucker.`object`.TruckerVehicle
import net.gtaun.shoebill.common.vehicle.VehicleLifecycleHolder
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-30.
 * This class manages [TruckerVehicle] objects
 */
class TruckerVehicleLifecycleHolder(eventManager: EventManager): VehicleLifecycleHolder(eventManager) {

    init {
        registerClass(TruckerVehicle::class.java)
    }

    fun getObject(vehicle: LtrpVehicle): TruckerVehicle? {
        return super.getObject(vehicle, TruckerVehicle::class.java)
    }

    fun getObjects(): Collection<TruckerVehicle> {
        return super.getObjects(TruckerVehicle::class.java)
    }
}