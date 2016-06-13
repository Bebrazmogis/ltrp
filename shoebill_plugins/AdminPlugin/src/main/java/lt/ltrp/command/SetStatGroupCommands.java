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
    @CommandHelp("Nustato þaidëjo lygá")
    public boolean level(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target, @CommandParameter(name = "Naujas þaidëjo lygis")int level) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        else if(level < 1)
            player.sendErrorMessage("Lygis negali bûti maþesnis uþ 1.");
        else {
            int oldLevel = target.getLevel();
            target.setLevel(level);
            player.sendMessage(Color.GREEN, "Þaidëjo " + target.getName() + " lygis pakeistas á " + level);
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeitë jûsø lygá á " + level);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " level to " + level + ". Old level:" + oldLevel);
        }
        return true;
    }

    @Command(name = "darbas")
    @CommandHelp("Pakeièia pasirinkto þaidëjo darbà")
    public boolean work(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        else {
            JobListDialog.create(player, eventManager)
                    .onClickOk((d, i) -> {
                        Job job = (Job)i.getData();
                        Job oldJob = JobPlugin.get(JobPlugin.class).getJob(target);
                        JobPlugin.get(JobPlugin.class).setJob(target, job);
                        player.sendMessage(Color.GREEN, target.getName() + " darbas sëkmingai pakeistas");
                        target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeitë jûsø darbà á \"" + job.getName() + "\"");
                        AdminLog.log(player, target, "Changed players " + target.getUUID() + " job from " + oldJob + " to " + job.getUUID());
                    })
                    .build()
                    .show();

        }
        return true;
    }

    @Command(name = "mirtys")
    public boolean deaths(Player p, @CommandParameter(name = "Þaidëjo ID/Dalis vardo")LtrpPlayer target, @CommandParameter(name = "Naujas þaidëjo mirèiø skaièius")int deaths) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(target == null)
            player.sendErrorMessage("Tokio þaidëjo nëra!");
        else if(deaths < 0)
            player.sendErrorMessage("Mirèiø kiekis negali bûti maþesnis uþ 0.");
        else {
            int oldDeaths = target.getDeaths();
            target.setDeaths(deaths);
            player.sendMessage(Color.GREEN, "Þaidëjo " + target.getName() + " mirèiø skaièius pakeistas á " + deaths);
            target.sendMessage(Color.GREEN, "Administratorius " + player.getName() + " pakeitë mirèiø skaièiø á " + deaths);
            AdminLog.log(player, target, "Changed players " + target.getUUID() + " death count to " + deaths + ". Old death count:" + oldDeaths);
        }
        return true;
    }
}
