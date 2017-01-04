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
            player.sendMessage("�i komanda skirta tik �aid�jams turintiems darb�.")
            return false
        } else
            return true
    }

    @Command fun open(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val jobData = player.jobData ?: return false

        val gate = jobData.job.gates.minBy { it.closedPosition.distance(player.location) }

        if(gate == null || gate.closedPosition.distance(player.location) > 10f)
            player.sendErrorMessage("Prie j�s� n�ra joki� vart�.")
        else if(gate.rank > jobData.rank)
            player.sendErrorMessage("J�s� rangas per �emas kad gal�tum�te naudotis �iais vartais.")
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
            player.sendErrorMessage("Prie j�s� n�ra joki� vart�.")
        else if(gate.rank > jobData.rank)
            player.sendErrorMessage("J�s� rangas per �emas kad gal�tum�te naudotis �iais vartais.")
        else if(!gate.isOpen)
            player.sendErrorMessage("Vartai jau u�daryti!")
        else {
            gate.close()
            player.sendInfoText("Vartai u�daryti")        }
        return true
    }


    @Command
    @CommandHelp("Leid�ia i�eiti i� darbo")
    fun leaveJob(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val jobData = player.jobData

        if(jobData == null)
            player.sendErrorMessage("J�s neturite darbo.")
        else if(jobData.job is Faction)
            player.sendErrorMessage("Kad i�eitum�te i� frakcijos, naudokite /leavefaction")
        else if(jobData.remainingContract > 0)
            player.sendErrorMessage("J�s� kontraktas dar nesibaig�. Jums dar liko " + jobData.remainingContract + " valandos.")
        else {
            PlayerJobController.instance.removeJob(player)
            player.sendMessage(Color.NEWS, "Palikote savo darboviet�, s�km�s!")
        }
        return true
    }
}
