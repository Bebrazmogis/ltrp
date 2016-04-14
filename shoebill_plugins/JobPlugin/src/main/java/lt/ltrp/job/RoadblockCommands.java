package lt.ltrp.job;

import lt.ltrp.LtrpGamemodeImpl;
import lt.ltrp.command.Commands;
import lt.ltrp.dialog.RoadblockDialog;
import lt.ltrp.dialog.RoadblockListDialog;
import lt.ltrp.job.medic.MedicManager;
import lt.ltrp.job.policeman.PolicemanManager;
import lt.ltrp.job.policeman.Roadblock;
import lt.ltrp.job.policeman.modelpreview.RoadblockModelPreview;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.data.AngledLocation;
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
    public boolean beforeCheck(LtrpPlayer player, String cmd, String params) {
        if(player.isAdmin() || player.getJob().equals(job)) {
            return true;
        } else {
            return false;
        }
    }

    @Command
    @CommandHelp("Pastato uþtvarà, galimi veiksmai create, destroy, edit, manage")
    public boolean roadblock(LtrpPlayer player, String param) {
        if(player.getVehicle() != null) {
            player.sendErrorMessage("Uþtvaros pastatyti negalite bûdami transporto priemonëje.");
        } else {
            if(param.equalsIgnoreCase("create")) {
                RoadblockModelPreview.create(player, eventManager, (preview, model) -> {
                    AngledLocation location = player.getLocation();
                    location.setX(location.getX() + (float) (3f * Math.sin(-Math.toRadians(location.getAngle()))));
                    location.setY(location.getY() + (float) (3f * Math.cos(-Math.toRadians(location.getAngle()))));
                    Roadblock roadblock = new Roadblock(model, location);
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
                    new RoadblockListDialog(player, LtrpGamemode.get().getEventManager()).show();
                }
            }
            return true;
        }
        return false;
    }

}
