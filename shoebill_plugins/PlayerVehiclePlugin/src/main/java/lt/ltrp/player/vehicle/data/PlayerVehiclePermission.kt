package lt.ltrp.player.vehicle.data

import lt.ltrp.`object`.impl.NamedEntityImpl

/**
 * Created by Bebras on 2016-12-30.
 * A class representing a permission for [lt.ltrp.player.vehicle.object.PlayerVehicle]
 */
open class PlayerVehiclePermission(uuid: Int, name: String, val identifier: String): NamedEntityImpl(uuid, name) {

    override fun toString(): String {
        return "PlayerVehiclePermission=[uuid=$UUID,name=$name,identifier=$identifier]"
    }

}