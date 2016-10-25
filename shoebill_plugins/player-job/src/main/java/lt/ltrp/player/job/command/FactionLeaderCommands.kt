package lt.ltrp.player.job.command

import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.command.Commands
import lt.ltrp.constant.Currency
import lt.ltrp.data.Color
import lt.ltrp.job.JobController
import lt.ltrp.job.`object`.Faction
import lt.ltrp.job.`object`.JobRank
import lt.ltrp.job.dialog.JobRankDialog
import lt.ltrp.player.job.PlayerJobController
import lt.ltrp.player.job.data.FactionInviteOffer
import lt.ltrp.util.AdminLog
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.BeforeCheck
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandHelp
import net.gtaun.shoebill.common.command.CommandParameter
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 *         2016.03.04.
 *
 *         This contains only the commands that are valid for ALL faction leaders
 *         For specific job leader commands see the appropriate package file
 */
class FactionLeaderCommands(private val eventManager: EventManager): Commands() {

    @BeforeCheck fun beforeCheck(p: Player, cmd: String, params: String): Boolean {
        val player = LtrpPlayer.get(p)
        return JobController.instance.get()
            .filterIsInstance<Faction>()
            .firstOrNull{ it.leaders.contains(player) } != null
    }

    @Command
    @CommandHelp("Pakvieèia þaidëjà prisijungti á jûsø frakcijà.")
    fun invite(p: Player, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")target: LtrpPlayer): Boolean {
        val player = LtrpPlayer.get(p)
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!")
        } else if(target.jobData != null) {
            player.sendErrorMessage("Ðis þaidëjas jau turi darbà!")
        } else if(target.containsOffer(FactionInviteOffer::class.java)) {
            player.sendErrorMessage("Ðiam þaidëjui jau kaþkas siûlo prisijungti prie jo frakcijos")
        }
        else {
            val f = player.jobData?.job as Faction
            val offer = FactionInviteOffer(target, player, f, eventManager)
            target.offers.add(offer)
            target.sendMessage(Color.NEWS, player.charName + " siûlo jums prisijungti prie jo frakcijos " + f.name)
            target.sendMessage(Color.NEWS, "Raðykite /accept faction norëdami prisijungti arba /decline faction norëdami atmesti.")
            player.sendMessage(Color.NEWS, "Kvietimas iðsiøstas, laukite atsakymo.")
        }
        return true
    }

    @Command
    fun unInvite(p: Player, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")target: LtrpPlayer): Boolean {
        val player = LtrpPlayer.get(p)
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!")
        } else {
            val jobData = player.jobData
            if(jobData == null || jobData != target.jobData) {
                player.sendErrorMessage(target.charName + " jums nedirba.")
            } else {
                target.sendMessage(Color.NEWS, "Lyderis " + player.charName + " iðmetë jus ið darbo.");
                player.sendMessage(Color.NEWS, jobData.rank.name + " " + target.charName + " iðmestas ið darbo.");
                PlayerJobController.instance.removeJob(target)
            }
        }
        return true;
    }

    @Command
    fun setRank(p: Player, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")target: LtrpPlayer): Boolean {
        val player = LtrpPlayer.get(p)
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!")
        } else {
            val targetJobData = target.jobData
            val playerJobData = player.jobData
            if(targetJobData == null || playerJobData == null|| targetJobData.job != playerJobData.job) {
                player.sendErrorMessage(target.charName + " jums nedirba.")
            } else {
                val dialog = JobRankDialog(player, eventManager, playerJobData.job)
                dialog.caption = "Pasirinkite " + target.charName + " rangà."
                dialog.buttonOk = "Paskirti";
                dialog.buttonCancel = "Atðaukti";
                dialog.setClickOkHandler { dialog: JobRankDialog?, rank: JobRank ->
                    if(rank > targetJobData.rank) {
                        target.sendMessage(Color.NEWS, playerJobData.rank.name + "" + player.charName + " paaaukðtino jus á  "+ rank.name)
                    } else {
                        target.sendMessage(Color.NEWS, playerJobData.rank.name + "" + player.charName + " paþemino jus á  "+ rank.name)
                    }
                    player.sendMessage(Color.NEWS, target.charName + " paskirtas " + rank.name)
                    PlayerJobController.instance.setRank(target, rank)
                }
                dialog.show()
            }
        }
        return true
    }

    @Command
    @CommandHelp("Patikrina pinigø kieká frakcijos biudþete")
    fun checkfbudget(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val faction = player.jobData?.job as? Faction ?: return false

        player.sendMessage(Color.GREEN, "|____________FRAKCIJOS BIUDÞETAS____________|")
        player.sendMessage(Color.WHITE, "Ðuo metu frakcijos biudþete yra " + faction.budget + Currency.SYMBOL)
        return true
    }

    @Command
    @CommandHelp("Leidþia paimti pinigø ið frakcijos biudþeto")
    fun takeFMoney(p: Player,
                             @CommandParameter(name = "3 lygio administratoriaus ID/Dalis vardo")admin: LtrpPlayer,
                             @CommandParameter(name = "Suma")amount: Int): Boolean {
        val player = LtrpPlayer.get(p)
        val faction = player.jobData?.job as? Faction ?: return false
        if(admin == null)
            return false
        else if(!admin.isAdmin)
            player.sendErrorMessage(admin.name + " nëra administratorius")
        else if(admin.adminLevel < 3)
            player.sendErrorMessage(admin.name + " administratoriaus lygis per maþas, minimalus yra 3.")
        else if(player.getDistanceToPlayer(admin) > 3f)
            player.sendErrorMessage("Administratorius " + admin.name + " yra per toli.")
        else if(amount <= 0)
            player.sendErrorMessage("Suma negali bûti maþesnë uþ 0.")
        else if(amount > faction.budget)
            player.sendErrorMessage("Suma negali bûti didesnë uþ " + faction.budget)
        else {
            player.giveMoney(amount)
            faction.budget -= amount
            JobController.instance.update(faction)

            LtrpPlayer.sendAdminMessage(String.format("Miesto meras %s(%d) paëmë %d ið biudþeto, tai autorizavæs administratorius %s.",
                    player.name, player.id, amount, admin.name))
            AdminLog.log(admin, "Leido paimti " + amount + " ið miesto biudþeto merui " + player.uuid)
        }
        return true;
    }

    @Command
    @CommandHelp("Ájungia/iðjungia OOC frakcijos pokalbius")
    fun nof(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val faction = player.jobData?.job as? Faction ?: return false
        faction.isChatEnabled = !faction.isChatEnabled
        if(!faction.isChatEnabled)
            faction.sendMessage(Color.CYAN, player.name + " iðjungë privatø frakcijos kanalà (/f). ")
        else
            faction.sendMessage(Color.CYAN, player.name + " ájungë privatø frakcijos kanalà (/f).")
        JobController.instance.update(faction)
        return true
    }

}
