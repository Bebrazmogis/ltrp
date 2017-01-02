package lt.ltrp.house.rent.command

import lt.ltrp.SpawnPlugin
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.command.Commands
import lt.ltrp.constant.Currency
import lt.ltrp.data.Color
import lt.ltrp.house.event.HouseEditEvent
import lt.ltrp.house.`object`.House
import lt.ltrp.spawn.data.SpawnData
import lt.ltrp.util.ErrorCode
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp
import net.gtaun.shoebill.common.command.CommandParameter
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-12.
 */
class LandlordCommands(val eventManager: EventManager): Commands() {


    @Command
    @CommandHelp("Nustato nuomoti namà ar ne, jei taip nuomos kainà")
    fun setRent(p: Player, @CommandParameter(name = "Nuomos kaina, 0 - nenumuoti") price: Int): Boolean {
        val player = LtrpPlayer.get(p)
        val house = House.get(player) ?: return false

        if (!house.isOwner(player))
            player.sendErrorMessage("Ðis namas jums nepriklauso")
        else if (price < 0)
            player.sendErrorMessage("Nuomos kaina negali bûti neigiama, ar norite mokëti nuomininkams?")
        else {
            house.rentPrice = price
            eventManager.dispatchEvent(HouseEditEvent(house, player))
            player.sendMessage(Color.HOUSE, "Nuomos mokestis pakeistas á " + price + Currency.SYMBOL)
            house.sendTenantMessage("Jûsø nuomuojamo namo savininkas " + player.charName + " pakeitë nuomos mokesti á " + price + Currency.SYMBOL)
            player.playSound(1052)
        }
        return true
    }

    @Command
    @CommandHelp("Paðalina visu jûsø namo nuomininkus")
    fun evictAll(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val house = House.get(player)
        if (house == null)
            return false
        else if (!house.isOwner(player))
            player.sendErrorMessage("Ðis namas jums nepriklauso")
        else {
            val spawnPlugin = getSpawnPlugin()
            if(spawnPlugin == null) {
                player.sendErrorMessage("Nepavyko iðkeldinti nuomininkø. Klaidos kodas " + ErrorCode.MISSING_PLUGIN_SPAWN)
            } else {
                val tenantCount = house.tenants.size
                house.tenants.forEach { tenant ->
                    if (tenant.player.isOnline) {
                        val t = LtrpPlayer.get(tenant.player.UUID)
                        t.sendMessage(Color.HOUSE, " * Jûs buvote iðkeldintas ið nuomojamo namo.")
                    }
                    spawnPlugin.setSpawnData(tenant.player.UUID, SpawnData.DEFAULT)
                    house.tenants.remove(tenant)
                }
                player.sendMessage(Color.HOUSE, "Sëkmingai iðkeldinti " + tenantCount + " nuomininkai.")
            }
        }
        return true
    }

    @Command
    @CommandHelp("Paðalina nuomininkà ið jûsø namo")
    fun evict(p: Player, @CommandParameter(name = "Þaidëjo ID/Dalis vardo") tenant: LtrpPlayer?): Boolean {
        val player = LtrpPlayer.get(p)
        val house = House.get(player)
        if (house == null)
            return false

        else if (!house.isOwner(player))
            player.sendErrorMessage("Ðis namas jums nepriklauso")
        else if (tenant == null || tenant == player)
            player.sendErrorMessage("Tokio þaidëjo nëra!")
        else if (!house.tenants.map { it.UUID }.contains(tenant.uuid))
            player.sendErrorMessage("Ðis þaidëjas nesinomuoja jûsø namo.")
        else {
            val plugin = getSpawnPlugin()
            if(plugin == null) {
                player.sendErrorMessage("Nepavyko iðkeldinti nuomininkø. Klaidos kodas " + ErrorCode.MISSING_PLUGIN_SPAWN)
            } else {
                tenant.sendMessage(Color.HOUSE, " * Jûs buvote iðkeldintas ið nuomojamo namo.")
                house.tenants.remove(house.tenants.find { it.player.UUID == tenant.uuid })
                player.sendMessage(Color.HOUSE, "Nuomininkas " + tenant.name + " iðkeldintas.")
                plugin.setSpawnData(tenant, SpawnData.DEFAULT)
            }
        }
        return true
    }

    @Command
    @CommandHelp("Parodo jûsø namà nuomuojanèiø þmoniø sàraðà")
    fun tenantry(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val house = House.get(player)
        if (house == null)
            return false
        else if (!house.isOwner(player))
            player.sendErrorMessage("Ðis namas jums nepriklauso")
        else if (house.tenants.size == 0)
            player.sendErrorMessage("Jûsø namo niekas nesinomuoja!")
        else {
            player.sendMessage(Color.HOUSE, "__________Nuomininkai__________")
            house.tenants.forEachIndexed { i, tenant ->
                player.sendMessage(Color.WHITE, "" + (i + 1) + "." + tenant.player.name)
            }
        }
        return true
    }

    private fun getSpawnPlugin(): SpawnPlugin? {
        return SpawnPlugin.get(SpawnPlugin::class.java)
    }

}