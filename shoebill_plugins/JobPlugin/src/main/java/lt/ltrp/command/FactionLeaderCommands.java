package lt.ltrp.command;

import lt.ltrp.FactionInviteOffer;
import lt.ltrp.JobController;
import lt.ltrp.JobPlugin;
import lt.ltrp.constant.Currency;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.dialog.JobRankDialog;
import lt.ltrp.object.Faction;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.AdminLog;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.03.04.
 *
 *         This contains only the commands that are valid for ALL faction leaders
 *         For specific job leader commands see the appropriate package file
 */
public class FactionLeaderCommands extends Commands {

    private JobPlugin jobManager;
    private EventManager eventManager;

    public FactionLeaderCommands(EventManager eventManager, JobPlugin jobManager) {
        this.eventManager = eventManager;
        this.jobManager = jobManager;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Optional<Faction> factionOptional = JobController.get().getFactions().stream().filter(f -> f.getLeaders().contains(player.getUUID())).findFirst();
        return factionOptional.isPresent();
    }

    @Command
    @CommandHelp("Pakvie�ia �aid�j� prisijungti � j�s� frakcij�.")
    public boolean invite(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else if(JobController.get().getJobData(target) != null) {
            player.sendErrorMessage("�is �aid�jas jau turi darb�!");
        } else if(target.getOffer(FactionInviteOffer.class) != null) {
            player.sendErrorMessage("�iam �aid�jui jau ka�kas si�lo prisijungti prie jo frakcijos");
        }
        else {
            Faction f = getLeaderFaction(target);
            FactionInviteOffer offer = new FactionInviteOffer(target, player, eventManager);
            target.getOffers().add(offer);
            target.sendMessage(Color.NEWS, player.getCharName() + " si�lo jums prisijungti prie jo frakcijos " + f.getName());
            target.sendMessage(Color.NEWS, "Ra�ykite /accept faction nor�dami prisijungti arba /decline faction nor�dami atmesti.");
            player.sendMessage(Color.NEWS, "Kvietimas i�si�stas, laukite atsakymo.");
        }
        return true;
    }

    @Command
    public boolean unInvite(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else {
            PlayerJobData jobData = JobController.get().getJobData(target);
            if(jobData.getJob() == null || !jobData.getJob().equals(JobController.get().getJobData(player).getJob())) {
                player.sendErrorMessage(target.getCharName() + " jums nedirba.");
            } else {
                target.sendMessage(Color.NEWS, "Lyderis " + player.getCharName() + " i�met� jus i� darbo.");
                player.sendMessage(Color.NEWS, jobData.getJobRank().getName() + " " + target.getCharName() + " i�mestas i� darbo.");
                JobController.get().setJob(target, null, null);
            }
        }
        return true;
    }

    @Command
    public boolean setRank(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        } else {
            PlayerJobData targetJobData = JobController.get().getJobData(target);
            PlayerJobData playerJobData = JobController.get().getJobData(target);
            if(targetJobData.getJob() == null || !targetJobData.getJob().equals(playerJobData.getJob())) {
                player.sendErrorMessage(target.getCharName() + " jums nedirba.");
            } else {
                JobRankDialog dialog = new JobRankDialog(player, eventManager, playerJobData.getJob());
                dialog.setCaption("Pasirinkite " + target.getCharName() + " rang�.");
                dialog.setButtonOk("Paskirti");
                dialog.setButtonCancel("At�aukti");
                dialog.setClickOkHandler((JobRankDialog.ClickOkHandler)(d, r) -> {
                    if(r.getNumber() > targetJobData.getJobRank().getNumber()) {
                        target.sendMessage(Color.NEWS, playerJobData.getJobRank().getName() + "" + player.getCharName() + " paaauk�tino jus �  "+ r.getName());
                    } else {
                        target.sendMessage(Color.NEWS, playerJobData.getJobRank().getName() + "" + player.getCharName() + " pa�emino jus �  "+ r.getName());
                    }
                    player.sendMessage(Color.NEWS, target.getCharName() + " paskirtas " + r.getName());
                    JobController.get().setRank(target, r);

                });
                dialog.show();
            }
        }
        return true;
    }

    @Command
    @CommandHelp("Patikrina pinig� kiek� frakcijos biud�ete")
    public boolean checkfbudget(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Faction faction = getLeaderFaction(player);
        if(faction == null)
            return false;
        player.sendMessage(Color.GREEN, "|____________FRAKCIJOS BIUD�ETAS____________|");
        player.sendMessage(Color.WHITE, "�uo metu frakcijos biud�ete yra " + faction.getBudget() + Currency.SYMBOL);
        return true;
    }

    @Command
    @CommandHelp("Leid�ia paimti pinig� i� frakcijos biud�eto")
    public boolean takeFMoney(Player p,
                             @CommandParameter(name = "3 lygio administratoriaus ID/Dalis varod")LtrpPlayer admin,
                             @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Faction faction = getLeaderFaction(player);
        if(admin == null)
            return false;
        else if(!admin.isAdmin())
            player.sendErrorMessage(admin.getName() + " n�ra administratorius");
        else if(admin.getAdminLevel() < 3)
            player.sendErrorMessage(admin.getName() + " administratoriaus lygis per ma�as, minimalus yra 3.");
        else if(player.getDistanceToPlayer(admin) > 3f)
            player.sendErrorMessage("Administratorius " + admin.getName() + " yra per toli.");
        else if(amount <= 0)
            player.sendErrorMessage("Suma negali b�ti ma�esn� u� 0.");
        else if(amount > faction.getBudget())
            player.sendErrorMessage("Suma negali b�ti didesn� u� " + faction.getBudget());
        else {
            player.giveMoney(amount);
            faction.setBudget(-amount);
            JobPlugin.get(JobPlugin.class).getFactionDao().update(faction);

            LtrpPlayer.sendAdminMessage(String.format("Miesto meras %s(%d) pa�m� %d i� biud�eto, tai autorizav�s administratorius %s.",
                    player.getName(), player.getId(), amount, admin.getName()));
            AdminLog.log(admin, "Leido paimti " + amount + " i� miesto biud�eto merui " + player.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("�jungia/i�jungia OOC frakcijos pokalbius")
    public boolean nof(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Faction faction = getLeaderFaction(player);
        faction.setChatEnabled(!faction.isChatEnabled());
        if(faction.isChatEnabled())
            faction.sendMessage(Color.CYAN, player.getName() + " i�jung� privat� frakcijos kanal� (/f). ");
        else
            faction.sendMessage(Color.CYAN, player.getName() + " �jung� privat� frakcijos kanal� (/f).");
        jobManager.getFactionDao().update(faction);
        return true;
    }

    private Faction getLeaderFaction(LtrpPlayer leader) {
        Optional<Faction> factionOptional = JobController.get().getFactions().stream().filter(f -> f.getLeaders().contains(leader.getUUID())).findFirst();
        return factionOptional.isPresent() ? factionOptional.get() : null;
    }

}
