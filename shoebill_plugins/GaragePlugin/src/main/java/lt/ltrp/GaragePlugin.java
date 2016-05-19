package lt.ltrp;

import lt.ltrp.command.GarageCommands;
import lt.ltrp.command.GarageOwnerCommands;
import lt.ltrp.dao.GarageDao;
import lt.ltrp.dao.impl.MySqlGarageDaoImpl;
import lt.ltrp.dialog.AdminGarageManagementDialog;
import lt.ltrp.event.property.garage.GarageBuyEvent;
import lt.ltrp.event.property.garage.GarageDestroyEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.GarageImpl;
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
 *         2016.05.19.
 */
public class GaragePlugin extends Plugin implements GarageController {

    private Logger logger;
    private Collection<Garage> garageCollection;
    private EventManagerNode eventManagerNode;
    private MySqlGarageDaoImpl garageDao;

    @Override
    protected void onEnable() throws Throwable {
        Instance.instance = this;
        logger = getLogger();
        eventManagerNode = getEventManager().createChildNode();
        garageCollection = new ArrayList<>();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(ItemPlugin.class);
        dependencies.add(PropertyPlugin.class);
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
                logger.debug("R:" + r);
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    logger.debug("Removing r");
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();
    }

    private void load() {
        eventManagerNode.cancelAll();
        DatabasePlugin databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin.class);
        this.garageDao = new MySqlGarageDaoImpl(databasePlugin.getDataSource(), eventManagerNode);
        Collection<Garage> garages = garageDao.get();
        loadItemsAsynchronously(garages);
        garageCollection.addAll(garages);
        addCommands();
        addEventHandlers();
        logger.info(getDescription().getName() + " loaded");
    }

    private void addCommands() {
        PlayerCommandManager playerCommandManager = new PlayerCommandManager(eventManagerNode);
        playerCommandManager.registerCommands(new GarageCommands(eventManagerNode));
        playerCommandManager.registerCommands(new GarageOwnerCommands(eventManagerNode));

        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
    }

    private void addEventHandlers() {
        eventManagerNode.registerHandler(GarageDestroyEvent.class, HandlerPriority.HIGHEST, e -> {
            garageCollection.remove(e.getProperty());
        });

        eventManagerNode.registerHandler(GarageBuyEvent.class, HandlerPriority.HIGHEST, e -> {
            garageDao.update(e.getProperty());
        });
    }

    private void loadItemsAsynchronously(Collection<Garage> garages) {
        new Thread(() -> {
            ItemPlugin itemPlugin = ResourceManager.get().getPlugin(ItemPlugin.class);
            garages.forEach(g -> {
                g.getInventory().add(itemPlugin.getItemDao().getItems(g)); 
            });
        }).start();
    }

    @Override
    protected void onDisable() throws Throwable {
        garageCollection.forEach(Garage::destroy);
        garageCollection.clear();
        garageCollection = null;
        eventManagerNode.cancelAll();
    }


    @Override
    public Garage get(int i) {
        Optional<Garage> op = getGarages()
                .stream()
                .filter(b -> b.getUUID() == i)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    @Override
    public Garage get(Location location) {
        Optional<Garage> garage = Garage.get().stream().filter(h -> h.getExit() != null && h.getExit().distance(location) < 200f).findFirst();
        return garage.isPresent() ? garage.get() : null;
    }

    @Override
    public Garage getClosest(Location location, float v) {
        Garage g = get(location);
        if(g != null) return g;

        Optional<Garage> op = getGarages().stream().min((b1, b2) -> {
            return Float.compare(b1.getEntrance().distance(location), b2.getEntrance().distance(location));
        });
        if(op.isPresent()) {
            float distance = op.get().getEntrance().distance(location);
            if(distance <= v) {
                return op.get();
            }
        }
        return null;
    }

    @Override
    public GarageDao getDao() {
        return garageDao;
    }

    @Override
    public Collection<Garage> getGarages() {
        return garageCollection;
    }

    @Override
    public void showManagementDialog(LtrpPlayer ltrpPlayer) {
        AdminGarageManagementDialog.create(ltrpPlayer, eventManagerNode).show();
    }

    @Override
    public Garage createGarage(int i, String s, int i1, int i2, int i3, Location location, Location location1, AngledLocation angledLocation, AngledLocation angledLocation1, Color color) {
        Garage g = new GarageImpl(i, s, i1, i2, i3, location, location1, angledLocation, angledLocation1, color, eventManagerNode);
        garageDao.insert(g);
        garageCollection.add(g);
        return g;
    }

}
