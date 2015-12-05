package lt.ltrp.property;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.event.property.*;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PropertyManager {


    private final EventManager eventManager;
    private static PropertyManager instance;

    public static void init() {
        instance = new PropertyManager();
    }

    public static PropertyManager get() {
        if(instance == null) {
            init();
        }
        return instance;
    }

    public PropertyManager() {
        eventManager = LtrpGamemode.get().getEventManager().createChildNode();

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
            if(type.equalsIgnoreCase("House")) {
                property = House.create((Integer)params[1], params[0] + " " + params[1], entrance, exit);
            } else if(type.equalsIgnoreCase("garagE")) {
                property = Garage.create((Integer)params[1], params[0] + " " + params[1], entrance, exit);
            } else if(type.equalsIgnoreCase("business")) {
                property = Business.create((Integer)params[1], params[0] + " " + params[1], entrance, exit);
            }

           return property.getUid();
        }, String.class, Integer.class, Float.class, Float.class, Float.class, Integer.class, Integer.class, Float.class, Float.class, Float.class, Integer.class, Integer.class);

        // Usage: DestroyProperty(string type, int uid);
        amx.registerFunction("DestroyProperty", params -> {
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
        amx.registerFunction("OnPlayerEnterBusiness", params -> {
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
}
