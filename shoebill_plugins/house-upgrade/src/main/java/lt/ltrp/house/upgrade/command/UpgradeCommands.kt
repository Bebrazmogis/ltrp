package lt.ltrp.house.upgrade.command

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.command.Commands
import lt.ltrp.house.HouseController
import lt.ltrp.house.upgrade.constant.HouseUpgradeType
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp

/**
 * Created by Bebras on 2016-10-15.
 * Commands for using house upgrades
 */
class UpgradeCommands: Commands() {

    @Command
    @CommandHelp("Leid�ia pavalgyti b�nant namuose")
    fun eat(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val house = HouseController.get().get(player.location)
        if (house == null)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�damas name.")
        else if (!house.isUpgradeInstalled(HouseUpgradeType.Refrigerator))
            player.sendErrorMessage("�iame name n�ra k� valgyti...")
        else {
            player.sendActionMessage("paima valgio i� �aldytuvo ir pradeda valgyti.")
            player.health = 100f
        }
        return true
    }

}