package lt.ltrp.policeman;

import lt.ltrp.AbstractJobManager;
import lt.ltrp.LoadingException;
import lt.ltrp.RoadblockCommands;
import lt.ltrp.object.JobVehicle;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PoliceFaction;
import lt.maze.streamer.object.DynamicLabel;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.player.PlayerDeathEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;

import java.util.*;

/**
 * @author Bebras
 *         2015.12.27.
 */
public class PolicemanManager extends AbstractJobManager {

    private PoliceFaction job;
    protected Map<JobVehicle, DynamicLabel> unitLabels = new HashMap<>();
    protected Map<JobVehicle, DynamicObject> policeSirens = new HashMap<>();
    protected Map<LtrpPlayer, DragTimer> dragTimers;
    private PlayerCommandManager commandManager;
    private Collection<LtrpPlayer> playersOnDuty;


    public PolicemanManager(EventManager eventManager, int id) throws LoadingException {
        super(eventManager);
        this.unitLabels = new HashMap<>();
        this.policeSirens = new HashMap<>();
        this.dragTimers = new HashMap<>();
        this.playersOnDuty = new ArrayList<>();

        //this.job = JobController.get().getDao().getOfficerJob(id);

        commandManager = new PlayerCommandManager(eventManager);

        commandManager.replaceTypeParser(LtrpPlayer.class, s -> {
            try {
                return LtrpPlayer.get(Integer.parseInt(s));
            } catch(NumberFormatException e) {
                return LtrpPlayer.getByPartName(s);
            }
        });

        commandManager.registerCommands(new PoliceCommands(commandManager, job, eventManager, unitLabels, policeSirens, dragTimers, this));
        commandManager.registerCommands(new RoadblockCommands(job, eventManagerNode));
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

    public boolean isOnDuty(LtrpPlayer player) {
        return playersOnDuty.contains(player);
    }

    public void setOnDuty(LtrpPlayer player, boolean set) {
        if(set)
            playersOnDuty.add(player);
        else
            playersOnDuty.remove(player);
    }

    @Override
    public void destroy() {
        commandManager.uninstallAllHandlers();
        commandManager.destroy();
        super.destroy();
    }

    @Override
    public PoliceFaction getJob() {
        return job;
    }
}
