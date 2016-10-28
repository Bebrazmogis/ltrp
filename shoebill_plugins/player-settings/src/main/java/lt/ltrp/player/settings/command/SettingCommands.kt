package lt.ltrp.player.settings.command

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.player.settings.dialog.PlayerSettingsListDialog
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-15.
 * Player commands to manage settings
 * Currently only /settings is available as managing is done via GUI
 */
class SettingCommands(private val eventManager: EventManager) {

    @Command
    @CommandHelp("Atidaro þaidimo nustatymø meniu")
    fun settings(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        PlayerSettingsListDialog.create(player, eventManager, player.settings).show()
        return true
    }

}