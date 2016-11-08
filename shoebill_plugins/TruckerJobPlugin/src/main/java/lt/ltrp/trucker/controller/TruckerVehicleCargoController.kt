package lt.ltrp.trucker.controller

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.trucker.data.Cargo
import lt.ltrp.trucker.data.VehicleCargo
import lt.maze.injector.InjectorPlugin
import net.gtaun.shoebill.resource.ResourceManager

/**
 * Created by Bebras on 2016-10-30.
 * A set of methods for interacting with vehicle cargo
 */
interface TruckerVehicleCargoController {

    fun get(vehicle: LtrpVehicle): List<VehicleCargo>
    fun create(vehicle: LtrpVehicle, cargo: Cargo, amount: Int): VehicleCargo
    fun remove(cargo: VehicleCargo)
    fun update(cargo: VehicleCargo)


    companion object {
        private val kodein: Kodein? by lazy {
            ResourceManager.get().getPlugin(InjectorPlugin::class.java).kodein
        }

        val INSTANCE: TruckerVehicleCargoController by kodein?.lazy?.instance<TruckerVehicleCargoController>()
    }

}