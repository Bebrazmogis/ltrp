package lt.ltrp.house.weed.`object`;

import lt.ltrp.`object`.Entity
import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.house.`object`.House
import lt.ltrp.house.weed.constant.GrowthStage
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.entities.Destroyable
import java.time.LocalDateTime


/**
 * @author Bebras
 *         2015.12.05.
 *
 *
 */
interface HouseWeedSapling: Entity, Destroyable {

    val house: House
    val location: Location
    val plantedAt: LocalDateTime
    var grownAt: LocalDateTime?
    var harvestedBy: PlayerData?
    var growthStage: GrowthStage
    val plantedBy: PlayerData
    var yieldAmount: Int
    var destroyed: Boolean


    fun isGrown(): Boolean
    fun startGrowth()

    companion object {
        val PLANT_POT_MODEL = 12
        val MIN_YIELD = 12
        val MAX_YIELD = 22
    }



}
