package lt.ltrp.trucker.dialog

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.PlayerCountdown
import lt.ltrp.constant.Currency
import lt.ltrp.modelpreview.BasicModelPreview
import lt.ltrp.trucker.TruckerJobPlugin
import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.`object`.TruckerPlayer
import lt.ltrp.trucker.`object`.TruckerVehicle
import lt.ltrp.trucker.data.IndustryStock
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-30.
 * This dialog lets the [trucker] to buy a commodity
 */
object IndustryBuyCommodityDialog {

    fun create(trucker: TruckerPlayer, industry: Industry, eventManager: EventManager): IndustrySoldStockListDialog {
        return IndustrySoldStockListDialog.create(trucker, industry, eventManager, {
            caption{ industry.name + " preki� s�ra�as" }
            buttonOk("Pirkti")
            buttonCancel{ "At�aukti" }
            onSelect(onBuyCommodity)
        })
    }

    var onBuyCommodity: (IndustrySoldStockListDialog, IndustryStock) -> Unit = { dialog, commodity ->
        val trucker = dialog.trucker
        val player = trucker.player
        if(player.money < commodity.price)
            player.sendErrorMessage("Jums neu�tenka pinig�, jums tr�kst dar " + (commodity.price - player.money) + Currency.SYMBOL)
        else if(commodity.currentStock == 0)
            player.sendErrorMessage("�ios prek�s sand�lyje neb�ra.")

        else {
            if(commodity.cargo.type.isCarryable) {
                if(trucker.cargo != null)
                    trucker.player.sendErrorMessage("J�s jau laikote ka�k� rankose!")
                else {
                    trucker.cargo = commodity.cargo
                    commodity.currentStock--
                    player.money -= commodity.price
                }

            } else {
                val holder = ResourceManager.get().getPlugin(TruckerJobPlugin::class.java).getVehicleHolder()
                val vehicles = holder.getObjects()
                if(vehicles.size == 0)
                    player.sendErrorMessage("Prie j�s� n�ra jokios transporto priemon�s � kuri� galima pakrauti krovin�!")
                else if(vehicles.size == 1) {
                    onBuyCommodityForVehicle(player, vehicles.first(), commodity)
                }
                else {
                    BasicModelPreview.create(player, dialog.eventManager)
                            .models(vehicles.map { it.vehicle.modelId })
                            .onSelectModel { modelPreview, modelId ->
                                onBuyCommodityForVehicle(player, vehicles.find { it.vehicle.modelId == modelId } as TruckerVehicle, commodity)
                            }
                            .build()
                            .show()
                }
            }
        }
    }

    private fun onBuyCommodityForVehicle(player: LtrpPlayer, vehicle: TruckerVehicle, commodity: IndustryStock) {
        if(!vehicle.isCompatibleCargo(commodity.cargo.type))
            player.sendErrorMessage(vehicle.vehicle.modelName + " �io krovinio �veti negali")
        else if(vehicle.getRequiredHours() > player.jobData!!.hours)
            player.sendErrorMessage("J�s dar nepakankamai patyr�s naudotis �ia transporto priemone.")
        else if(vehicle.isFull(commodity.cargo.type))
            player.sendErrorMessage(vehicle.vehicle.modelName + " jau yra pilna(s)")
        else if(vehicle.isBeingLoaded)
            player.sendErrorMessage(vehicle.vehicle.modelName + " �iuo metu kraunama.")
        else {
            player.sendMessage("J�s� krovinys prad�tas krauti, krovimas u�truks " + commodity.cargo.type.loadingTime)
            vehicle.vehicle.sendStateMessage("Darbininkai pradeda krauti ka�k� kas atrodo kaip " + commodity.cargo.name)
            player.countdown = PlayerCountdown.create(player, commodity.cargo.type.loadingTime, false, null, false, "Kraunama")
            vehicle.load(commodity.cargo, {
                vehicle.vehicle.sendStateMessage("Darbininkai baigia krauti krovin�")
                player.money -= commodity.price
                commodity.currentStock--
            })
        }
    }
}