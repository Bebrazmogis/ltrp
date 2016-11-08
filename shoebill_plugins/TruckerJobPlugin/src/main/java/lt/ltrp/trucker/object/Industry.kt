package lt.ltrp.trucker.`object`

import lt.ltrp.`object`.impl.EntityImpl
import lt.ltrp.trucker.data.Cargo
import lt.ltrp.trucker.data.IndustryStock
import lt.ltrp.trucker.dialog.IndustryBuyCommodityDialog
import lt.maze.streamer.`object`.DynamicLabel
import net.gtaun.shoebill.`object`.Destroyable
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Location
import net.gtaun.util.event.EventManager
import java.util.*

/**
 * @author Bebras
* 2016.06.19.
 */
class Industry(id: Int, name: String, location: Location, private val eventManager: EventManager): EntityImpl(id), Destroyable {

    var name: String = name
        get
        set(value) {
            updateLabel()
            field = value
        }

    var location: Location = location
        get
        set(value) {
            updateLabel()
            field = value
        }


    var productions: ArrayList<IndustryProduction> = ArrayList<IndustryProduction>()
        get
        private set


    var boughtStock = mutableListOf<IndustryStock>()
        get
        private set

    var soldStock = mutableListOf<IndustryStock>()
        get
        private set

    private var destroyed = false
    private var label: DynamicLabel = DynamicLabel.create(getLabelText(), Color.WHITE, location)


    fun isBuyingCommodity(c: Cargo): Boolean {
        return boughtStock.map { it.cargo }.contains(c)
    }

    override fun destroy() {
        destroyed = true
        label.destroy()
    }

    override fun isDestroyed(): Boolean {
        return destroyed
    }

    private fun getLabelText(): String {
        return name + "\n"
    }

    fun updateLabel() {
        val text = getLabelText()
        if(label.isValid && !label.isDestroyed) {
            label.update(Color.WHITE, text)
        } else {
            label.destroy()
            label = DynamicLabel.create(text, Color.WHITE, location)
        }
    }

    fun showSoldCommodities(player: TruckerPlayer) {
        IndustryBuyCommodityDialog.create(player, this, eventManager)
                .show()
    }
}