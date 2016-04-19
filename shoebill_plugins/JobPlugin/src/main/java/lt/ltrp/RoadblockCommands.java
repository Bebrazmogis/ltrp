package lt.ltrp;

import lt.ltrp.command.Commands;
import lt.ltrp.data.JobData;
import lt.ltrp.object.Job;
import lt.ltrp.policeman.Roadblock;
import lt.ltrp.policeman.dialog.RoadblockListDialog;
import lt.ltrp.policeman.modelpreview.RoadblockModelPreview;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

import java.util.Optional;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class RoadblockCommands extends Commands {

    private Job job;
    private EventManager eventManager;

    public RoadblockCommands(Job job, EventManager eventManager) {
        this.job = job;
        this.eventManager = eventManager;
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        JobData jobData = JobController.get().getJobData(player);
        if(player.isAdmin() || jobData.getJob().equals(job)) {
            return true;
        } else {
            return false;
        }
    }

    @Command
    @CommandHelp("Pastato uþtvarà, galimi veiksmai create, destroy, edit, manage")
    public boolean roadblock(Player p, String param) {
        LtrpPlayer player = LtrpPlayer.get(p);
        if(player.getVehicle() != null) {
            player.sendErrorMessage("Uþtvaros pastatyti negalite bûdami transporto priemonëje.");
        } else {
            if(param.equalsIgnoreCase("create")) {
                RoadblockModelPreview.create(player, eventManager, (preview, model) -> {
                    AngledLocation location = player.getLocation();
                    location.setX(location.getX() + (float) (3f * Math.sin(-Math.toRadians(location.getAngle()))));
                    location.setY(location.getY() + (float) (3f * Math.cos(-Math.toRadians(location.getAngle()))));
                    Roadblock roadblock = new Roadblock(model, location, new Vector3D());
                    roadblock.getObject().edit(player);
                });
            } else if(param.equalsIgnoreCase("destroy")) {
                AngledLocation location = player.getLocation();
                Optional<Roadblock> roadblock = Roadblock.get().stream().filter(rd -> rd.getLocation().distance(location) < 2f).min((o1, o2) -> Float.compare(o1.getLocation().distance(location), o2.getLocation().distance(location)));
                if(!roadblock.isPresent()) {
                    player.sendErrorMessage("Prie jûsø nëra jokios kelio uþtvaros!");
                } else {
                    roadblock.get().destroy();
                    player.sendMessage("Uþtvaras paðalintas.");
                }
            } else if(param.equalsIgnoreCase("edit")) {
                AngledLocation location = player.getLocation();
                Optional<Roadblock> roadblock = Roadblock.get().stream().filter(rd -> rd.getLocation().distance(location) < 2f).min((o1, o2) -> Float.compare(o1.getLocation().distance(location), o2.getLocation().distance(location)));
                if(!roadblock.isPresent()) {
                    player.sendErrorMessage("Prie jûsø nëra jokios kelio uþtvaros!");
                } else {
                    roadblock.get().getObject().edit(player);
                }
            } else if(param.equalsIgnoreCase("manage")) {
                if(player.isAdmin()) {
                    new RoadblockListDialog(player, eventManager).show();
                }
            }
            return true;
        }
        return false;
    }

}
