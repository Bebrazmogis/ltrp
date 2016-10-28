package lt.ltrp.house.weed.`object`.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.PlayerData
import lt.ltrp.`object`.impl.EntityImpl
import lt.ltrp.house.`object`.House
import lt.ltrp.house.weed.`object`.HouseWeedSapling
import lt.ltrp.house.weed.constant.GrowthStage
import lt.ltrp.house.weed.event.WeedGrowEvent
import lt.maze.streamer.`object`.DynamicObject
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.data.Vector3D
import net.gtaun.util.event.EventManager
import java.time.LocalDateTime
import java.util.*

/**
 * Created by Bebras on 2016-10-14.
 * A concrete implementation of [HouseWeedSapling] object
 */
class HouseWeedSaplingImpl(uuid: Int,
                           override val house: House,
                           override val location: Location,
                           override val plantedAt: LocalDateTime,
                           override var growthStage: GrowthStage,
                           override val plantedBy: PlayerData,
                           override var grownAt: LocalDateTime?,
                           override var harvestedBy: PlayerData?,
                           override var yieldAmount: Int,
                           val eventManager: EventManager):
        HouseWeedSapling, EntityImpl(uuid) {

    constructor(house: House, location: Location, plantedBy: PlayerData, eventManager: EventManager):
            this(Entity.INVALID_ID, house, location, LocalDateTime.now(), GrowthStage.Seed,
                    plantedBy, null, null, 0, eventManager) {

    }

    override var destroyed: Boolean = false

    private var weedObject: DynamicObject? = null
    private var plantPotObject:DynamicObject? = null
    private var growthTimer: Timer? = null

    init {
        createObjects()
    }

    override fun startGrowth() {
        if (growthStage == GrowthStage.Grown)
            return
        setTimer()
    }

    override fun isGrown(): Boolean {
        return growthStage == GrowthStage.Grown
    }

    override fun isDestroyed(): Boolean {
        return destroyed
    }

    override fun destroy() {
        destroyed = true
        growthTimer?.cancel()
        weedObject?.destroy()
        plantPotObject?.destroy()
        house.weedSaplings.remove(this)
    }

    private fun setTimer() {
        if(growthTimer == null)
            growthTimer = Timer("WeedSapling UUID $UUID timer")
        else
            growthTimer?.cancel()

        growthTimer?.schedule(object : TimerTask() {
            override fun run() {
                growthStage = growthStage.next()
                if(growthStage == GrowthStage.Grown) {
                    grownAt = LocalDateTime.now()
                    yieldAmount = Random().nextInt(HouseWeedSapling.MAX_YIELD - HouseWeedSapling.MIN_YIELD) + HouseWeedSapling.MIN_YIELD
                } else {
                    setTimer()
                    createObjects()
                }
                eventManager.dispatchEvent(WeedGrowEvent(house, this@HouseWeedSaplingImpl, isGrown()))
            }

        }, growthStage.growthDuration.toMillis())
    }

    private fun createObjects() {
        plantPotObject?.destroy()
        weedObject?.destroy()

        plantPotObject = DynamicObject.create(HouseWeedSapling.PLANT_POT_MODEL, location, Vector3D())
        // Seed stage is not shown
        if (growthStage != GrowthStage.Seed) {
            weedObject = DynamicObject.create(growthStage.modelId, location, Vector3D())
        }
    }

}