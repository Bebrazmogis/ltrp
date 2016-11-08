package lt.ltrp.trucker.dialog

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.trucker.controller.TruckerVehicleCargoController
import lt.ltrp.trucker.data.VehicleCargo
import lt.maze.dialog.ListDialogItem
import lt.maze.dialog.TabListDialog
import lt.maze.dialog.TabListDialogHeader
import lt.maze.dialog.TabListDialogItem
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-30.
 */
open class TruckerVehicleCargoListDialog(player: LtrpPlayer, eventManager: EventManager, protected val vehicle: LtrpVehicle):
        TabListDialog(player, eventManager) {


    private var selectHandler: ((TruckerVehicleCargoListDialog, VehicleCargo) -> Unit)? = null

    override fun show() {
        headers.add(TabListDialogHeader(0, "Prekë"))
        headers.add(TabListDialogHeader(0, "Kiekis"))
        TruckerVehicleCargoController.INSTANCE.get(vehicle).forEach {
            items.add(TabListDialogItem.create {
                column(0, it.commodity.name)
                column(1, it.amount.toString())
                data{ it }
            })
        }
        super.show()
    }

    override fun onSelectItem(item: ListDialogItem) {
        if(selectHandler != null)
            selectHandler?.invoke(this, item.data as VehicleCargo)
        super.onSelectItem(item)
    }

    companion object {
        fun create(player: LtrpPlayer, eventManager: EventManager, vehicle: LtrpVehicle,
                   params: (TruckerVehicleCargoListDialogBuilder.() -> Unit)):
                TruckerVehicleCargoListDialog {
            val dialog = TruckerVehicleCargoListDialog(player, eventManager, vehicle)
            val builder = TruckerVehicleCargoListDialogBuilder(dialog)
            builder.params()
            return builder.build()
        }


    }


    @Suppress("UNCHECKED_CAST")
    abstract class AbstractTruckerVehicleCargoListDialogBuilder<T, V>(dialog: T): AbstractTabListDialogBuilder<T, V>(dialog)
            where T: TruckerVehicleCargoListDialog, V: AbstractTruckerVehicleCargoListDialogBuilder<T, V> {

        fun selectHandler(handler: (TruckerVehicleCargoListDialog, VehicleCargo) -> Unit): V {
            dialog.selectHandler = handler
            return this as V
        }
    }

    class TruckerVehicleCargoListDialogBuilder internal constructor (dialog: TruckerVehicleCargoListDialog):
            AbstractTruckerVehicleCargoListDialogBuilder<TruckerVehicleCargoListDialog, TruckerVehicleCargoListDialogBuilder>(dialog) {

    }
}