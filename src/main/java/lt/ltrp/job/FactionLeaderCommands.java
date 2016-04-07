package lt.ltrp.job;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.Commands;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.JobRankDialog;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
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

    private JobManager jobManager;
    private EventManager eventManager;

    public FactionLeaderCommands(EventManager eventManager, JobManager jobManager) {
        this.eventManager = eventManager;
        this.jobManager = jobManager;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Optional<Faction> factionOptional = JobManager.getFactions().stream().filter(f -> f.getLeaders().contains(player.getUserId())).findFirst();
        return factionOptional.isPresent();
    }

    @Command
    @CommandHelp("Pakvieèia þaidëjà prisijungti á jûsø frakcijà.")
    public boolean invite(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(target.getJob() != null) {
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
        } else if(target.getJob() == null || !target.getJob().equals(player.getJob())) {
            player.sendErrorMessage(target.getCharName() + " jums nedirba.");
        } else {
            target.sendMessage(Color.NEWS, "Lyderis " + player.getCharName() + " iðmetë jus ið darbo.");
            player.sendMessage(Color.NEWS, target.getJobRank().getName() + " " + target.getCharName() + " iðmestas ið darbo.");
            target.setJob(null);
            target.setJobRank(null);
            LtrpGamemode.getDao().getPlayerDao().update(target);
        }
        return true;
    }

    @Command
    public boolean setRank(Player p, LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null) {
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        } else if(target.getJob() == null || !target.getJob().equals(player.getJob())) {
            player.sendErrorMessage(target.getCharName() + " jums nedirba.");
        } else {
            JobRankDialog dialog = new JobRankDialog(player, eventManager, player.getJob());
            dialog.setCaption("Pasirinkite " + target.getCharName() + " rangà.");
            dialog.setButtonOk("Paskirti");
            dialog.setButtonCancel("Atðaukti");
            dialog.setClickOkHandler((JobRankDialog.ClickOkHandler)(d, r) -> {
                if(r.getNumber() > target.getJobRank().getNumber()) {
                    target.sendMessage(Color.NEWS, player.getJobRank().getName() + "" + player.getCharName() + " paaaukðtino jus á  "+ r.getName());
                } else {
                    target.sendMessage(Color.NEWS, player.getJobRank().getName() + "" + player.getCharName() + " paþemino jus á  "+ r.getName());
                }
                player.sendMessage(Color.NEWS, target.getCharName() + " paskirtas " + r.getName());
                target.setJobRank(r);
                LtrpGamemode.getDao().getPlayerDao().update(player);

            });
            dialog.show();
        }
        return true;
    }

    private Faction getLeaderFaction(LtrpPlayer leader) {
        Optional<Faction> factionOptional = JobManager.getFactions().stream().filter(f -> f.getLeaders().contains(leader.getUserId())).findFirst();
        return factionOptional.isPresent() ? factionOptional.get() : null;
    }

}
