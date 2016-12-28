package lt.ltrp.dialog

import lt.ltrp.`object`.LtrpPlayer
import lt.maze.dialog.ListDialog
import lt.maze.dialog.ListDialogItem
import net.gtaun.shoebill.constant.WeaponModel
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-28.
 * This dialog shows a list of possible [WeaponModel]
 */
open class WeaponModelSelectDialog(player: LtrpPlayer, override val eventManager: EventManager):
        ListDialog(player, eventManager) {

    var clickOkHandler: ((WeaponModelSelectDialog, WeaponModel) -> Unit)? = null

    override fun show() {
        WeaponModel.values().forEach { w ->
                items.add(ListDialogItem.create {
                    itemText { w.name }
                    data { w }
                })
        }
        super.show()
    }

    override fun onSelectItem(item: ListDialogItem) {
        if(clickOkHandler != null)
            clickOkHandler?.invoke(this, item.data as WeaponModel)
        else super.onSelectItem(item)
    }

    companion object {
        fun create(player: LtrpPlayer, eventManager: EventManager, params: (WeaponModelSelectDialogBuilder.() -> Unit)):
                WeaponModelSelectDialog {
            val dialog = WeaponModelSelectDialog(player, eventManager)
            val builder = WeaponModelSelectDialogBuilder(dialog)
            builder.apply(params)
            return builder.build()
        }
    }

    abstract class AbstractWeaponModelSelectDialogBuilder<T, V>(dialog: T):
            AbstractListDialogBuilder<T, V>(dialog) where T:WeaponModelSelectDialog, V: AbstractWeaponModelSelectDialogBuilder<T, V> {

        @Suppress("UNCHECKED_CAST")
        fun onSelectWeapon(handler: (WeaponModelSelectDialog, WeaponModel) -> Unit): V {
            dialog.clickOkHandler = handler
            return this as V
        }

    }

    class WeaponModelSelectDialogBuilder internal constructor(dialog: WeaponModelSelectDialog):
            AbstractWeaponModelSelectDialogBuilder<WeaponModelSelectDialog, WeaponModelSelectDialogBuilder>(dialog) {

    }
}