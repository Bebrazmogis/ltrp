package lt.ltrp.property.`object`

import lt.ltrp.ActionMessenger
import lt.ltrp.StateMessenger
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.NamedEntity
import lt.maze.streamer.`object`.DynamicPickup
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.entities.Destroyable
import net.gtaun.shoebill.entities.Player
import java.util.Collections
import java.util.Optional
import java.util.stream.Collectors

/**
 * @author Bebras
 * *         2016.04.13.
 */
interface Property : NamedEntity, Destroyable, StateMessenger, ActionMessenger {

    val isOwned: Boolean
        get() = ownerUUID != Entity.INVALID_ID

    var ownerUUID: Int
    var exit: Location?
    var entrance: Location
    var isLocked: Boolean
    var price: Int


    var pickupModelId: Int
    val pickup: DynamicPickup

    fun isOwner(player: LtrpPlayer): Boolean
    fun isInside(player: LtrpPlayer): Boolean

    companion object {

        internal val propertyList = mutableListOf<Property>()

        fun get(): Collection<Property> {
            return propertyList
        }

        operator fun get(id: Int): Property? {
            return propertyList.firstOrNull { it.UUID == id }
        }

        operator fun get(player: Player): Property? {
            return get().firstOrNull { it.exit != null && it.exit?.distance(player.location) ?: 201f < 200f }
        }

        fun getClosest(location: Location, maxDistance: Float): Property? {
            val closest =  get()
                    .minBy { Math.min(it.entrance.distance(location), it.exit?.distance(location) ?: Float.MAX_VALUE) }
            if (closest != null) {
                val distance = closest.entrance.distance(location)
                if (distance <= maxDistance) {
                    return closest
                }
            }
            return null
        }

        fun getByDistance(location: Location): List<Property> {
            return get().sortedBy {
                val dist = Math.min(it.entrance.distance(location), if (it.exit != null) it.exit!!.distance(location) else Float.MAX_VALUE)
                dist
            }
        }
    }

}
