package lt.ltrp;

import lt.ltrp.command.HouseCommands;
import lt.ltrp.command.HouseOwnerCommands;
import lt.ltrp.command.HouseUpgradeCommands;
import lt.ltrp.constant.Currency;
import lt.ltrp.dao.HouseDao;
import lt.ltrp.dao.impl.MySqlHouseDaoImpl;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.AdminHouseManagementListDialog;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.property.HouseEvent;
import lt.ltrp.event.property.PlayerPlantWeedEvent;
import lt.ltrp.event.property.WeedGrowEvent;
import lt.ltrp.event.property.house.*;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.HouseImpl;
import lt.ltrp.player.BankAccount;
import lt.maze.streamer.event.PlayerDynamicPickupEvent;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
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
 *         2016.05.18.
 */
public class HousePlugin extends Plugin implements HouseController {

    private Logger logger;
    private Collection<House> houseCollection;
    private MySqlHouseDaoImpl houseDao;
    private EventManagerNode node;

    @Override
    protected void onEnable() throws Throwable {
        Instance.instance = this;
        logger = getLogger();
        houseCollection = new ArrayList<>();
        node = getEventManager().createChildNode();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(PropertyPlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            node.registerHandler(ResourceEnableEvent.class, e -> {
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
        DatabasePlugin databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin.class);
        node.cancelAll();
        houseDao = new MySqlHouseDaoImpl(databasePlugin.getDataSource(), node);
        houseCollection.addAll(houseDao.get());
        addEventHandlers();
        addCommands();
    }

    private void addCommands() {
        PlayerCommandManager playerCommandManager = new PlayerCommandManager(node);
        playerCommandManager.registerCommands(new HouseCommands(node));
        playerCommandManager.registerCommands(new HouseOwnerCommands(node));
        CommandGroup group = new HouseUpgradeCommands(node).getGroup();
        playerCommandManager.registerChildGroup(group, "hu");

        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);

    }

    private void addEventHandlers() {
        node.registerHandler(PlayerPlantWeedEvent.class, e -> {
            houseDao.insert(e.getWeedSapling());
        });

        node.registerHandler(WeedGrowEvent.class, e -> {
            getHouseDao().update(e.getSapling());

            LtrpPlayer owner = LtrpPlayer.get(e.getSapling().getPlantedByUser());
            if(owner != null && e.isFullyGrown()) {
                owner.sendMessage(net.gtaun.shoebill.data.Color.DARKGREEN, "..Galbût þolë namie jau uþaugo?");
            }
        });
        node.registerHandler(PaydayEvent.class, e -> {
            BankPlugin bankPlugin = Shoebill.get().getResourceManager().getPlugin(BankPlugin.class);
            if(bankPlugin != null) {
                LtrpPlayer.get().stream().filter(p -> SpawnPlugin.get(SpawnPlugin.class).getSpawnData(p).getType() == SpawnData.SpawnType.House).forEach(p -> {
                    SpawnData spawnData = SpawnPlugin.get(SpawnPlugin.class).getSpawnData(p);
                    House house = House.get(spawnData.getId());
                    if (house != null) {
                        int rent = house.getRentPrice();
                        BankAccount account = bankPlugin.getBankController().getAccount(p);
                        if (account.getMoney() >= rent) {
                            account.addMoney(-rent);
                            bankPlugin.getBankController().update(account);
                            house.addMoney(rent);
                            p.sendMessage(net.gtaun.shoebill.data.Color.WHITE, String.format("| Mokestis uþ nuomà: %d%c |", rent, Currency.SYMBOL));
                        } else {
                            p.sendMessage("Jûsø banko sàskaitoje nëra pakankamai pinigø susimokëti uþ nuomà, todël buvote iðmestas.");
                            node.dispatchEvent(new PlayerSpawnLocationChangeEvent(p, SpawnData.DEFAULT));
                        }

                    } else {
                        logger.error("Player " + p.getUUID() + " lives in an inexistent house " + spawnData.getId());
                    }
                });
            } else {
                logger.error("BankPlugin is not loaded");
            }
        });

        node.registerHandler(PlayerDynamicPickupEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            Optional<House> optionalHouse = House.get().stream().filter(h -> h.getPickup() != null && h.getPickup().equals(e.getPickup())).findFirst();
            if(optionalHouse.isPresent()) {
                House h = optionalHouse.get();
                String s = " Namo numeris: "+ h.getUUID();
                if(h.getRentPrice() > 0) {
                    s  += String.format("~n~Nuomos mokestis: %d~n~Nuomavimuisi - /rentroom",  h.getRentPrice());
                }
                player.sendInfoText(s, 15);
            }
        });

        node.registerHandler(HouseCreateEvent.class, HandlerPriority.HIGHEST, e -> {
            //houseDao.insert(e.getProperty());
        });

        node.registerHandler(HouseDestroyEvent.class, HandlerPriority.HIGHEST, e -> {
            House h = e.getProperty();
            houseCollection.remove(h);
            //houseDao.remove(e.getProperty());
        });

        node.registerHandler(HouseEditEvent.class, HandlerPriority.HIGHEST, e -> {
            houseDao.update(e.getProperty());
        });

        node.registerHandler(HouseMoneyEvent.class, HandlerPriority.HIGHEST, e -> {
            houseDao.update(e.getProperty());
        });

        node.registerHandler(HouseEvent.class, e -> {
            logger.debug("HouseEvent ");
            new Thread(() -> {
                House h=  e.getHouse();
                if(h.getUUID() > 0)
                    houseDao.update(h);
            }) .start();
        });

        node.registerHandler(HouseBuyEvent.class, HandlerPriority.HIGHEST, e -> {
            LtrpPlayer player = e.getNewOwner() ;
            houseDao.update(e.getProperty());
        });
    }

    @Override
    protected void onDisable() throws Throwable {
        houseCollection.forEach(House::destroy);
        houseCollection.clear();
        houseCollection = null;
        node.cancelAll();
    }

    @Override
    public House createHouse(int i, String s, int i1, int i2, int i3, Location location, Location location1, Color color, int i4, int i5) {
        House h = new HouseImpl(i, s, i1, i2, i3, location, location1, color, i4, i5, node);
        houseDao.insert(h);
        houseCollection.add(h);
        return h;
    }

    @Override
    public Collection<House> getHouses() {
        return houseCollection;
    }

    @Override
    public HouseDao getHouseDao() {
        return houseDao;
    }

    @Override
    public House getHouse(Location location) {
        Optional<House> house = House.get().stream().filter(h -> h.getExit() != null && h.getExit().distance(location) < 200f).findFirst();
        return house.isPresent() ? house.get() : null;
    }

    @Override
    public void showManagementDialog(LtrpPlayer ltrpPlayer) {
        AdminHouseManagementListDialog.create(ltrpPlayer, node)
                .show();
    }

    @Override
    public House getClosest(Location location, float v) {
        House closest = getHouse(location);
        logger.debug("inside?" + closest);
        if(closest != null)
            return closest;
        float min = v;
        for (House b : getHouses()) {
            float distance = b.getEntrance().distance(location);
            logger.debug("Distance to " + b.getUUID() + ":" + distance + " max distance:" + min);
            if(distance <= min) {
                closest = b;
                min = distance;
            }
        }
        return closest;
    }
}
