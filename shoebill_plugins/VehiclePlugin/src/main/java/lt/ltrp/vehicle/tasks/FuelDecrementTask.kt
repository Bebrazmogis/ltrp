package lt.ltrp.vehicle.tasks

import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.dao.VehicleFuelConsumptionDao
import lt.ltrp.vehicle.event.VehicleEngineKillEvent
import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.entities.VehicleParam
import net.gtaun.util.event.EventManager
import java.util.*

/**
 * Created by Bebras on 2016-12-29.
 * Task that decrements vehicle fuel in all vehicles with turned on engines
 */
class FuelDecrementTask(private val vehicleFuelConsumptionDao: VehicleFuelConsumptionDao,
                        private val eventManager: EventManager): TimerTask() {

    override fun run() {
        val filtered = LtrpVehicle.vehicles
                .filter { it.isSpawned && it.vehicle!!.state.engine == VehicleParam.PARAM_ON }

        Shoebill.get().runOnMainThread {
            filtered.forEach {
                val fuelTank = it.fuelTank
                fuelTank.fuel -= vehicleFuelConsumptionDao.get(it.vehicle!!.modelId)
                if(fuelTank.fuel < 0) fuelTank.fuel = 0f
                if(fuelTank.fuel == 0f) {
                    val event = VehicleEngineKillEvent(it, null)
                    eventManager.dispatchEvent(event, it)
                    if(!event.isDenied) {
                        it.vehicle!!.state.engine = VehicleParam.PARAM_OFF
                    }
                }
            }
        }
    }
}