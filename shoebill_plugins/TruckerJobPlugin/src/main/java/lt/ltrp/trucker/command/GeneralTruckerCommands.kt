package lt.ltrp.trucker.command

import lt.ltrp.JobPlugin
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.command.Commands
import lt.ltrp.trucker.TruckerJobPlugin
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.BeforeCheck
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp

/**
 * @author Bebras
* 2016.06.19.
 */
class GeneralTruckerCommands(truckerPlugin : TruckerJobPlugin): Commands() {

    val truckerPlugin = truckerPlugin


    @BeforeCheck
    fun checkPermissions(p: Player, cmd: String, params: String): Boolean {
        val player = LtrpPlayer.get(p);
        val jobData = player.jobData
        return jobData != null && jobData.job == truckerPlugin.truckerJob
    }

    @Command
    @CommandHelp("Atidaro industrij� ir versl� informacijos lentel�")
    fun tpda(p: Player): Boolean {

        return true;
    }
}