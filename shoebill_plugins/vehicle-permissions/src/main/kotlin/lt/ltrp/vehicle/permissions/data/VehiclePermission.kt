package lt.ltrp.vehicle.permissions.data

/**
 * Created by Bebras on 2016-12-30.
 * A class representing a a vehicle permission
 * An identifier should be all lower case, no spaces
 */
data class VehiclePermission(val uuid: Int, var name: String, var identifier: String) {}