package lt.ltrp.house.upgrade.command;

import lt.ltrp.LtrpWorld
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.command.Commands
import lt.ltrp.data.Color
import lt.ltrp.house.HouseController
import lt.ltrp.house.upgrade.HouseUpgradeController
import lt.ltrp.house.upgrade.constant.HouseUpgradeType
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandGroup
import net.gtaun.shoebill.common.command.CommandHelp
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 * Commands for managing house upgrades
 */
class HouseOwnerCommands(private var eventManager: EventManager, private var group: CommandGroup):
        Commands() {


    init {
        group.registerCommands(this)
        group.setUsageMessageSupplier { p, s, c ->
            p.sendMessage(Color.GREEN, "__________ Namo patobulinimas __________" )
            p.sendMessage(Color.LIGHTGREY, "Maistas, kaina: $8000 (( /eat ))")
            p.sendMessage(Color.LIGHTRED, "Teisingas komandos naudojimas: /hu maistas")
            ""
        }
        group.setNotFoundHandler { p, group, cmd ->
            p.sendMessage(Color.GREEN, "__________ Namo patobulinimas __________" );
            p.sendMessage(Color.LIGHTGREY, "Maistas, kaina: $8000 (( /eat ))");
            p.sendMessage(Color.LIGHTRED, "Teisingas komandos naudojimas: /hu maistas");
            true
        }
    }

    @Command
    @CommandHelp("Leidþia pirkti atnaujinimus á namà")
    fun maistas(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val house = HouseController.get().get(p.location)
        if(house == null)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdamas name.")
        else if(!house.isOwner(player))
            player.sendErrorMessage("Ðis namas jums nepriklauso")
        else if(house.isUpgradeInstalled(HouseUpgradeType.Refrigerator))
            player.sendErrorMessage("Ðiame name jau yra ðis patobulinimas.")
        else if(player.money < 8000)
            player.sendErrorMessage("Jums neuþtenka pinigø. ")
        else {
            player.giveMoney(-8000)
            val vat = LtrpWorld.get().taxes.getVAT(8000)
            LtrpWorld.get().addMoney(vat)
            HouseUpgradeController.get().insert(house, HouseUpgradeType.Refrigerator)
            player.sendMessage(Color.HOUSE, "Á nama sëkimngai buvo árengta maisto ruoðimo galimybë.")
        }
        return true
    }
}
