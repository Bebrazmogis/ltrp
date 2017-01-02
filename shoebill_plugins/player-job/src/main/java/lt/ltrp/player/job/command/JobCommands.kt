package lt.ltrp.player.job.command

import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.command.Commands
import lt.ltrp.data.Color
import lt.ltrp.job.`object`.Faction
import lt.ltrp.player.job.PlayerJobController
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.BeforeCheck
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp

/**
 * @author Bebras
 *         2016.03.04.
 *
 *        Common commands that all players that have a job can use
 */
class JobCommands: Commands() {



    @BeforeCheck fun beforeCheck(p: Player, cmd: String, params: String): Boolean {
        val player = LtrpPlayer.get(p)
        if(player.jobData == null) {
            player.sendMessage("Ði komanda skirta tik þaidëjams turintiems darbà.")
            return false
        } else
            return true
    }

    @Command fun open(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val jobData = player.jobData ?: return false

        val gate = jobData.job.gates.minBy { it.closedPosition.distance(player.location) }

        if(gate == null || gate.closedPosition.distance(player.location) > 10f)
            player.sendErrorMessage("Prie jûsø nëra jokiø vartø.")
        else if(gate.rank > jobData.rank)
            player.sendErrorMessage("Jûsø rangas per þemas kad galëtumëte naudotis ðiais vartais.")
        else if(gate.isOpen)
            player.sendErrorMessage("Vartai jau atidaryti!")
        else {
            gate.open()
            player.sendInfoText("Vartai atidaryti")
        }
        return true
    }

    @Command fun close(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val jobData = player.jobData ?: return false

        val gate = jobData.job.gates.minBy { it.closedPosition.distance(player.location) }

        if(gate == null || gate.closedPosition.distance(player.location) > 10f)
            player.sendErrorMessage("Prie jûsø nëra jokiø vartø.")
        else if(gate.rank > jobData.rank)
            player.sendErrorMessage("Jûsø rangas per þemas kad galëtumëte naudotis ðiais vartais.")
        else if(!gate.isOpen)
            player.sendErrorMessage("Vartai jau uþdaryti!")
        else {
            gate.close()
            player.sendInfoText("Vartai uþdaryti")        }
        return true
    }


    @Command
    @CommandHelp("Leidþia iðeiti ið darbo")
    fun leaveJob(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val jobData = player.jobData

        if(jobData == null)
            player.sendErrorMessage("Jûs neturite darbo.")
        else if(jobData.job is Faction)
            player.sendErrorMessage("Kad iðeitumëte ið frakcijos, naudokite /leavefaction")
        else if(jobData.remainingContract > 0)
            player.sendErrorMessage("Jûsø kontraktas dar nesibaigë. Jums dar liko " + jobData.remainingContract + " valandos.")
        else {
            PlayerJobController.instance.removeJob(player)
            player.sendMessage(Color.NEWS, "Palikote savo darbovietæ, sëkmës!")
        }
        return true
    }
}
