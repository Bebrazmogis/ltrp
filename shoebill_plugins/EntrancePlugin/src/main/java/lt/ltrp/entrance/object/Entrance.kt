package lt.ltrp.entrance.`object`

import lt.ltrp.entrance.EntrancePlugin
import lt.ltrp.job.`object`.Job
import lt.ltrp.`object`.DestroyableEntity
import lt.maze.streamer.`object`.DynamicLabel
import lt.maze.streamer.`object`.DynamicPickup
import net.gtaun.shoebill.data.AngledLocation
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Location

/**
 * @author Bebras
 * *         2016.05.22.
 */
interface Entrance : DestroyableEntity {


    var location: AngledLocation

    val pickup: DynamicPickup
    val label: DynamicLabel

    var text: String

    var pickupModelId: Int

    var color: Color

    fun allowsVehicles(): Boolean
    fun setAllowVehicles(set: Boolean)

    val isJob: Boolean
    var job: Job

    var exit: Entrance

    companion object {

        val DEFAULT_PICKUP_MODEL = 19606
        val DEFAULT_LABEL_COLOR = Color(0x1299A2AA)
        val MIN_TEXT_LENGTH = 10

        fun create(location: AngledLocation, name: String): Entrance {
            return create(location, name, DEFAULT_LABEL_COLOR, DEFAULT_PICKUP_MODEL, null, false)
        }

        fun create(location: AngledLocation, text: String, labelColor: Color, pickupModelId: Int, job: Job?, allowsVehicles: Boolean): Entrance {
            return EntrancePlugin.get(EntrancePlugin::class.java.toInt()).createEntrance(location, text, labelColor, pickupModelId, job, allowsVehicles)
        }

        fun get(): Collection<Entrance> {
            return EntrancePlugin.get(EntrancePlugin::class.java.toInt()).getEntrances()
        }

        operator fun get(uuid: Int): Entrance {
            return EntrancePlugin.get(EntrancePlugin::class.java.toInt()).get(uuid)
        }

        @JvmOverloads fun getClosest(location: Location, maxDistance: Float = java.lang.Float.POSITIVE_INFINITY): Entrance {
            return EntrancePlugin.get(EntrancePlugin::class.java.toInt()).getClosest(location, maxDistance)
        }
    }

}
