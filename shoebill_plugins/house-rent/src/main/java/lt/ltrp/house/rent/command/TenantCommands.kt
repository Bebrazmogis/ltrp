package lt.ltrp.house.rent.command

import lt.ltrp.SpawnPlugin
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.command.Commands
import lt.ltrp.constant.Currency
import lt.ltrp.data.Color
import lt.ltrp.house.`object`.House
import lt.ltrp.house.rent.HouseRentPlugin
import lt.ltrp.spawn.data.SpawnData
import lt.ltrp.util.ErrorCode
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp

/**
 * Created by Bebras on 2016-10-12.
 *
 */
class TenantCommands(val rentPlugin: HouseRentPlugin): Commands() {

    @Command
    @CommandHelp("Pradeda namo nuomà")
    fun rentRoom(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val house = House.getClosest(player.location, 8f)
        val spawnPlugin = getSpawnPlugin()
        if (house == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdami prie nuomuojamo namo áëjimo.")
        else if (house.owner == LtrpPlayer.INVALID_USER_ID || house.rentPrice == 0)
            player.sendErrorMessage("Namas nëra nuomuojamas!")
        else if (house.rentPrice > player.money)
            player.sendErrorMessage("Neturite pakankamai pinigø pradiniam nuomos mokesèiui.")
        else if(spawnPlugin == null)
            player.sendErrorMessage(ErrorCode.MISSING_PLUGIN_SPAWN)
        else {
            player.playSound(1052)
            rentPlugin.insertTenant(house, player)
            house.addMoney(house.rentPrice)
            player.giveMoney(-house.rentPrice)
            val spawnData = spawnPlugin.getSpawnData(player)
            spawnData.type = SpawnData.SpawnType.House
            spawnData.id = house.UUID
            spawnPlugin.setSpawnData(player, spawnData)
            player.sendMessage(Color.HOUSE, "Sveikiname, sëkmingai iðsinuomavote kambará ðiame name. Nusitatykite atsiradimo vietà su komanda /setspawn.")
            player.sendMessage(Color.HOUSE, "Po kiekvienos algos, ið jûsø banko sàskaitos bus nuskaièiuojamas nuomos mokestis(" + house.rentPrice + Currency.SYMBOL + ")")
        }
        return true
    }

    @Command
    @CommandHelp("Leidþia atsisakyti dabartinës namo nuomos")
    fun unRent(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val spawnPlugin = getSpawnPlugin()
        if(spawnPlugin == null)
            player.sendErrorMessage(ErrorCode.MISSING_PLUGIN_SPAWN)
        else {
            val spawnData = spawnPlugin.getSpawnData(player)
            if (spawnData == null || spawnData.type !== SpawnData.SpawnType.House)
                player.sendErrorMessage("Jûs nesinomuojate namo!")
            else {
                player.playSound(1052)
                val house = House.get(spawnData.id)
                rentPlugin.removeTenant(house, player)
                player.sendMessage(Color.HOUSE, "Sveikiname, sëkmingai atsisakëte dabartinio gyvenamojo namo nuomos. Nuo ðiol atsirasite nebe ðiame name.")
                spawnPlugin.setSpawnData(player, SpawnData.DEFAULT)
            }
        }
        return true
    }

    private fun getSpawnPlugin(): SpawnPlugin? {
        return SpawnPlugin.get(SpawnPlugin::class.java)
    }
}
