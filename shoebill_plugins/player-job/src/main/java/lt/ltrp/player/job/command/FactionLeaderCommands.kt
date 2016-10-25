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
    @CommandHelp("Pakvie�ia �aid�j� prisijungti � j�s� frakcij�.")
    fun invite(p: Player, @CommandParameter(name = "�aid�jo ID/Dalis vardo")target: LtrpPlayer): Boolean {
        val player = LtrpPlayer.get(p)
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!")
        } else if(target.jobData != null) {
            player.sendErrorMessage("�is �aid�jas jau turi darb�!")
        } else if(target.containsOffer(FactionInviteOffer::class.java)) {
            player.sendErrorMessage("�iam �aid�jui jau ka�kas si�lo prisijungti prie jo frakcijos")
        }
        else {
            val f = player.jobData?.job as Faction
            val offer = FactionInviteOffer(target, player, f, eventManager)
            target.offers.add(offer)
            target.sendMessage(Color.NEWS, player.charName + " si�lo jums prisijungti prie jo frakcijos " + f.name)
            target.sendMessage(Color.NEWS, "Ra�ykite /accept faction nor�dami prisijungti arba /decline faction nor�dami atmesti.")
            player.sendMessage(Color.NEWS, "Kvietimas i�si�stas, laukite atsakymo.")
        }
        return true
    }

    @Command
    fun unInvite(p: Player, @CommandParameter(name = "�aid�jo ID/Dalis vardo")target: LtrpPlayer): Boolean {
        val player = LtrpPlayer.get(p)
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!")
        } else {
            val jobData = player.jobData
            if(jobData == null || jobData != target.jobData) {
                player.sendErrorMessage(target.charName + " jums nedirba.")
            } else {
                target.sendMessage(Color.NEWS, "Lyderis " + player.charName + " i�met� jus i� darbo.");
                player.sendMessage(Color.NEWS, jobData.rank.name + " " + target.charName + " i�mestas i� darbo.");
                PlayerJobController.instance.removeJob(target)
            }
        }
        return true;
    }

    @Command
    fun setRank(p: Player, @CommandParameter(name = "�aid�jo ID/Dalis vardo")target: LtrpPlayer): Boolean {
        val player = LtrpPlayer.get(p)
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!")
        } else {
            val targetJobData = target.jobData
            val playerJobData = player.jobData
            if(targetJobData == null || playerJobData == null|| targetJobData.job != playerJobData.job) {
                player.sendErrorMessage(target.charName + " jums nedirba.")
            } else {
                val dialog = JobRankDialog(player, eventManager, playerJobData.job)
                dialog.caption = "Pasirinkite " + target.charName + " rang�."
                dialog.buttonOk = "Paskirti";
                dialog.buttonCancel = "At�aukti";
                dialog.setClickOkHandler { dialog: JobRankDialog?, rank: JobRank ->
                    if(rank > targetJobData.rank) {
                        target.sendMessage(Color.NEWS, playerJobData.rank.name + "" + player.charName + " paaauk�tino jus �  "+ rank.name)
                    } else {
                        target.sendMessage(Color.NEWS, playerJobData.rank.name + "" + player.charName + " pa�emino jus �  "+ rank.name)
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
    @CommandHelp("Patikrina pinig� kiek� frakcijos biud�ete")
    fun checkfbudget(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val faction = player.jobData?.job as? Faction ?: return false

        player.sendMessage(Color.GREEN, "|____________FRAKCIJOS BIUD�ETAS____________|")
        player.sendMessage(Color.WHITE, "�uo metu frakcijos biud�ete yra " + faction.budget + Currency.SYMBOL)
        return true
    }

    @Command
    @CommandHelp("Leid�ia paimti pinig� i� frakcijos biud�eto")
    fun takeFMoney(p: Player,
                             @CommandParameter(name = "3 lygio administratoriaus ID/Dalis vardo")admin: LtrpPlayer,
                             @CommandParameter(name = "Suma")amount: Int): Boolean {
        val player = LtrpPlayer.get(p)
        val faction = player.jobData?.job as? Faction ?: return false
        if(admin == null)
            return false
        else if(!admin.isAdmin)
            player.sendErrorMessage(admin.name + " n�ra administratorius")
        else if(admin.adminLevel < 3)
            player.sendErrorMessage(admin.name + " administratoriaus lygis per ma�as, minimalus yra 3.")
        else if(player.getDistanceToPlayer(admin) > 3f)
            player.sendErrorMessage("Administratorius " + admin.name + " yra per toli.")
        else if(amount <= 0)
            player.sendErrorMessage("Suma negali b�ti ma�esn� u� 0.")
        else if(amount > faction.budget)
            player.sendErrorMessage("Suma negali b�ti didesn� u� " + faction.budget)
        else {
            player.giveMoney(amount)
            faction.budget -= amount
            JobController.instance.update(faction)

            LtrpPlayer.sendAdminMessage(String.format("Miesto meras %s(%d) pa�m� %d i� biud�eto, tai autorizav�s administratorius %s.",
                    player.name, player.id, amount, admin.name))
            AdminLog.log(admin, "Leido paimti " + amount + " i� miesto biud�eto merui " + player.uuid)
        }
        return true;
    }

    @Command
    @CommandHelp("�jungia/i�jungia OOC frakcijos pokalbius")
    fun nof(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val faction = player.jobData?.job as? Faction ?: return false
        faction.isChatEnabled = !faction.isChatEnabled
        if(!faction.isChatEnabled)
            faction.sendMessage(Color.CYAN, player.name + " i�jung� privat� frakcijos kanal� (/f). ")
        else
            faction.sendMessage(Color.CYAN, player.name + " �jung� privat� frakcijos kanal� (/f).")
        JobController.instance.update(faction)
        return true
    }

}
