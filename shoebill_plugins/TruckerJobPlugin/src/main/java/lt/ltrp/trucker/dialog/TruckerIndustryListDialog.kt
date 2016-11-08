package lt.ltrp.trucker.dialog

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.controller.IndustryController
import lt.maze.dialog.ListDialog
import lt.maze.dialog.ListDialogItem
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-11-06.
 * This dialog shows a list of industries
 */
open class TruckerIndustryListDialog(override val player: LtrpPlayer, eventManager: EventManager): ListDialog(player, eventManager) {

    private var selectHandler: ((TruckerIndustryListDialog, Industry) -> Unit)? = null

    override fun show() {
        IndustryController.INSTANCE.get().forEach {
            items.add(ListDialogItem.create {
                itemText { "{FFFFFF}" + it.name + "{C0C0C0}" }
                data { it }
            })
        }
        super.show()
    }

    override fun onSelectItem(item: ListDialogItem) {
        selectHandler?.invoke(this, item.data as Industry)
        super.onSelectItem(item)
    }

    companion object {

        fun create(player: LtrpPlayer, eventManager: EventManager, params: (TruckerIndustryListDialogBuilder.() -> Unit)): TruckerIndustryListDialog {
            val builder = TruckerIndustryListDialogBuilder(TruckerIndustryListDialog(player, eventManager))
            builder.params()
            return builder.build()
        }

        abstract class AbstractTruckerIndustryListDialogBuilder<T, V>(dialog: T): AbstractListDialogBuilder<T, V>(dialog)
                where T: TruckerIndustryListDialog, V: AbstractTruckerIndustryListDialogBuilder<T, V> {

            @Suppress("UNCHECKED_CAST")
            fun selectHandler(handler: (TruckerIndustryListDialog, Industry) -> Unit): V {
                dialog.selectHandler = handler
                return this as V
            }
        }

        class TruckerIndustryListDialogBuilder internal constructor(dialog: TruckerIndustryListDialog):
                AbstractTruckerIndustryListDialogBuilder<TruckerIndustryListDialog, TruckerIndustryListDialogBuilder>(dialog) {

        }
    }
}