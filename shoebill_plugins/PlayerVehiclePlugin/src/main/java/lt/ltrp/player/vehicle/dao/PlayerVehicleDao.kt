package lt.ltrp.player.vehicle.dao

import lt.ltrp.player.vehicle.data.PlayerVehicleMetadata
import lt.ltrp.data.VehicleCrime
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.player.vehicle.`object`.PlayerVehicle
import lt.ltrp.vehicle.dao.VehicleDao
import net.gtaun.shoebill.data.AngledLocation

/**
 * @author Bebras
 * *         2016.05.23.
 */
interface PlayerVehicleDao : VehicleDao {


    // Player vehicles
    fun getPlayerVehicles(player: LtrpPlayer): IntArray

    fun getArrestedPlayerVehicles(player: LtrpPlayer): IntArray
    fun insert(modelId: Int, spawnLocation: AngledLocation, license: String, color1: Int, color2: Int, mileage: Float, fuel: Float, ownerId: Int,
               deaths: Int, alarm: String, lock: String, lockCrackTime: Int, lockPrice: Int, insurance: Int, doors: Int, panels: Int, lights: Int,
               tires: Int, health: Float): Int

    operator fun get(vehicleId: Int): PlayerVehicle
    fun update(playerVehicle: PlayerVehicle)
    fun delete(vehicle: PlayerVehicle)
    fun getPlayerVehicleCount(player: LtrpPlayer): Int
    fun getPlayerVehicleByLicense(licensePlate: String): Int
    fun getPlayerVehicleMeta(vehicleId: Int): PlayerVehicleMetadata

    fun setOwner(vehicle: PlayerVehicle, owner: LtrpPlayer)


    fun insertCrime(crime: VehicleCrime)

}
