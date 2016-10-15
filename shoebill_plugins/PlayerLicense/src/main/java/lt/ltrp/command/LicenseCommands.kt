package lt.ltrp.command

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.data.Color
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp
import net.gtaun.shoebill.common.command.CommandParameter

/**
 * Created by Bebras on 2016-10-15.
 */
class LicenseCommands {

    @Command
    @CommandHelp("Parodo jûsø turimas licenzijas pasirinktam þaidëjui")
    fun licenses(p: Player, @CommandParameter(name = "Þaidëjo ID/Dalis vardo") target: LtrpPlayer?): Boolean {
        val player = LtrpPlayer.get(p)
        if (target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!")
        } else if (player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target!!.getCharName() + " yra per toli!")
        } else {
            target!!.sendMessage(Color.GREEN, String.format("|________%s licencijos________|", player.getCharName()))
            for (license in player.getLicenses().get()) {
                if (license != null)
                    player.sendMessage(Color.WHITE, String.format("Licenzijos tipas:%s. Etapas: %s Iðlaikymo data: %s Áspëjimø skaièius: %d",
                            license!!.getType().getName(), if (license!!.getStage() == 2) "Praktika" else "Teorija", license!!.getDateAquired(), license!!.getWarnings().size))
            }
        }
        return true
    }

}