package lt.ltrp.job.policeman;

import lt.ltrp.InitException;
import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.PlayerCommandManager;
import lt.ltrp.job.policeman.modelpreview.RoadblockModelPreview;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.plugin.streamer.DynamicLabel;
import lt.ltrp.plugin.streamer.DynamicSampObject;
import lt.ltrp.vehicle.JobVehicle;
import lt.ltrp.vehicle.LtrpVehicle;
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
public class PolicemanManager  {

    public static final int JOB_ID = 2;
    private static PolicemanManager instance;

    public static PolicemanManager getInstance() {
        if(instance == null) {
            instance = new PolicemanManager();
        }
        return instance;
    }

    private EventManager eventManager;
    private OfficerJob job;
    private RoadblockModelPreview roadblockPreview;
    protected Map<JobVehicle, DynamicLabel> unitLabels = new HashMap<>();
    protected Map<JobVehicle, DynamicSampObject> policeSirens = new HashMap<>();
    protected Map<LtrpPlayer, DragTimer> dragTimers;
    private List<HandlerEntry> eventHandlers;
    private PlayerCommandManager commandManager;


    public PolicemanManager() {
        this.eventManager = LtrpGamemode.get().getEventManager().createChildNode();
        this.unitLabels = new HashMap<>();
        this.policeSirens = new HashMap<>();
        this.dragTimers = new HashMap<>();
        this.eventHandlers = new ArrayList<>();
        this.roadblockPreview = RoadblockModelPreview.get();

        try {
            this.job = LtrpGamemode.getDao().getJobDao().getPoliceFaction(JOB_ID);
        } catch (IOException e) {
           throw new InitException("Policeman manager initialization failed", e);
        }

        commandManager = new PlayerCommandManager(HandlerPriority.NORMAL, eventManager);
        commandManager.registerCommands(new PoliceCommands(job, eventManager, unitLabels, policeSirens, dragTimers));

        eventHandlers.add(eventManager.registerHandler(VehicleDeathEvent.class, e -> {
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
        }));

        eventHandlers.add(eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {
                Optional<DragTimer> timer = dragTimers.values().stream().filter(dt -> dt.getTarget().equals(player) || dt.getPlayer().equals(player)).findFirst();
                if(timer.isPresent()) {
                    DragTimer t = timer.get();
                    dragTimers.remove(t.getPlayer());
                    t.destroy();
                    t.getTarget().toggleControllable(true);
                }
            }
        }));

        eventHandlers.add(eventManager.registerHandler(PlayerDeathEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {
                Optional<DragTimer> timer = dragTimers.values().stream().filter(dt -> dt.getTarget().equals(player) || dt.getPlayer().equals(player)).findFirst();
                if(timer.isPresent()) {
                    DragTimer t = timer.get();
                    dragTimers.remove(t.getPlayer());
                    t.destroy();
                    t.getTarget().toggleControllable(true);
                }
            }
        }));




    }

}
