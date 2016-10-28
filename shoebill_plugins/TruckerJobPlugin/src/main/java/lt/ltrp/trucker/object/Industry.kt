package lt.ltrp.trucker.`object`

import lt.ltrp.`object`.impl.EntityImpl
import lt.ltrp.trucker.constant.IndustryCommodityType
import lt.ltrp.trucker.data.IndustryCommodity
import lt.maze.streamer.`object`.DynamicLabel
import net.gtaun.shoebill.`object`.Destroyable
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Location
import java.util.ArrayList

/**
 * @author Bebras
* 2016.06.19.
 */
class Industry(id: Int, name: String, location: Location): EntityImpl(id), Destroyable {

    var name: String = name
        get
        set

    var location: Location = location
        get
        set


    var productions: ArrayList<IndustryProduction> = ArrayList<IndustryProduction>()
        get
        private set


    var commodities: ArrayList<IndustryCommodity> = ArrayList<IndustryCommodity>()
        get
        private set

    private var destroyed = false
    private var label: DynamicLabel = DynamicLabel.create(getText(), Color.WHITE, location)

    fun getSoldCommodities(): List<IndustryCommodity> {
        return commodities.filter { it.type == IndustryCommodityType.SOLD }
    }

    fun getBoughtCommodities(): List<IndustryCommodity> {
        return commodities.filter { it.type == IndustryCommodityType.BOUGHT }
    }

    override fun destroy() {
        destroyed = true
        label.destroy()
    }

    override fun isDestroyed(): Boolean {
        return destroyed
    }

    fun getText(): String {
        return name + "\n"
    }
}