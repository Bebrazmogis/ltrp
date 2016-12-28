package lt.ltrp;

import lt.ltrp.command.EntranceCommands;
import lt.ltrp.dao.EntranceDao;
import lt.ltrp.business.dao.impl.MySqlEntranceDaoImpl;
import lt.ltrp.dialog.EntranceListManagementDialog;
import lt.ltrp.event.EntranceCreateEvent;
import lt.ltrp.object.Entrance;
import lt.ltrp.job.object.Job;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.EntranceImpl;
import lt.maze.streamer.event.PlayerDynamicPickupEvent;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class EntrancePlugin extends Plugin {

    private Logger logger;
    private EventManagerNode eventManagerNode;
    private Collection<Entrance> entrances;
    private EntranceDao entranceDao;



    @Override
    protected void onEnable() throws Throwable {
        logger = getLogger();
        eventManagerNode = getEventManager().createChildNode();
        entrances = new ArrayList<>();

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
            eventManagerNode.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();
    }

    @Override
    protected void onDisable() throws Throwable {
        entrances.forEach(Entrance::destroy);
        entrances.clear();
        entrances = null;
        eventManagerNode.cancelAll();
    }

    private void load() {
        eventManagerNode.cancelAll();
        DatabasePlugin databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin.class);
        entranceDao = new MySqlEntranceDaoImpl(databasePlugin.getDataSource(), eventManagerNode);
        entrances.addAll(entranceDao.get());
        addEventHandlers();

        PlayerCommandManager cmdManager = new PlayerCommandManager(eventManagerNode);
        cmdManager.registerCommands(new EntranceCommands(eventManagerNode));
        cmdManager.installCommandHandler(HandlerPriority.NORMAL);

        logger.info(getDescription().getName() + " loaded with " + entrances.size() + " entrances.");
    }

    private void addEventHandlers() {
        eventManagerNode.registerHandler(PlayerDynamicPickupEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            Optional<Entrance> optional = getEntrances().stream().filter(en -> en.getPickup().equals(e.getPickup())).findFirst();
            if(optional.isPresent()) {
                Entrance entrance = optional.get();
                player.sendGameText(7, 2000, String.format("~g~~h~~h~%s~n~~w~Noredami ieiti - Rasykite ~y~/enter", entrance.getText()));
            }
        });
    }


    public Collection<Entrance> getEntrances() {
        return entrances;
    }

    public Entrance get(int uuid) {
        Optional<Entrance> op = entrances.stream().filter(e -> e.getUUID() == uuid).findFirst();
        return op.isPresent() ? op.get() : null;
    }

    public Entrance getClosest(Location location, float maxDistance) {
        Optional<Entrance> op = getEntrances().stream().min((b1, b2) -> {
            return Float.compare(b1.getLocation().distance(location), b2.getLocation().distance(location));
        });
        if(op.isPresent()) {
            float distance = op.get().getLocation().distance(location);
            if(distance <= maxDistance) {
                return op.get();
            }
        }
        return null;
    }

    public Entrance createEntrance(AngledLocation angledLocation, String text, Color labelColor, int pickupModelId, Job job, boolean allowsVehicles) {
        EntranceImpl impl = new EntranceImpl(0, labelColor, pickupModelId, text, angledLocation, job, allowsVehicles, eventManagerNode);
        entrances.add(impl);
        entranceDao.insert(impl);
        eventManagerNode.dispatchEvent(new EntranceCreateEvent(impl));
        return impl;
    }

    public void updateEntrance(Entrance entrance) {
        entranceDao.update(entrance);
    }

    public void showManagementDialog(LtrpPlayer player) {
        EntranceListManagementDialog.create(player, eventManagerNode)
                .show();
    }
}
