package lt.ltrp.trucker.`object`;

import lt.ltrp.trucker.data.Cargo
import lt.maze.mapandreas.MapAndreas
import lt.maze.streamer.`object`.DynamicObject
import net.gtaun.shoebill.`object`.Destroyable
import net.gtaun.shoebill.`object`.Timer
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.data.Vector3D

/**
 * Created by Bebras on 2016-11-06.
 * This class represents a box containing cargo placed on the ground
 * Should disappear after a certain time or picked up
 *
 */
open class PlacedCargoBox(val commodity: Cargo, val location: Location): Destroyable {

    private var boxObject: DynamicObject
    private var disappearTimer: Timer
    private var destroyed = false

    init {
        // Try to find a better Z coordinate,
        // if it finds one below the current one, we use that
        val newZ = MapAndreas.findZ(location)
        if(newZ < location.z)
            location.z = newZ

        boxObject = DynamicObject.create(BOX_OBJECT_MODEL, location, Vector3D())
        disappearTimer = Timer.create(DISAPPEAR_TIME, {
            destroy()
        })
    }

    protected fun finalize() {
        if(!isDestroyed) destroy()
    }

    override fun destroy() {
        cargoBoxes.remove(this)
        destroyed = true
        boxObject.destroy()
        disappearTimer.destroy()
    }

    override fun isDestroyed(): Boolean {
        return destroyed
    }

    companion object {
        private val BOX_OBJECT_MODEL = 2912
        private val DISAPPEAR_TIME = 5*60*1000
        val cargoBoxes = mutableSetOf<PlacedCargoBox>()


        fun create(commodity: Cargo, location: Location): PlacedCargoBox {
            val cargoBox = PlacedCargoBox(commodity, location)
            cargoBoxes.add(cargoBox)
            return cargoBox
        }
    }
}
