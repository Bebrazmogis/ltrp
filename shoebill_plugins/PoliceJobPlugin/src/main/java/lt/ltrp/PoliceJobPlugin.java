package lt.ltrp;

import lt.ltrp.command.PoliceCommands;
import lt.ltrp.dao.PoliceFactionDao;
import lt.ltrp.dao.impl.MySqlPoliceFactionImpl;
import lt.ltrp.object.JobVehicle;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PoliceFaction;
import lt.ltrp.policeman.DragTimer;
import lt.maze.streamer.object.DynamicLabel;
import lt.maze.streamer.object.DynamicObject;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.player.PlayerDeathEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.24.
 */
public class PoliceJobPlugin extends Plugin {

    private EventManagerNode eventManager;
    private Logger logger;
    protected Map<JobVehicle, DynamicLabel> unitLabels = new HashMap<>();
    protected Map<JobVehicle, DynamicObject> policeSirens = new HashMap<>();
    protected Map<LtrpPlayer, DragTimer> dragTimers;
    private PlayerCommandManager commandManager;
    private Collection<LtrpPlayer> playersOnDuty;
    private PoliceFaction policeFaction;
    private PoliceFactionDao policeFactionDao;

    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        eventManager = getEventManager().createChildNode();
        this.unitLabels = new HashMap<>();
        this.policeSirens = new HashMap<>();
        this.dragTimers = new HashMap<>();
        this.playersOnDuty = new ArrayList<>();


        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(JobPlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            eventManager.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();

    }

    private  void load() {
        eventManager.cancelAll();
        policeFactionDao = new MySqlPoliceFactionImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), null, eventManager);
        policeFaction = policeFactionDao.get(JobPlugin.JobId.Officer.id);

        registerCommands();
        registerEventHandlers();

        logger.info(getDescription().getName() + " loaded");
    }

    private void registerCommands() {
        commandManager = new PlayerCommandManager(eventManager);

        commandManager.replaceTypeParser(LtrpPlayer.class, s -> {
            try {
                return LtrpPlayer.get(Integer.parseInt(s));
            } catch(NumberFormatException e) {
                return LtrpPlayer.getByPartName(s);
            }
        });

        commandManager.registerCommands(new PoliceCommands(commandManager, policeFaction, eventManager, unitLabels, policeSirens, dragTimers));
        commandManager.registerCommands(new RoadblockCommands(policeFaction, eventManager));
        commandManager.installCommandHandler(HandlerPriority.NORMAL);
    }

    private void registerEventHandlers() {
        eventManager.registerHandler(VehicleDeathEvent.class, e -> {
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

        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> {
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

        eventManager.registerHandler(PlayerDeathEvent.class, e -> {
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
    protected void onDisable() throws Throwable {
        eventManager.cancelAll();
        commandManager.uninstallAllHandlers();
        commandManager.destroy();
        logger.info(getDescription().getName() + " unloaded");
    }
}
