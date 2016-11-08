package lt.ltrp.trucker.dialog

import lt.ltrp.BusinessController
import lt.ltrp.`object`.Business
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.property.business.dialog.BusinessListDialog
import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.controller.BusinessCommodityController
import lt.maze.dialog.*
import net.gtaun.shoebill.`object`.Checkpoint
import net.gtaun.shoebill.data.Radius
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-11-06.
 * The main dialog for truckers
 * The dialog should look somewhat like this
 * |--------------------------|
 * | Industry information     |
 * | Ship information         |
 * | Business information     |
 * |--------------------------|
 *
 * It should just show other dialogs on selection
 */
object TpdaDialog {

    fun create(player: LtrpPlayer, eventManager: EventManager): Dialog {
        return ListDialog.create(player, eventManager, {
            caption { "TPDA" }
            buttonOk { "Pasirinkti" }
            buttonCancel { "Uþdaryti" }
            item(ListDialogItem.create {
                itemText { "Perþiûrëti industrijø informacijà" }
                selectHandler(industryItemHandler)
            })
            item {
                itemText { "Perþiûrëti laivo informacijà" }
                // TODO
            }
            item {
                itemText { "Perþiûrëti verslø informacijà" }
                selectHandler { businessItemHandler }
                // Only show if atleast one business needs products
                enabled { Business.get().filter { it.resourcePrice > 0 }.size > 0 }
            }
        })
    }

    private val industryItemHandler: (ListDialogItem) -> Unit = { item ->
        val dialog = item.dialog as ListDialog
        val player = LtrpPlayer.get(dialog.player)
        TruckerIndustryListDialog.create(player, dialog.eventManager, {
            caption { "San Andreas industrijø sàraðas" }
            buttonOk { "Pasirinkti" }
            buttonCancel { "Atgal" }
            parent { dialog }
            selectHandler(industrySelectHandler)
            onClickCancel { it.showParent() }
        }).show()
    }

    private val industrySelectHandler: (TruckerIndustryListDialog, Industry) -> Unit = { dialog, industry ->
        val player = dialog.player
        TruckerIndustryInfoDialog.create(player, industry, dialog, dialog.eventManager).show()
    }

    private val businessItemHandler: (ListDialogItem) -> Unit = { item ->
        val dialog = item.dialog as ListDialog
        val player = LtrpPlayer.get(dialog.player)
        BusinessListDialog.create(player, BusinessController.get().businesses.filter { it.resourcePrice > 0 }, dialog.eventManager, {
            caption { "Ðiuo metu prekes perkantys verslai" }
            buttonOk { "Paþymëti" }
            header { TabListDialogHeader(0, "Pavadinimas") }
            header { TabListDialogHeader(1, "Kaina") }
            header { TabListDialogHeader(2, "Prekë") }
            itemTextSupplier(0, { it.name.take(40) })
            itemTextSupplier(1, { it.resourcePrice.toString() })
            itemTextSupplier(2, { BusinessCommodityController.instance.get(it.businessType)?.name ?: "-" })
            selectBusinessHandler { bizDialog, biz ->
                player.checkpoint = Checkpoint.create(Radius(biz.entrance, 5f), { it.disableCheckpoint() }, null)
            }
        }).show()
    }
}