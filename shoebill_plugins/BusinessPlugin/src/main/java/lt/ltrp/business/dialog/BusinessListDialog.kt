package lt.ltrp.business.dialog

import lt.ltrp.BusinessController
import lt.ltrp.`object`.Business
import lt.ltrp.`object`.LtrpPlayer
import lt.maze.dialog.ListDialogItem
import lt.maze.dialog.TabListDialog
import lt.maze.dialog.TabListDialogHeader
import lt.maze.dialog.TabListDialogItem
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-11-07.
 * This dialog shows a list of businesses
 * If a specific list is not specified it will show all existing businesses
 * It also supports an item text supplier if the item text needs to look differently
 */
open class BusinessListDialog(player: LtrpPlayer, val businesses: Collection<Business>, eventManager: EventManager):
        TabListDialog(player, eventManager) {

    private val defaultTextSupplier: (Business) -> String = { it.name.take(40) }
    private val defaultHeader = TabListDialogHeader(0, "Pavadinimas")

    private var itemTextSuppliers: MutableList<(Business) -> String> = mutableListOf()
    private var selectHandler: ((BusinessListDialog, Business) -> Unit)? = null

    override fun show() {

        if(headers.size == 0)
            headers.add(defaultHeader)

        businesses.forEach {
            items.add(TabListDialogItem.create {
                headers.forEachIndexed { index, header ->
                    column(index, itemTextSuppliers.getOrElse(index, { defaultTextSupplier }).invoke(it))
                }
            })
        }
        super.show()
    }

    override fun onSelectItem(item: ListDialogItem) {
        selectHandler?.invoke(this, item.data as Business)
        super.onSelectItem(item)
    }

    companion object {
        fun create(player: LtrpPlayer, eventManager: EventManager, params: BusinessListDialogBuilder.() -> Unit): BusinessListDialog {
            return create(player, BusinessController.get().businesses, eventManager, params)
        }

        fun create(player: LtrpPlayer, businesses: Collection<Business>, eventManager: EventManager,
                   params: BusinessListDialogBuilder.() -> Unit): BusinessListDialog {
            val builder = BusinessListDialogBuilder(BusinessListDialog(player, businesses, eventManager))
            builder.params()
            return builder.build()
        }

        abstract class AbstractBusinessListDialogBuilder<T, V>(dialog: T): AbstractTabListDialogBuilder<T, V>(dialog)
                where T: BusinessListDialog, V: AbstractBusinessListDialogBuilder<T, V> {


            /**
             * Sets an item text supplier for dialog item text
             */
            @Suppress("UNCHECKED_CAST")
            fun itemTextSupplier(index: Int, supplier: (Business) -> String): V {
                dialog.itemTextSuppliers.add(index, supplier)
                return this as V
            }

            @Suppress("UNCHECKED_CAST")
            fun selectBusinessHandler(handler: (BusinessListDialog, Business) -> Unit): V {
                dialog.selectHandler = handler
                return this as V
            }
        }

        class BusinessListDialogBuilder internal constructor (dialog: BusinessListDialog):
                AbstractBusinessListDialogBuilder<BusinessListDialog, BusinessListDialogBuilder>(dialog) {

        }
    }
}