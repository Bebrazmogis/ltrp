package lt.ltrp;

import lt.ltrp.command.Commands;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.dialog.JobRankDialog;
import lt.ltrp.object.Faction;
import lt.ltrp.object.LtrpPlayer;
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

    private Faction getLeaderFaction(LtrpPlayer leader) {
        Optional<Faction> factionOptional = JobController.get().getFactions().stream().filter(f -> f.getLeaders().contains(leader.getUUID())).findFirst();
        return factionOptional.isPresent() ? factionOptional.get() : null;
    }

}
