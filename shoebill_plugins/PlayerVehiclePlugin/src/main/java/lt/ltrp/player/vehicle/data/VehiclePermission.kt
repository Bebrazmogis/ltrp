package lt.ltrp.player.vehicle.data

/**
 * Created by Bebras on 2016-12-30.
 * A class representing a a vehicle permission
 * An identifier should be all lower case, no spaces
 */
data class VehiclePermission(var uuid: Int, var name: String, val identifier: String) {}