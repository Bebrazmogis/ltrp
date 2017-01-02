package lt.ltrp

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.business.BusinessPlugin
import lt.ltrp.constant.Currency
import lt.ltrp.data.Color
import lt.ltrp.event.PlayerPaydayEvent
import lt.ltrp.garage.GaragePlugin
import lt.maze.event.PayDayEvent
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventHandler
import net.gtaun.util.event.EventManager
import org.slf4j.Logger

/**
 * Created by Bebras on 2016-12-21.
 *
 */
class PayDayEventHandler(private val eventManager: EventManager,
                         private val logger: Logger) : EventHandler<PayDayEvent> {

    override fun handleEvent(e: PayDayEvent?) {
        if(e == null) {
            return
        }
        val bankPlugin = ResourceManager.get().getPlugin(BankPlugin::class.java)
        val houseContainer = HouseContainer.instance
        val bizPlugin = ResourceManager.get().getPlugin(BusinessPlugin::class.java)
        val garagePlugin = ResourceManager.get().getPlugin(GaragePlugin::class.java)
        val playerPlugin = ResourceManager.get().getPlugin(PlayerPlugin::class.java)

        if(houseContainer == null || bizPlugin == null || garagePlugin == null) {
            logger.error("Missing house, business or garage plugin player payday failed.")
            return
        }

        if(bankPlugin == null) {
            logger.error("Bank plugin not loaded")
            return
        }

        if(playerPlugin == null) {
            logger.error("Player plugin not found")
            return
        }

        LtrpPlayer.get().forEach { p ->

            val taxes = LtrpWorld.get().taxes

            val houseTax = houseContainer.houses.filter { it.isOwner(p) }.count() * taxes.houseTax
            val businessTax = bizPlugin.businesses.filter { it.isOwner(p) }.count() * taxes.houseTax
            val garageTax = garagePlugin.garages.filter{ it.isOwner(p) }.count() * taxes.garageTax
            // TODO vehicle-tax

            if(p.minutesOnlineSincePayday > 20) {
                val jobData = p.jobData
                var paycheck = 0
                if(jobData != null) {
                    paycheck = jobData.rank.salary
                } else {
                    paycheck = 100
                }

                val bankAccount = bankPlugin.bankController?.getAccount(p)
                val totalTaxes = houseTax + businessTax + garageTax
                p.sendMessage(Color.LIGHTGREEN, "|______________ Los Santos banko ataskaita______________ |")
                p.sendMessage(Color.WHITE, String.format("| Gautas atlyginimas: %d%c | Papildomi mokesèiai: %d%c |", paycheck, Currency.SYMBOL,totalTaxes, Currency.SYMBOL))
                if(bankAccount != null)
                    p.sendMessage(Color.WHITE, String.format("| Buvæs banko balansas: %d%c |", bankAccount.money, Currency.SYMBOL))
                p.sendMessage(Color.WHITE, String.format("| Galutinë gauta suma: %d%c |", paycheck, Currency.SYMBOL))

                if(bankAccount != null) {
                    bankAccount.addMoney(-totalTaxes)
                    bankPlugin.bankController.update(bankAccount)
                    p.sendMessage(Color.WHITE, String.format("| Dabartinis banko balansas: %d%c |", bankAccount.money, Currency.SYMBOL))
                } else {
                    p.giveMoney(-totalTaxes)
                }

                p.addTotalPaycheck(paycheck)
                p.sendMessage(Color.WHITE, String.format("| Sukauptas atlyginimas: %d%c", p.totalPaycheck, Currency.SYMBOL))

                if (houseTax > 0)
                    p.sendMessage(Color.WHITE, String.format("| Mokestis uþ nekilnojama turtà: %d%c |", houseTax, Currency.SYMBOL))
                if (businessTax > 0)
                    p.sendMessage(Color.WHITE, String.format("| Verslo mokestis: %d%c |", businessTax, Currency.SYMBOL))

                p.sendGameText(1, 5000, " ~y~Mokesciai~n~~g~Alga")
                p.onlineHours = p.onlineHours + 1
                p.setMinutesOnlineSincePayday(0)

                playerPlugin.playerController.update(p)
                eventManager.dispatchEvent(PlayerPaydayEvent(p))
            } else {
                p.sendErrorMessage("Apgailestaujame, bet atlyginimo uþ ðià valandà negausite, kadangi Jûs nebuvote prisijungæs pakankamai.")
            }
        }
    }

}