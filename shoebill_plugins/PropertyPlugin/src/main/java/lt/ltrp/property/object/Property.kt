package lt.ltrp.property.`object`

import lt.ltrp.property.PropertyController
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.NamedEntity
import lt.maze.streamer.`object`.DynamicPickup
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.entities.Destroyable
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

        fun get(): Collection<Property> {
            return PropertyController.get().properties
        }

        operator fun get(id: Int): Property? {
            val op = get()
                    .stream()
                    .filter({ b -> b.getUUID() === id })
                    .findFirst()
            return if (op.isPresent()) op.get() else null
        }

        operator fun get(player: LtrpPlayer): Property {
            return PropertyController.get().get(player)
        }

        fun getClosest(location: Location, maxDistance: Float): Property? {
            val op = get().stream().min({ b1, b2 ->
                java.lang.Float.compare(Math.min(b1.entrance.distance(location), b1.exit!!.distance(location)),
                        Math.min(b2.entrance.distance(location), b2.exit!!.distance(location)))
            })
            if (op.isPresent()) {
                val distance = op.get().entrance.distance(location)
                if (distance <= maxDistance) {
                    return op.get()
                }
            }
            return null
        }

        fun getByDistance(location: Location): List<Property> {
            val list = get().stream().collect(Collectors.toList<Property>())
            Collections.sort<Property>(list) { p1, p2 ->
                val dist1 = Math.min(p1.entrance.distance(location), if (p1.exit != null) p1.exit!!.distance(location) else java.lang.Float.POSITIVE_INFINITY)
                val dist2 = Math.min(p2.entrance.distance(location), if (p2.exit != null) p2.exit!!.distance(location) else java.lang.Float.POSITIVE_INFINITY)
                java.lang.Float.compare(dist1, dist2)
            }
            return list
        }
    }

}
