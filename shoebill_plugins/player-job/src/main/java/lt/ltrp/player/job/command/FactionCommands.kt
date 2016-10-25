package lt.ltrp.player.job.command;


import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.command.Commands
import lt.ltrp.data.Color
import lt.ltrp.job.JobController
import lt.ltrp.job.`object`.Faction
import lt.ltrp.player.job.PlayerJobController
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp
import net.gtaun.shoebill.common.command.CommandParameter
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 *         2016.03.04.
 */
class FactionCommands(private val eventManager: EventManager): Commands() {

    /*/ @BeforeCheck
        public boolean beforeCheck(Player p, String cmd, String params) {
            LtrpPlayer player = LtrpPlayer.get(p);
            PlayerJobData jobData = JobController.get().getJobData(player);
            Job job = jobData.getJob();
            if(job != null && job instanceof Faction) {
                return true;
            } else {
                return false;
            }
        }
    */

    @Command
    @CommandHelp("Iðsiunèia OOC þinutæ á frakcijos chatà")
    fun f(p: Player, @CommandParameter(name = "Tekstas")text: String): Boolean {
        val player = LtrpPlayer.get(p)
        val jobData = player.jobData
        if(player.isMuted)
            player.sendErrorMessage("Jums buvo uþdrausta kalbëti!")
        if(text == null || jobData == null)
            return false

        else if(jobData.job !is Faction)
            player.sendErrorMessage("Ðis chatas galimas tik dirbant frakcijoje.")

        else if(!((jobData.job as Faction).isChatEnabled))
            player.sendErrorMessage("Frakcijos chatas yra iðjungtas.")

        else {
            jobData.job.sendMessage(Color.CYAN, String.format("((%s (%s): %s ))", jobData.rank.name,
                    player.name, text))
        }
        return true
    }

    @Command
    fun fList(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val faction = player.jobData?.job as? Faction
        if(faction == null)
            player.sendErrorMessage("Jûs nepriklausote jokiai frakcijai")
        else {
            val members = JobController.instance.getEmployeeList(faction)
            player.sendMessage(Color.GREEN, "|___________ Frakcijai priklausantys nariai(" + members.size + ") ___________|")
            members.forEachIndexed { i, data ->
                val jobData = PlayerJobController.instance.getData(data)
                player.sendMessage(Color.WHITE, String.format("**%d. %s [%d - %s]",
                        i + 1,
                        data.name,
                        jobData.rank.number,
                        jobData.rank.name))
            }
        }
        return true
    }

    @Command
    @CommandHelp("Leidþia iðeiði ið frakcijos")
    fun leaveFaction(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        if(player.jobData == null || player.jobData?.job !is Faction)
            player.sendErrorMessage("Jûs neturite darbo.")
        else {
            PlayerJobController.instance.removeJob(player)
            player.sendMessage(Color.NEWS, "Sëkmingai iðëjote ið frakcijos.")
        }
        return true
    }



}
