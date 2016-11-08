package lt.ltrp.trucker.dialog

import lt.ltrp.constant.Currency
import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.`object`.TruckerPlayer
import lt.ltrp.trucker.data.IndustryStock
import lt.maze.dialog.ListDialogItem
import lt.maze.dialog.TabListDialog
import lt.maze.dialog.TabListDialogHeader
import lt.maze.dialog.TabListDialogItem
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-30.
 * A list of the industries commodities
 * Shows commodities their prices and stock amounts with a tabbed, header list dialog
 *
 * |---------------------------------------------|
 * | Name | Price($) | current_stock / max_stock |
 * |---------------------------------------------|
 *
 */
open class IndustrySoldStockListDialog(val trucker: TruckerPlayer, val industry: Industry, eventManager: EventManager):
        TabListDialog(trucker.player, eventManager) {

    private var selectCommodityHandler: ((IndustrySoldStockListDialog, IndustryStock) -> Unit)? = null

    override fun show() {
        headers.add(TabListDialogHeader(0, "Prekë"))
        headers.add(TabListDialogHeader(1, "Kaina(" + Currency.SYMBOL + ")"))
        headers.add(TabListDialogHeader(2, "Kiekis"))
        industry.soldStock.forEach {
            items.add(TabListDialogItem.create {
                column(0, it.cargo.name)
                column(1, it.price.toString())
                column(2, String.format("%d/%d", it.currentStock, it.maxStock))
                data { it }
            })
        }
        super.show()
    }

    override fun onSelectItem(item: ListDialogItem) {
        selectCommodityHandler?.invoke(this, item.data as IndustryStock)
        super.onSelectItem(item)
    }


    companion object {
        fun create(player: TruckerPlayer, industry: Industry, eventManager: EventManager,
                   params: IndustrySoldStockListDialogBuilder.() -> Unit): IndustrySoldStockListDialog {
            val dialog = IndustrySoldStockListDialog(player, industry, eventManager)
            val builder = IndustrySoldStockListDialogBuilder(dialog)
            builder.params()
            return builder.build()
        }

    }

    @Suppress("UNCHECKED_CAST")
    abstract class AbstractIndustrySoldStockListDialogBuilder<T, V>(dialog: T):
            AbstractTabListDialogBuilder<T, V>(dialog)
    where T: IndustrySoldStockListDialog, V: AbstractIndustrySoldStockListDialogBuilder<T, V> {

        fun onSelect(handler: (IndustrySoldStockListDialog, IndustryStock) -> Unit): V {
            dialog.selectCommodityHandler = handler
            return this as V
        }

    }

    class IndustrySoldStockListDialogBuilder internal constructor (dialog: IndustrySoldStockListDialog):
            AbstractIndustrySoldStockListDialogBuilder<IndustrySoldStockListDialog, IndustrySoldStockListDialogBuilder>(dialog) {

    }
}