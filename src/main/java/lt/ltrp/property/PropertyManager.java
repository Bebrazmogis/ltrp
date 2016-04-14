package lt.ltrp.property;

import lt.ltrp.BankPlugin;
import lt.ltrp.LtrpGamemodeImpl;
import lt.ltrp.common.constant.Currency;
import lt.ltrp.dao.HouseDao;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.item.FixedSizeInventory;
import lt.ltrp.item.object.Inventory;
import lt.ltrp.player.BankAccount;
import lt.ltrp.player.data.SpawnData;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.event.*;
import lt.ltrp.property.object.Business;
import lt.ltrp.property.object.Garage;
import lt.ltrp.property.object.House;
import lt.ltrp.property.object.Property;
import lt.ltrp.vehicle.object.LtrpVehicle;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PropertyManager implements Destroyable {

    private static final Logger logger = LoggerFactory.getLogger(PropertyManager.class);

    private final EventManagerNode eventManager;
    private boolean destroyed;

    public PropertyManager(EventManager eventManager1, HouseDao houseDao, BankPlugin bankPlugin) {
        eventManager = eventManager1.createChildNode();

        PlayerCommandManager commandManager = new PlayerCommandManager(eventManager);
        commandManager.installCommandHandler(HandlerPriority.NORMAL);
        commandManager.registerCommands(new HouseCommands(eventManager));
        commandManager.registerCommands(new BusinessCommands());
        commandManager.registerCommands(new GarageCommands(eventManager));
        commandManager.registerCommands(new PropertyCommands());

        eventManager.registerHandler(AmxLoadEvent.class, e -> {
            addPawnFunctions(e.getAmxInstance());
        });

        eventManager.registerHandler(PlayerEnterPropertyEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            Property property = e.getProperty();
            p.setProperty(property);
        });

        eventManager.registerHandler(PlayerExitPropertyEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            Property property = e.getProperty();
            p.setProperty(null);
        });

        eventManager.registerHandler(PlayerPlantWeedEvent.class, e -> {
             houseDao.insertWeed(e.getWeedSapling());
        });

        eventManager.registerHandler(WeedGrowEvent.class, e -> {
            LtrpGamemodeImpl.getDao().getHouseDao().updateWeed(e.getSapling());

            LtrpPlayer owner = LtrpPlayer.get(e.getSapling().getPlantedByUser());
            if(owner != null && e.isFullyGrown()) {
                owner.sendMessage(Color.DARKGREEN, "..Galbût şolë namie jau uşaugo?");
            }
        });

        eventManager.registerHandler(PaydayEvent.class, e -> {
            LtrpPlayer.get().stream().filter(p -> p.getSpawnData().getType() == SpawnData.SpawnType.House).forEach(p -> {
                House house = House.get(p.getSpawnData().getId());
                if(house != null) {
                    int rent = house.getHouseRent();
                    BankAccount account = bankPlugin.getBankController().getAccount(p);
                    if(account.getMoney() >= rent) {
                        account.addMoney(- rent);
                        bankPlugin.getBankController().update(account);
                        house.addBankMoney(rent);
                        p.sendMessage(Color.WHITE, String.format("| Mokestis uş nuomà: %d%c |", rent, Currency.SYMBOL));
                    } else {
                        p.sendMessage("Jûsø banko sàskaitoje nëra pakankamai pinigø susimokëti uş nuomà, todël buvote iğmestas.");
                        p.setSpawnData(SpawnData.DEFAULT);
                        LtrpPlayer.getPlayerDao().setSpawnData(p);
                    }

                } else {
                    logger.error("Player " + p.getUUID() + " lives in an unexistent house " + p.getSpawnData().getId());
                }
            });
        });
    }

    @Override
    public void destroy() {
        this.destroyed = true;
        eventManager.cancelAll();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
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
                House house = House.create((Integer)params[1], params[0] + " " + params[1], entrance, exit, eventManager);
                inventory = new FixedSizeInventory(eventManager, "Namo " + house.getUid() + " daiktai", (House)house);
                house.setWeedSaplings(LtrpGamemodeImpl.getDao().getHouseDao().getWeed(house));
                inventory.add(LtrpGamemodeImpl.getDao().getItemDao().getItems(house));
                house.setInventory(inventory);
                property = house;
            } else if(type.equalsIgnoreCase("garagE")) {
                property = Garage.create((Integer) params[1], params[0] + " " + params[1], entrance, exit, eventManager);
                inventory = new FixedSizeInventory(eventManager, "Garaşo " + property.getUid() + " daiktai", (Garage)property);
                inventory.add(LtrpGamemodeImpl.getDao().getItemDao().getItems((Garage)property));
                ((Garage)property).setInventory(inventory);
            } else if(type.equalsIgnoreCase("business")) {
                property = Business.create((Integer) params[1], params[0] + " " + params[1], entrance, exit, eventManager);
                //inventory = new FixedSizeInventory(eventManager, "Verslo " + property.getUid() + " daiktai", (InventoryEntity)property);
            } else {
                return 0;
            }
           return property.getUid();
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

        // OnPlayerEnterHouse(playerid, housesqlid);
        amx.registerFunction("OnPlayerEnterHouse", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            House house = House.get((Integer)params[1]);
            if(player != null && house != null) {
                eventManager.dispatchEvent(new PlayerEnterHouseEvent(player, house));
                eventManager.dispatchEvent(new PlayerEnterPropertyEvent(player, house));
            }
            return 1;
        }, Integer.class, Integer.class);

        // OnPlayerEnterBusiness(playerid, businessqlid);
        amx.registerFunction("OnPlayerEnterBusiness", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            Business business = Business.get((Integer)params[1]);
            if(player != null && business != null) {
                eventManager.dispatchEvent(new PlayerEnterBusinessEvent(player, business));
                eventManager.dispatchEvent(new PlayerEnterPropertyEvent(player, business));
            }
            return 1;
        }, Integer.class, Integer.class);

        // OnPlayerEnterGarage(playerid, garagesqlid, vehicleid);
        amx.registerFunction("OnPlayerEnterGarage", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            Garage garage = Garage.get((Integer)params[1]);
            if(player != null && garage != null) {
                LtrpVehicle vehicle = LtrpVehicle.getById((Integer) params[2]);
                eventManager.dispatchEvent(new PlayerEnterGarageEvent(player, garage, vehicle));
                eventManager.dispatchEvent(new PlayerEnterPropertyEvent(player, garage));
            }
            return 1;
        }, Integer.class, Integer.class);

        // OnPlayerExitGarage(playerid, garagesqlid, vehicleid);
        amx.registerFunction("OnPlayerExitGarage", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            Garage garage = Garage.get((Integer)params[1]);
            if(player != null && garage != null) {
                LtrpVehicle vehicle = LtrpVehicle.getById((Integer)params[2]);
                eventManager.dispatchEvent(new PlayerExitGarageEvent(player, garage, vehicle));
                eventManager.dispatchEvent(new PlayerExitPropertyEvent(player, garage));
            }
            return 1;
        }, Integer.class, Integer.class);
        // OnPlayerExitBusiness(playerid, businessssqlid);
        amx.registerFunction("OnPlayerExitBusiness", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            Business business = Business.get((Integer)params[1]);
            if(player != null && business != null) {
                eventManager.dispatchEvent(new PlayerExitBusinessEvent(player, business));
                eventManager.dispatchEvent(new PlayerExitPropertyEvent(player, business));
            }
            return 1;
        }, Integer.class, Integer.class);

        // OnPlayerExitHouse(playerid, housesqlid);
        amx.registerFunction("OnPlayerExitHouse", params -> {
            LtrpPlayer player = LtrpPlayer.get((Integer)params[0]);
            House house = House.get((Integer)params[1]);
            if(player != null && house != null) {
                eventManager.dispatchEvent(new PlayerExitHouseEvent(player, house));
                eventManager.dispatchEvent(new PlayerExitPropertyEvent(player, house));
            }
            return 1;
        }, Integer.class, Integer.class);
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
