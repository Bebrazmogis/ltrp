package lt.ltrp.job.policeman;

import lt.ltrp.InitException;
import lt.ltrp.LoadingException;
import lt.ltrp.LtrpGamemode;
import lt.ltrp.job.AbstractJobManager;
import lt.ltrp.job.Job;
import lt.ltrp.job.RoadblockCommands;
import lt.ltrp.job.policeman.modelpreview.RoadblockModelPreview;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.plugin.streamer.DynamicLabel;
import lt.ltrp.plugin.streamer.DynamicSampObject;
import lt.ltrp.vehicle.JobVehicle;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.player.PlayerDeathEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;
import net.gtaun.util.event.HandlerPriority;

import java.io.IOException;
import java.util.*;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class PolicemanManager extends AbstractJobManager {

    private OfficerJob job;
    private RoadblockModelPreview roadblockPreview;
    protected Map<JobVehicle, DynamicLabel> unitLabels = new HashMap<>();
    protected Map<JobVehicle, DynamicSampObject> policeSirens = new HashMap<>();
    protected Map<LtrpPlayer, DragTimer> dragTimers;
    private PlayerCommandManager commandManager;


    public PolicemanManager(EventManager eventManager, int id) throws LoadingException {
        super(eventManager);
        this.unitLabels = new HashMap<>();
        this.policeSirens = new HashMap<>();
        this.dragTimers = new HashMap<>();
        this.roadblockPreview = RoadblockModelPreview.get();

        this.job = LtrpGamemode.getDao().getJobDao().getOfficerJob(id);

        commandManager = new PlayerCommandManager(eventManager);

        commandManager.replaceTypeParser(LtrpPlayer.class, s -> {
            try {
                return LtrpPlayer.get(Integer.parseInt(s));
            } catch(NumberFormatException e) {
                return LtrpPlayer.getByPartName(s);
            }
        });

        commandManager.registerCommands(new PoliceCommands(commandManager, job, eventManager, unitLabels, policeSirens, dragTimers));
        commandManager.registerCommands(new RoadblockCommands(job));
        commandManager.installCommandHandler(HandlerPriority.NORMAL);

        eventManagerNode.registerHandler(VehicleDeathEvent.class, e -> {
            JobVehicle jobVehicle = JobVehicle.getById(e.getVehicle().getId());
            if (jobVehicle != null) {
                if (unitLabels.containsKey(jobVehicle)) {
                    unitLabels.get(jobVehicle).destroy();
                    unitLabels.remove(jobVehicle);
                }
                if (policeSirens.containsKey(jobVehicle)) {
                    policeSirens.get(jobVehicle).destroy();
                    policeSirens.remove(jobVehicle);
                }
            }
        });

        eventManagerNode.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if (player != null) {
                Optional<DragTimer> timer = dragTimers.values().stream().filter(dt -> dt.getTarget().equals(player) || dt.getPlayer().equals(player)).findFirst();
                if (timer.isPresent()) {
                    DragTimer t = timer.get();
                    dragTimers.remove(t.getPlayer());
                    t.destroy();
                    t.getTarget().toggleControllable(true);
                }
            }
        });

        eventManagerNode.registerHandler(PlayerDeathEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if (player != null) {
                Optional<DragTimer> timer = dragTimers.values().stream().filter(dt -> dt.getTarget().equals(player) || dt.getPlayer().equals(player)).findFirst();
                if (timer.isPresent()) {
                    DragTimer t = timer.get();
                    dragTimers.remove(t.getPlayer());
                    t.destroy();
                    t.getTarget().toggleControllable(true);
                }
            }
        });




    }

    @Override
    public void destroy() {
        commandManager.uninstallAllHandlers();
        commandManager.destroy();
        super.destroy();
    }

    @Override
    public Job getJob() {
        return job;
    }
}
