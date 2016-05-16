package lt.ltrp;

import lt.ltrp.constant.BusinessType;
import lt.ltrp.constant.Currency;
import lt.ltrp.dao.BusinessDao;
import lt.ltrp.dao.GarageDao;
import lt.ltrp.dao.HouseDao;
import lt.ltrp.dao.PropertyDao;
import lt.ltrp.dao.impl.MySqlBusinessDaoImpl;
import lt.ltrp.dao.impl.MySqlGarageDaoImpl;
import lt.ltrp.dao.impl.MySqlHouseDaoImpl;
import lt.ltrp.dao.impl.MySqlPropertyDaoImpl;
import lt.ltrp.data.SpawnData;
import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.player.PlayerSpawnLocationChangeEvent;
import lt.ltrp.event.property.*;
import lt.ltrp.object.*;
import lt.ltrp.object.impl.BusinessImpl;
import lt.ltrp.object.impl.GarageImpl;
import lt.ltrp.object.impl.HouseImpl;
import lt.ltrp.player.BankAccount;
import lt.ltrp.util.StringUtils;
import lt.maze.streamer.event.PlayerDynamicPickupEvent;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class PropertyPlugin extends Plugin implements PropertyController {

    private static Logger logger;

    private Collection<House> houseCollection;
    private Collection<Business> businessCollection;
    private Collection<Garage> garageCollection;
    private EventManagerNode eventManagerNode;
    private HouseDao houseDao;
    private BusinessDao businessDao;
    private GarageDao garageDao;

    @Override
    protected void onEnable() throws Throwable {
        System.out.println("PropertyPlugin :: onEnable");
        Instance.instance = this;
        logger = LoggerFactory.getLogger(PropertyController.class);
        houseCollection = new ArrayList<>();
        businessCollection = new ArrayList<>();
        garageCollection = new ArrayList<>();

        eventManagerNode = getEventManager().createChildNode();
        if(Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class) != null) {
            load();
        } else {
            eventManagerNode.registerHandler(ResourceEnableEvent.class, ee -> {
                if(ee.getResource().getClass().equals(DatabasePlugin.class)) {
                    load();
                }
            });
        }
    }

    private void load() {
        System.out.println("Yes its database plugin enable event");
        DatabasePlugin dp = Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class);
        DataSource ds = dp.getDataSource();
        PropertyDao propertyDao = new MySqlPropertyDaoImpl(ds);
        houseDao = new MySqlHouseDaoImpl(ds, propertyDao, eventManagerNode);
        businessDao = new MySqlBusinessDaoImpl(ds, propertyDao, eventManagerNode);
        garageDao = new MySqlGarageDaoImpl(ds, propertyDao, eventManagerNode);
        // This simple call will load 'em all
        businessDao.read();
        garageDao.read();
       // houseDao.read();

        new Thread(() -> {
            Garage.get().forEach(g -> {
                g.getInventory().add(ItemController.get().getItemDao().getItems(g));
            });
        }).start();

        PlayerCommandManager commandManager = new PlayerCommandManager(eventManagerNode);
        HouseCommands houseCommands = new HouseCommands(eventManagerNode);
        CommandGroup acceptGroup = new CommandGroup();
        acceptGroup.registerCommands(new PropertyAcceptCommands(eventManagerNode));
        HouseUpgradeCommands houseUpgradeCommands = new HouseUpgradeCommands(eventManagerNode);
        commandManager.registerChildGroup(houseUpgradeCommands.getGroup(), "hu");
        commandManager.registerChildGroup(acceptGroup, "accept");
        commandManager.registerCommands(houseCommands);
        commandManager.registerCommands(new BusinessCommands(eventManagerNode));
        commandManager.registerCommands(new GarageCommands(eventManagerNode));
        commandManager.registerCommands(new PropertyCommands(eventManagerNode));
        commandManager.installCommandHandler(HandlerPriority.NORMAL);

        eventManagerNode.registerHandler(AmxLoadEvent.class, e -> {
            addPawnFunctions(e.getAmxInstance());
        });

        eventManagerNode.registerHandler(PlayerEnterPropertyEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            Property property = e.getProperty();
            p.setProperty(property);
        });

        eventManagerNode.registerHandler(PlayerExitPropertyEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            Property property = e.getProperty();
            p.setProperty(null);
        });

        eventManagerNode.registerHandler(PlayerPlantWeedEvent.class, e -> {
            houseDao.insertWeed(e.getWeedSapling());
        });

        eventManagerNode.registerHandler(WeedGrowEvent.class, e -> {
            PropertyController.get().getHouseDao().updateWeed(e.getSapling());

            LtrpPlayer owner = LtrpPlayer.get(e.getSapling().getPlantedByUser());
            if(owner != null && e.isFullyGrown()) {
                owner.sendMessage(Color.DARKGREEN, "..Galbût þolë namie jau uþaugo?");
            }
        });

        eventManagerNode.registerHandler(BusinessEvent.class, e -> {
            new Thread(() -> {
                Business b = e.getProperty();
                businessDao.update(b);
                b.getCommodities().forEach(businessDao::update);
            }).start();
        });

        eventManagerNode.registerHandler(BusinessNameChangeEvent.class, e -> {
            Business b = e.getProperty();
            businessDao.update(b);
            LtrpPlayer.sendAdminMessage(e.getPlayer() + " pakeitë verslo " + b.getUUID() + " pavadinimà á " + b.getName());
        });

        eventManagerNode.registerHandler(PropertyDestroyEvent.class, HandlerPriority.HIGHEST, e -> {
            Property property = e.getProperty();
            if(property instanceof Business)
                businessCollection.remove((Business)property);
            else if(property instanceof House)
                houseCollection.remove((House)property);
            else if(property instanceof Garage)
                garageCollection.remove((Garage)property);
        });

        eventManagerNode.registerHandler(PaydayEvent.class, e -> {
            BankPlugin bankPlugin = Shoebill.get().getResourceManager().getPlugin(BankPlugin.class);
            if(bankPlugin != null) {
                LtrpPlayer.get().stream().filter(p -> p.getSpawnData().getType() == SpawnData.SpawnType.House).forEach(p -> {
                    House house = House.get(p.getSpawnData().getId());
                    if(house != null) {
                        int rent = house.getRentPrice();
                        BankAccount account = bankPlugin.getBankController().getAccount(p);
                        if(account.getMoney() >= rent) {
                            account.addMoney(- rent);
                            bankPlugin.getBankController().update(account);
                            house.addMoney(rent);
                            p.sendMessage(Color.WHITE, String.format("| Mokestis uþ nuomà: %d%c |", rent, Currency.SYMBOL));
                        } else {
                            p.sendMessage("Jûsø banko sàskaitoje nëra pakankamai pinigø susimokëti uþ nuomà, todël buvote iðmestas.");
                            eventManagerNode.dispatchEvent(new PlayerSpawnLocationChangeEvent(p, SpawnData.DEFAULT));
                        }

                    } else {
                        logger.error("Player " + p.getUUID() + " lives in an unexistent house " + p.getSpawnData().getId());
                    }
                });
            } else {
                logger.error("BankPlugin is not loaded");
            }
        });

        eventManagerNode.registerHandler(BusinessBuyEvent.class, HandlerPriority.HIGHEST, e -> {
            businessDao.update(e.getProperty());
        });

        eventManagerNode.registerHandler(BusinessCreateEvent.class, HandlerPriority.HIGHEST, e -> {
            Business b = e.getProperty();
            businessDao.insert(b);
        });

        eventManagerNode.registerHandler(BusinessNameChangeEvent.class, e -> {
            businessDao.update(e.getProperty());
        });

        eventManagerNode.registerHandler(BusinessBankChangeEvent.class, e -> {
            Business b = e.getProperty();
            businessDao.update(b);
            LtrpPlayer p = e.getPlayer();
            if(p != null) {
                // TODO log
            }
        });

        eventManagerNode.registerHandler(BusinessEvent.class, e -> {
            System.out.println("BusinessEvent");
            businessDao.update(e.getProperty());
        });

        eventManagerNode.registerHandler(BusinessEditEvent.class, e -> {
            System.out.println("BusinessEditEvent");
            businessDao.update(e.getProperty());
        });

        eventManagerNode.registerHandler(BusinessDestroyEvent.class, HandlerPriority.HIGHEST, e -> {
            businessDao.remove(e.getProperty());
        });


        eventManagerNode.registerHandler(PlayerDynamicPickupEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            Optional<Business> opB = Business.get().stream().filter(b -> b.getPickup() != null && b.getPickup().equals(e.getPickup())).findFirst();
            if(opB.isPresent()) {
                Business b = opB.get();
                String name = StringUtils.limit(StringUtils.replaceLtChars(StringUtils.stripColors(b.getName())), 40, "..");
                String msg = String.format("%s~n~~w~Savininkas: ~g~%s~n~ ~w~Mokestis: ~g~ %d ~n~~p~ Noredami ieiti - Rasykite /enter",
                        name, PlayerController.get().getUsernameByUUID(b.getOwner()), b.getEntrancePrice());
                player.sendGameText(6000, 7, msg);
                // TODO rasi alternatyva ivietoj gameText. Su ilgaisp avadinimais blogai
            }
        });

        //commodities
        eventManagerNode.registerHandler(BusinessCommodityAddEvent.class, e -> {
            businessDao.insert(e.getCommodity());
        });

        eventManagerNode.registerHandler(BusinessCommodityRemoveEvent.class, e -> {
            businessDao.remove(e.getCommodity());
        });

        eventManagerNode.registerHandler(BusinessCommodityPriceUpdateEvent.class, e -> {
            businessDao.update(e.getCommodity());
        });



        logger.debug("Property plugin loaded");
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManagerNode.cancelAll();
    }


    // Functions for Pawn
    private void addPawnFunctions(AmxInstance amx) {
        // Usage: CreatePoperty(string type, int uid, float enx, float eny, float enz, int enint, int enVirtual, float exx, float exy, float exz, float exint, exVirtual);
        amx.registerFunction("createProperty", params -> {
            Location entrance = new Location((Float)params[2],
                    (Float)params[3],
                    (Float)params[4],
                    (Integer)params[6],
                    (Integer)params[5]);
            Location exit = new Location((Float)params[7],
                    (Float)params[8],
                    (Float)params[9],
                    (Integer)params[11],
                    (Integer)params[10]);
            String type = (String)params[0];
            Property property = null;
            Inventory inventory = null;
            if(type.equalsIgnoreCase("House")) {
               /* House house = House.create((Integer)params[1], params[0] + " " + params[1], entrance, exit, eventManagerNode);
                inventory = Inventory.create(eventManagerNode, house, "Namo " + house.getUUID() + " daiktai", 20);
                house.setWeedSaplings(PropertyController.get().getHouseDao().getWeed(house));
                inventory.add(ItemController.get().getItemDao().getItems(house));
                house.setInventory(inventory);
                property = house;
                */
            }/* else if(type.equalsIgnoreCase("garagE")) {
                property = Garage.create((Integer) params[1], params[0] + " " + params[1], entrance, exit, eventManagerNode);
                inventory = Inventory.create(eventManagerNode, (Garage)property, "Garaþo " + property.getUUID() + " daiktai", 20);
                inventory.add(ItemController.get().getItemDao().getItems((Garage) property));
                ((Garage)property).setInventory(inventory);
            }*/
            /* else if(type.equalsIgnoreCase("business")) {
                property = Business.create((Integer) params[1], params[0] + " " + params[1], entrance, exit, eventManagerNode);
            }*/ else {
                return 0;
            }
            return property.getUUID();
        }, String.class, Integer.class, Float.class, Float.class, Float.class, Integer.class, Integer.class, Float.class, Float.class, Float.class, Integer.class, Integer.class);

        // Usage: DestroyProperty(string type, int uid);
        amx.registerFunction("destroyProperty", params -> {
            String type = (String)params[0];
            int id = (Integer)params[1];
            if(type.equalsIgnoreCase("House")) {
                House.get(id).destroy();
            } else if(type.equalsIgnoreCase("garagE")) {
                Garage.get(id).destroy();
            } else if(type.equalsIgnoreCase("business")) {
                Business.get(id).destroy();
            }

            return 1;
        }, String.class, Integer.class);
/*
        // OnPlayerEnterHouse(playerid, housesqlid);
        amx.registerFunction("OnPlayerEnterHouse", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            House house = House.get((Integer)params[1]);
            if(player != null && house != null) {
                eventManagerNode.dispatchEvent(new PlayerEnterHouseEvent(player, house));
                eventManagerNode.dispatchEvent(new PlayerEnterPropertyEvent(player, house));
            }
            return 1;
        }, Integer.class, Integer.class);

        // OnPlayerEnterBusiness(playerid, businessqlid);
        amx.registerFunction("OnPlayerEnterBusiness", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            Business business = Business.get((Integer)params[1]);
            if(player != null && business != null) {
                eventManagerNode.dispatchEvent(new PlayerEnterBusinessEvent(player, business));
                eventManagerNode.dispatchEvent(new PlayerEnterPropertyEvent(player, business));
            }
            return 1;
        }, Integer.class, Integer.class);

        // OnPlayerEnterGarage(playerid, garagesqlid, vehicleid);
        amx.registerFunction("OnPlayerEnterGarage", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            Garage garage = Garage.get((Integer)params[1]);
            if(player != null && garage != null) {
                LtrpVehicle vehicle = LtrpVehicle.getById((Integer) params[2]);
                eventManagerNode.dispatchEvent(new PlayerEnterGarageEvent(player, garage, vehicle));
                eventManagerNode.dispatchEvent(new PlayerEnterPropertyEvent(player, garage));
            }
            return 1;
        }, Integer.class, Integer.class);

        // OnPlayerExitGarage(playerid, garagesqlid, vehicleid);
        amx.registerFunction("OnPlayerExitGarage", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            Garage garage = Garage.get((Integer)params[1]);
            if(player != null && garage != null) {
                LtrpVehicle vehicle = LtrpVehicle.getById((Integer)params[2]);
                eventManagerNode.dispatchEvent(new PlayerExitGarageEvent(player, garage, vehicle));
                eventManagerNode.dispatchEvent(new PlayerExitPropertyEvent(player, garage));
            }
            return 1;
        }, Integer.class, Integer.class);
        // OnPlayerExitBusiness(playerid, businessssqlid);
        amx.registerFunction("OnPlayerExitBusiness", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            Business business = Business.get((Integer)params[1]);
            if(player != null && business != null) {
                eventManagerNode.dispatchEvent(new PlayerExitBusinessEvent(player, business));
                eventManagerNode.dispatchEvent(new PlayerExitPropertyEvent(player, business));
            }
            return 1;
        }, Integer.class, Integer.class);

        // OnPlayerExitHouse(playerid, housesqlid);
        amx.registerFunction("OnPlayerExitHouse", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            House house = House.get((Integer)params[1]);
            if(player != null && house != null) {
                eventManagerNode.dispatchEvent(new PlayerExitHouseEvent(player, house));
                eventManagerNode.dispatchEvent(new PlayerExitPropertyEvent(player, house));
            }
            return 1;
        }, Integer.class, Integer.class);
        */
    }


    @Override
    public House createHouse(int i, String s, int i1, int i2, int i3, Location location, Location location1, lt.ltrp.data.Color color, int i4, int i5, EventManager eventManager) {
        House house = new HouseImpl(i, s, i1, i2, i3, location, location1, color, i4, i5, eventManager);
        houseCollection.add(house);
        return house;
    }

    @Override
    public Business createBusiness(int id, String name, BusinessType type, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, Color labelColor,
                                   int money, int resources, int commodityLimit, EventManager eventManager) {
        BusinessImpl impl = new BusinessImpl(id, name, type, ownerUserId, pickupModelId, price, entrance, exit, labelColor, money, resources, commodityLimit, eventManager);
        businessCollection.add(impl);
        return impl;
    }

    @Override
    public Garage createGarage(int i, String s, int i1, int i2, int i3, Location location, Location location1, AngledLocation location2, AngledLocation location3, Color color, EventManager eventManager) {
        Garage garage = new GarageImpl(i, s, i1, i2, i3, location, location1, location2, location3, color, eventManager);
        garageCollection.add(garage);
        return garage;
    }

    @Override
    public Collection<Garage> getGarages() {
        return garageCollection;
    }

    @Override
    public Collection<Business> getBusinesses() {
        return businessCollection;
    }

    @Override
    public Collection<House> getHouses() {
        return houseCollection;
    }

    @Override
    public Collection<Property> getProperties() {
        Collection<Property> cl = new ArrayList<>();
        cl.addAll(houseCollection);
        cl.addAll(businessCollection);
        cl.addAll(garageCollection);
        return cl;
    }

    @Override
    public List<BusinessCommodity> getAvailableCommodities(BusinessType businessType) {
        return businessDao.get(businessType);
    }

    @Override
    public HouseDao getHouseDao() {
        return houseDao;
    }

    @Override
    public BusinessDao getBusinessDao() {
        return businessDao;
    }

    @Override
    public GarageDao getGarageDao() {
        return garageDao;
    }
}
