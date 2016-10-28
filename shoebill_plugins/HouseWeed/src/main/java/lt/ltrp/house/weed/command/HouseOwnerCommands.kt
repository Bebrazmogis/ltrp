package lt.ltrp.house.weed.command

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.WeedItem
import lt.ltrp.command.Commands
import lt.ltrp.data.Color
import lt.ltrp.house.HouseController
import lt.ltrp.house.`object`.House
import lt.ltrp.house.weed.HouseWeedController
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.BeforeCheck
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-14.
 * Commands for managing weed to house owners
 */
class HouseOwnerCommands(private val eventManager: EventManager): Commands() {

    @BeforeCheck fun bc(p: Player, cmd: String, params: String): Boolean {
        val house = HouseController.get().get(p.location)
        val player = LtrpPlayer.get(p)
        return house.isOwner(player)
    }

    @Command
    @CommandHelp("Nuima u�augint� �ol� namuose")
    fun cutWeed(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val house = House.get(player)
        if(house == null || !house.isOwner(player))
            player.sendErrorMessage("Tai ne j�s� namas.")
        else if(house.weedSaplings.size == 0)
            player.sendErrorMessage("J�s� namusoe neauga �ol�.")
        else if(player.inventory!!.isFull)
            player.sendErrorMessage("J�s� inventorius pilnas.")
        else {
            var totalYield = 0
            val grownSaplings = house.weedSaplings.filter { it.isGrown() }
            grownSaplings.forEach {
                totalYield += it.yieldAmount
                it.harvestedBy = player
                it.destroy()
                HouseWeedController.instance.updateWeed(it)
            }
            val weed = WeedItem.create(eventManager)
            weed.amount = totalYield
            player.inventory!!.add(weed)
            player.sendMessage(Color.FORESTGREEN, "S�kmingai nu�m�te derli�. I� viso pavyko u�auginti " + totalYield + "gramus i� " + grownSaplings.size + " augal�.")
        }
        return true
    }

}