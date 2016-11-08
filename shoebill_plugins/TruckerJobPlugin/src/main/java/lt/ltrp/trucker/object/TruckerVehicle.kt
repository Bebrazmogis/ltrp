package lt.ltrp.trucker.`object`

import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.trucker.constant.TruckerCargoType
import lt.ltrp.trucker.constant.TruckerVehicleConstants
import lt.ltrp.trucker.controller.TruckerVehicleCargoController
import lt.ltrp.trucker.data.Cargo
import lt.ltrp.trucker.data.VehicleCargo
import net.gtaun.shoebill.`object`.Timer
import net.gtaun.shoebill.`object`.VehicleParam
import net.gtaun.shoebill.common.vehicle.VehicleLifecycleObject
import net.gtaun.shoebill.constant.VehicleModel
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-30.
 *
 */
class TruckerVehicle(eventManager: EventManager, vehicle: LtrpVehicle): VehicleLifecycleObject(eventManager, vehicle) {

    private var loadingTimer: Timer? = null

    var cargoList: MutableList<VehicleCargo> = mutableListOf()
        private set

    var isBeingLoaded: Boolean = false
        private set

    override fun onInit() {

    }

    override fun onDestroy() {
        loadingTimer?.destroy()
    }

    override fun getVehicle(): LtrpVehicle {
        return super.getVehicle() as LtrpVehicle
    }

    fun isFull(): Boolean {
        getCompatibleTypes().forEach {
            if(isFull(it))
                return true
        }
        return false
    }

    fun isFull(type: TruckerCargoType): Boolean {
        val constants = TruckerVehicleConstants.getByModelId(getVehicle().modelId)
        if(constants != null) {
            val data = constants.vehicleData.firstOrNull { it.cargoType == type }
            val cargo = cargoList.firstOrNull { it.commodity.type == type }
            if(data != null && cargo != null) {
                return data.limit.toInt() == cargo.amount
            }
        }
        return false
    }

    fun isCompatibleCargo(type: TruckerCargoType): Boolean {
        return getCompatibleTypes().contains(type)
    }

    fun getCompatibleTypes(): Collection<TruckerCargoType> {
        val constants = TruckerVehicleConstants.getByModelId(getVehicle().modelId)
        if(constants != null) {
            return constants.vehicleData.map { it.cargoType }
        }
        return setOf()
    }

    fun addCargo(commodity: Cargo): VehicleCargo {
        val cargo = cargoList.firstOrNull { it.commodity == commodity }
        if(cargo != null) {
            cargo.amount++
            return cargo
        }
        else {
            val newCargo = TruckerVehicleCargoController.INSTANCE.create(this.getVehicle(), commodity, 1)
            this.cargoList.add(newCargo)
            return newCargo
        }
    }

    fun getRequiredHours(): Int {
        return TruckerVehicleConstants.getByModelId(getVehicle().modelId)?.hours?.toInt() ?: 0
    }

    fun load(commodity: Cargo, onLoad: ((VehicleCargo) -> Unit)? = null) {
        if(!isBeingLoaded) {
            isBeingLoaded = true
            if(VehicleModel.getType(getVehicle().modelId) == VehicleModel.VehicleType.TRAILER)
                getVehicle().state.engine = VehicleParam.PARAM_OFF
            else
                getVehicle().pullingVehicle?.state?.engine = VehicleParam.PARAM_OFF

            loadingTimer = Timer.create(commodity.type.loadingTime, { i ->
                val cargo = addCargo(commodity)
                loadingTimer = null
                isBeingLoaded = false
                if(onLoad != null) onLoad(cargo)
            })
        }
    }

}