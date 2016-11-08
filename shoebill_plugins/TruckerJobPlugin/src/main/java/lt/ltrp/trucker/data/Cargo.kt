package lt.ltrp.trucker.data

import lt.ltrp.`object`.impl.EntityImpl
import lt.ltrp.trucker.constant.TruckerCargoType

/**
 * @author Bebras
 * 2016.06.19.
 *
 * This class represents a simple cargo that has an UUID,name and a [TruckerCargoType]
 */
open class Cargo(uuid: Int, name: String, val type: TruckerCargoType): EntityImpl(uuid) {
    val name: String = name
        get

    override fun equals(other: Any?): Boolean {
        return other is Cargo && other.UUID == UUID && other.type == this.type
    }

    override fun hashCode(): Int{
        return UUID.hashCode()
    }
}