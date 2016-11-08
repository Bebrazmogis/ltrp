package lt.ltrp.trucker.dialog

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.constant.Currency
import lt.ltrp.trucker.`object`.Industry
import lt.maze.dialog.AbstractDialog
import lt.maze.dialog.MsgBoxDialog
import net.gtaun.shoebill.`object`.Checkpoint
import net.gtaun.shoebill.data.Radius
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-11-06.
 * This dialog shows information about an industry and its sold/bought commodities
 * If selects OK, checkpoint is created and shown
 */
object TruckerIndustryInfoDialog {

    fun create(player: LtrpPlayer, industry: Industry, parent: AbstractDialog, eventManager: EventManager): MsgBoxDialog {
        return MsgBoxDialog.create(player, eventManager, {
            caption { industry.name + " informacija" }
            buttonOk { "Paþymëti" }
            buttonCancel { "Atgal" }
            parent(parent)
            body {
                val maxLen = industry.soldStock.maxBy { it.cargo.name.length }
                var body = "{FFFFFF}Sveiki atvyke á {00FF66}" + industry.name + "!\n" +
                        String.format("{C8C8C8}Prekë%"+maxLen+"s Yra sandëlyje(limitas)\t\tKaina(%s)\n", "", Currency.NAME_SHORT)
                industry.soldStock.forEach {
                    body += String.format("{FFFFFF}%"+maxLen+"s%d vienetai {C8C8C8}(%d){FFFFFF}\t\t%c%d\n",
                            it.cargo.name,
                            it.currentStock,
                            it.maxStock,
                            Currency.SYMBOL,
                            it.price)
                }
                body += "\n"
                if(industry.boughtStock.size > 0) {
                    body += String.format("{C8C8C8}Prekë%"+maxLen+"sYra sandëlyje (limitas)\t\tKaina\n", "")
                    industry.boughtStock.forEach {
                        body += String.format("{FFFFFF}%"+maxLen+"s%d vienetai{C8C8C8}(%d){FFFFFF}\t\t\t%c%d\n",
                                it.cargo.name,
                                it.currentStock,
                                it.maxStock,
                                Currency.SYMBOL,
                                it.price)
                    }
                } else
                    body += "Ði industrija nieko neperka."
                body
            }
            clickOk{ dialog ->
                player.checkpoint = Checkpoint.create(Radius(industry.location, 5f), {
                    player.sendMessage("Sveiki atvykæ á \"" + industry.name + "\"")
                    player.disableCheckpoint()
                }, null)
            }
            onClickCancel{ it.showParent() }
        })
    }

}