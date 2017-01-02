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
    @CommandHelp("Pradeda namo nuom�")
    fun rentRoom(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val house = House.getClosest(player.location, 8f)
        val spawnPlugin = getSpawnPlugin()
        if (house == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami prie nuomuojamo namo ��jimo.")
        else if (house.owner == LtrpPlayer.INVALID_USER_ID || house.rentPrice == 0)
            player.sendErrorMessage("Namas n�ra nuomuojamas!")
        else if (house.rentPrice > player.money)
            player.sendErrorMessage("Neturite pakankamai pinig� pradiniam nuomos mokes�iui.")
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
            player.sendMessage(Color.HOUSE, "Sveikiname, s�kmingai i�sinuomavote kambar� �iame name. Nusitatykite atsiradimo viet� su komanda /setspawn.")
            player.sendMessage(Color.HOUSE, "Po kiekvienos algos, i� j�s� banko s�skaitos bus nuskai�iuojamas nuomos mokestis(" + house.rentPrice + Currency.SYMBOL + ")")
        }
        return true
    }

    @Command
    @CommandHelp("Leid�ia atsisakyti dabartin�s namo nuomos")
    fun unRent(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val spawnPlugin = getSpawnPlugin()
        if(spawnPlugin == null)
            player.sendErrorMessage(ErrorCode.MISSING_PLUGIN_SPAWN)
        else {
            val spawnData = spawnPlugin.getSpawnData(player)
            if (spawnData == null || spawnData.type !== SpawnData.SpawnType.House)
                player.sendErrorMessage("J�s nesinomuojate namo!")
            else {
                player.playSound(1052)
                val house = House.get(spawnData.id)
                rentPlugin.removeTenant(house, player)
                player.sendMessage(Color.HOUSE, "Sveikiname, s�kmingai atsisak�te dabartinio gyvenamojo namo nuomos. Nuo �iol atsirasite nebe �iame name.")
                spawnPlugin.setSpawnData(player, SpawnData.DEFAULT)
            }
        }
        return true
    }

    private fun getSpawnPlugin(): SpawnPlugin? {
        return SpawnPlugin.get(SpawnPlugin::class.java)
    }
}
