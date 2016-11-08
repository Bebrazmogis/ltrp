package lt.ltrp.trucker.data

import lt.ltrp.trucker.`object`.TruckerVehicle
import lt.ltrp.trucker.controller.TruckerVehicleCargoController

/**
 * Created by Bebras on 2016-10-29.
 * Data for cargo stored inside vehicles
 */
class VehicleCargo(val vehicle: TruckerVehicle,
                   val commodity: Cargo,
                   amount: Int) {

    var amount = amount
        set(value) {
            TruckerVehicleCargoController.INSTANCE.update(this)
            field = value
        }

}