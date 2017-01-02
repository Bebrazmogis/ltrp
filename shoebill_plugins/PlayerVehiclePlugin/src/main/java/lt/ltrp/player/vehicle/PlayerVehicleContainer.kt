package lt.ltrp.player.vehicle

import lt.ltrp.player.vehicle.`object`.PlayerVehicle

/**
 * Created by Bebras on 2017-01-02.
 * [PlayerVehicle] container
 */
object PlayerVehicleContainer {

    internal val vehicles = mutableSetOf<PlayerVehicle>()


    fun get(): Collection<PlayerVehicle> {
        return vehicles
    }

}