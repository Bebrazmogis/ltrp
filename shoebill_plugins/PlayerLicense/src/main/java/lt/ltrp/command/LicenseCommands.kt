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
    @CommandHelp("Parodo j�s� turimas licenzijas pasirinktam �aid�jui")
    fun licenses(p: Player, @CommandParameter(name = "�aid�jo ID/Dalis vardo") target: LtrpPlayer?): Boolean {
        val player = LtrpPlayer.get(p)
        if (target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!")
        } else if (player.getDistanceToPlayer(target) > 5f) {
            player.sendErrorMessage(target!!.getCharName() + " yra per toli!")
        } else {
            target!!.sendMessage(Color.GREEN, String.format("|________%s licencijos________|", player.getCharName()))
            for (license in player.getLicenses().get()) {
                if (license != null)
                    player.sendMessage(Color.WHITE, String.format("Licenzijos tipas:%s. Etapas: %s I�laikymo data: %s �sp�jim� skai�ius: %d",
                            license!!.getType().getName(), if (license!!.getStage() == 2) "Praktika" else "Teorija", license!!.getDateAquired(), license!!.getWarnings().size))
            }
        }
        return true
    }

}