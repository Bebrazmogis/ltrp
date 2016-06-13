package lt.ltrp.command;

import lt.ltrp.JobPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.JobListDialog;
import lt.ltrp.object.Job;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.AdminLog;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.13.
 */
public class SetStatGroupCommands {

    private EventManager eventManager;


    public SetStatGroupCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean bc(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        return player.getAdminLevel() >= 4;
    }

    @Command(name = "lygis")
    @CommandHelp("Nustato �aid�jo lyg�")
    public boolean level(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target, @CommandParameter(name = "Naujas �aid�jo lygis")int level) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        else if(level < 1)
            player.sendErrorMessage("Lygis negali b�ti ma�esnis u� 1.");
        else {
            int oldLevel = target.getLevel();
            target.setLevel(level);
            player.sendMessage(Color.GREEN, "�aid�jo " + target.getName() + " lygis pakeistas � " + level);
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeit� j�s� lyg� � " + level);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " level to " + level + ". Old level:" + oldLevel);
        }
        return true;
    }

    @Command(name = "darbas")
    @CommandHelp("Pakei�ia pasirinkto �aid�jo darb�")
    public boolean work(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        else {
            JobListDialog.create(player, eventManager)
                    .onClickOk((d, i) -> {
                        Job job = (Job)i.getData();
                        Job oldJob = JobPlugin.get(JobPlugin.class).getJob(target);
                        JobPlugin.get(JobPlugin.class).setJob(target, job);
                        player.sendMessage(Color.GREEN, target.getName() + " darbas s�kmingai pakeistas");
                        target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeit� j�s� darb� � \"" + job.getName() + "\"");
                        AdminLog.log(player, target, "Changed players " + target.getUUID() + " job from " + oldJob + " to " + job.getUUID());
                    })
                    .build()
                    .show();

        }
        return true;
    }

    @Command(name = "mirtys")
    public boolean deaths(Player p, @CommandParameter(name = "�aid�jo ID/Dalis vardo")LtrpPlayer target, @CommandParameter(name = "Naujas �aid�jo mir�i� skai�ius")int deaths) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio �aid�jo n�ra!");
        else if(deaths < 0)
            player.sendErrorMessage("Mir�i� kiekis negali b�ti ma�esnis u� 0.");
        else {
            int oldDeaths = target.getDeaths();
            target.setDeaths(deaths);
            player.sendMessage(Color.GREEN, "�aid�jo " + target.getName() + " mir�i� skai�ius pakeistas � " + deaths);
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeit� mir�i� skai�i� � " + deaths);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " death count to " + deaths + ". Old death count:" + oldDeaths);
        }
        return true;
    }
}
