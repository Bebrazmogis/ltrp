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
    @CommandHelp("Pakvieèia þaidëjà prisijungti á jûsø frakcijà.")
    public boolean invite(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(JobController.get().getJobData(target) != null) {
            player.sendErrorMessage("Ðis þaidëjas jau turi darbà!");
        } else if(target.getOffer(FactionInviteOffer.class) != null) {
            player.sendErrorMessage("Ðiam þaidëjui jau kaþkas siûlo prisijungti prie jo frakcijos");
        }
        else {
            Faction f = getLeaderFaction(target);
            FactionInviteOffer offer = new FactionInviteOffer(target, player, eventManager);
            target.getOffers().add(offer);
            target.sendMessage(Color.NEWS, player.getCharName() + " siûlo jums prisijungti prie jo frakcijos " + f.getName());
            target.sendMessage(Color.NEWS, "Raðykite /accept faction norëdami prisijungti arba /decline faction norëdami atmesti.");
            player.sendMessage(Color.NEWS, "Kvietimas iðsiøstas, laukite atsakymo.");
        }
        return true;
    }

    @Command
    public boolean unInvite(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else {
            PlayerJobData jobData = JobController.get().getJobData(target);
            if(jobData.getJob() == null || !jobData.getJob().equals(JobController.get().getJobData(player).getJob())) {
                player.sendErrorMessage(target.getCharName() + " jums nedirba.");
            } else {
                target.sendMessage(Color.NEWS, "Lyderis " + player.getCharName() + " iðmetë jus ið darbo.");
                player.sendMessage(Color.NEWS, jobData.getJobRank().getName() + " " + target.getCharName() + " iðmestas ið darbo.");
                JobController.get().setJob(target, null, null);
            }
        }
        return true;
    }

    @Command
    public boolean setRank(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else {
            PlayerJobData targetJobData = JobController.get().getJobData(target);
            PlayerJobData playerJobData = JobController.get().getJobData(target);
            if(targetJobData.getJob() == null || !targetJobData.getJob().equals(playerJobData.getJob())) {
                player.sendErrorMessage(target.getCharName() + " jums nedirba.");
            } else {
                JobRankDialog dialog = new JobRankDialog(player, eventManager, playerJobData.getJob());
                dialog.setCaption("Pasirinkite " + target.getCharName() + " rangà.");
                dialog.setButtonOk("Paskirti");
                dialog.setButtonCancel("Atðaukti");
                dialog.setClickOkHandler((JobRankDialog.ClickOkHandler)(d, r) -> {
                    if(r.getNumber() > targetJobData.getJobRank().getNumber()) {
                        target.sendMessage(Color.NEWS, playerJobData.getJobRank().getName() + "" + player.getCharName() + " paaaukðtino jus á  "+ r.getName());
                    } else {
                        target.sendMessage(Color.NEWS, playerJobData.getJobRank().getName() + "" + player.getCharName() + " paþemino jus á  "+ r.getName());
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
    @CommandHelp("Patikrina pinigø kieká frakcijos biudþete")
    public boolean checkfbudget(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Faction faction = getLeaderFaction(player);
        if(faction == null)
            return false;
        player.sendMessage(Color.GREEN, "|____________FRAKCIJOS BIUDÞETAS____________|");
        player.sendMessage(Color.WHITE, "Ðuo metu frakcijos biudþete yra " + faction.getBudget() + Currency.SYMBOL);
        return true;
    }

    @Command
    @CommandHelp("Leidþia paimti pinigø ið frakcijos biudþeto")
    public boolean takeFMoney(Player p,
                             @CommandParameter(name = "3 lygio administratoriaus ID/Dalis varod")LtrpPlayer admin,
                             @CommandParameter(name = "Suma")int amount) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Faction faction = getLeaderFaction(player);
        if(admin == null)
            return false;
        else if(!admin.isAdmin())
            player.sendErrorMessage(admin.getName() + " nëra administratorius");
        else if(admin.getAdminLevel() < 3)
            player.sendErrorMessage(admin.getName() + " administratoriaus lygis per maþas, minimalus yra 3.");
        else if(player.getDistanceToPlayer(admin) > 3f)
            player.sendErrorMessage("Administratorius " + admin.getName() + " yra per toli.");
        else if(amount <= 0)
            player.sendErrorMessage("Suma negali bûti maþesnë uþ 0.");
        else if(amount > faction.getBudget())
            player.sendErrorMessage("Suma negali bûti didesnë uþ " + faction.getBudget());
        else {
            player.giveMoney(amount);
            faction.setBudget(-amount);
            JobPlugin.get(JobPlugin.class).getFactionDao().update(faction);

            LtrpPlayer.sendAdminMessage(String.format("Miesto meras %s(%d) paëmë %d ið biudþeto, tai autorizavæs administratorius %s.",
                    player.getName(), player.getId(), amount, admin.getName()));
            AdminLog.log(admin, "Leido paimti " + amount + " ið miesto biudþeto merui " + player.getUUID());
        }
        return true;
    }

    @Command
    @CommandHelp("Ájungia/iðjungia OOC frakcijos pokalbius")
    public boolean nof(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Faction faction = getLeaderFaction(player);
        faction.setChatEnabled(!faction.isChatEnabled());
        if(faction.isChatEnabled())
            faction.sendMessage(Color.CYAN, player.getName() + " iðjungë privatø frakcijos kanalà (/f). ");
        else
            faction.sendMessage(Color.CYAN, player.getName() + " ájungë privatø frakcijos kanalà (/f).");
        jobManager.getFactionDao().update(faction);
        return true;
    }

    private Faction getLeaderFaction(LtrpPlayer leader) {
        Optional<Faction> factionOptional = JobController.get().getFactions().stream().filter(f -> f.getLeaders().contains(leader.getUUID())).findFirst();
        return factionOptional.isPresent() ? factionOptional.get() : null;
    }

}
