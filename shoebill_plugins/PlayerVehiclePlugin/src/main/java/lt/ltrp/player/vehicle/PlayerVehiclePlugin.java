package lt.ltrp.player.vehicle;

import lt.ltrp.player.vehicle.command.GeneralPlayerVehicleCommands;
import lt.ltrp.player.vehicle.command.PlayerVehicleAcceptCommand;
import lt.ltrp.player.vehicle.command.PlayerVehicleCommands;
import lt.ltrp.player.vehicle.constant.PlayerVehiclePermission;
import lt.ltrp.player.vehicle.dao.PlayerVehicleArrestDao;
import lt.ltrp.player.vehicle.dao.PlayerVehicleDao;
import lt.ltrp.player.vehicle.dao.PlayerVehicleFineDao;
import lt.ltrp.player.vehicle.dao.PlayerVehiclePermissionDao;
import lt.ltrp.player.vehicle.dao.impl.MySqlPlayerVehicleArrestDaoImpl;
import lt.ltrp.player.vehicle.dao.impl.MySqlPlayerVehicleDaoImpl;
import lt.ltrp.player.vehicle.dao.impl.MySqlPlayerVehicleFineDaoImpl;
import lt.ltrp.player.vehicle.dao.impl.MySqlPlayerVehiclePermissionDaoImpl;
import lt.ltrp.data.*;
import lt.ltrp.player.vehicle.data.PlayerVehicleArrest;
import lt.ltrp.player.vehicle.data.PlayerVehicleMetadata;
import lt.ltrp.player.vehicle.object.PlayerVehicle;
import lt.ltrp.player.vehicle.object.VehicleAlarm;
import lt.ltrp.vehicle.VehiclePlugin;
import lt.ltrp.player.vehicle.event.PlayerVehicleDestroyEvent;
import lt.ltrp.player.vehicle.event.PlayerVehicleSellEvent;
import lt.ltrp.object.*;
import lt.ltrp.player.vehicle.object.impl.PersonalAlarm;
import lt.ltrp.player.vehicle.object.impl.PlayerVehicleImpl;
import lt.ltrp.player.vehicle.object.impl.PoliceAlertAlarm;
import lt.ltrp.player.vehicle.object.impl.SimpleAlarm;
import lt.ltrp.shopplugin.ShopVehicle;
import lt.ltrp.shopplugin.VehicleShop;
import lt.ltrp.shopplugin.VehicleShopPlugin;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.CommandHandler;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.05.31.
 *
 *         This plugin is responsible for player vehicles which are represented by [PlayerVehicle] object
 *
 *         Player vehicles are based on permissions. In theory there should be no such thing as an "owner"
 *         Permissions should be dynamic for extensibility
 *
 *
 *         // TODO vehicle arrests, vehicle fines should be moved to separate modules
 */
public class PlayerVehiclePlugin extends Plugin {

    private static final int INSURANCE_BASE_PRICE = 800;
    private static final int SCRAP_BASE_PRICE = 200;

    private static final CommandHandler V_HELP_HANDLER = (p, params) -> {
        p.sendMessage(Color.GREEN, "|______________________Tr. Priemoniu komandos ir naudojimas__________________________|");
        p.sendMessage(Color.LIGHTRED, "  KOMANDOS NAUDOJIMAS: /v [KOMANDA], pavyzdþiui: /v list");
        p.sendMessage(Color.WHITE, "  PAGRINDINËS KOMANDOS: list, get, park, buypark, lock, find, documents ");
        p.sendMessage(Color.WHITESMOKE, "  TR. PRIEMONËS SKOLINIMAS: setpermission managePerms");
        p.sendMessage(Color.WHITE, "  TOBULINIMAS/TVARKYMAS: register buy buyalarm buylock buyinsurance");
        p.sendMessage(Color.WHITE, "  KITA: destroy scrap payfine buy radio fines"); // tODO faction
        p.sendMessage(Color.WHITE, "  VALDYMAS: /trunk /trunko /bonnet /windows /seatbelt /maxspeed ");
        return true;
    };

    private Logger logger;
    private EventManagerNode eventManager;
    private List<PlayerVehicle> playerVehicles;
    private PlayerVehicleDao vehicleDao;
    private Map<LtrpPlayer, int[]> playerVehicleUUIDs;
    private Map<Integer, SoftReference<Collection<PlayerVehiclePermission>>> permissionCache = new HashMap<>();
    private PlayerCommandManager playerCommandManager;
    private PlayerVehicleArrestDao vehicleArrestDao;
    private PlayerVehiclePermissionDao vehiclePermissionDao;
    private PlayerVehicleFineDao vehicleFineDao;

    @Override
    protected void onEnable() throws Throwable {
        this.logger = getLogger();
        this.playerVehicles = new ArrayList<>();
        this.playerVehicleUUIDs = new HashMap<>();
        this.permissionCache = new HashMap<>();
        eventManager = getEventManager().createChildNode();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(VehiclePlugin.class);
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

    private void load() {
        eventManager.cancelAll();
        DataSource datasource = DatabasePlugin.get(DatabasePlugin.class).getDataSource();
        this.vehicleDao = new MySqlPlayerVehicleDaoImpl(datasource, eventManager);
        this.vehicleArrestDao = new MySqlPlayerVehicleArrestDaoImpl(datasource);
        this.vehiclePermissionDao = new MySqlPlayerVehiclePermissionDaoImpl(datasource);
        this.vehicleFineDao = new MySqlPlayerVehicleFineDaoImpl(datasource);

        registerCommands();
        registerEventHandlers();
        logger.info(getDescription().getName() + " loaded");
    }

    private void registerEventHandlers() {

        eventManager.registerHandler(PlayerDataLoadEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            loadVehicles(p);
        });

        // Remove the loaded vehicle UUIDs
        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> playerVehicleUUIDs.remove(LtrpPlayer.get(e.getPlayer())));

        eventManager.registerHandler(PlayerVehicleDestroyEvent.class, e -> {
            PlayerVehicle vehicle = e.getVehicle();
            logger.debug("PlayerVehicleDestroyEvent. Vehicle: "+ vehicle);
            destroyVehicle(vehicle);

        });

        eventManager.registerHandler(VehicleDeathEvent.class, e -> {
            PlayerVehicle vehicle = PlayerVehicle.Companion.getByVehicle(e.getVehicle());
            logger.debug("VehicleDeathEvent:" + vehicle);
            if(vehicle != null) {
                // If the vehicle did not have insurance, it we set it's health low #28
                if(vehicle.getInsurance() == 0) {
                    vehicle.setHealth(300f + new Random().nextFloat() * 10);
                } else {
                    vehicle.setHealth(1000f);
                }
                vehicle.setDeaths(vehicle.getDeaths() + 1);
                vehicle.setInterior(vehicle.getInsurance() - 1);
                vehicle.destroy();
            }
        });


        eventManager.registerHandler(PlayerBuyNewVehicleEvent.class, HandlerPriority.HIGHEST, e -> {
            // Retrieve the vehicle UID list again so it would contain the newly inserted vehicle
            loadVehicles(e.getPlayer());
        });

        eventManager.registerHandler(PlayerVehicleSellEvent.class, HandlerPriority.HIGHEST, e -> {
            // Also we need to reload the vehicle UID arrays, well because that's the design of this whole damn thing
            loadVehicles(e.getPlayer());
            loadVehicles(e.getNewOwner());
        });
    }

    private void registerCommands() {
        playerCommandManager = new PlayerCommandManager(eventManager);
        CommandGroup vGroup = new CommandGroup();
        vGroup.setNotFoundHandler((p, g, cmd) -> {
            V_HELP_HANDLER.handle(p, null);
            return true;
        });
        vGroup.registerCommands(new PlayerVehicleCommands(this, vGroup, eventManager));


        CommandGroup acceptGroup = new CommandGroup();
        acceptGroup.registerCommands(new PlayerVehicleAcceptCommand(eventManager));

        playerCommandManager.registerChildGroup(vGroup, "v");
        playerCommandManager.registerChildGroup(acceptGroup, "accept");
        playerCommandManager.registerCommands(new GeneralPlayerVehicleCommands());
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManager.cancelAll();
        playerVehicles.clear();
        playerCommandManager.uninstallAllHandlers();
        playerCommandManager.destroy();
    }

    public PlayerVehicle createVehicle(int id, int modelId, AngledLocation location, int color1, int color2, int ownerId,
                                       int deaths, FuelTank fueltank, float mileage, String license, int insurance, VehicleAlarm alarm,
                                       VehicleLock lock, int doors, int panels, int lights, int tires, float health, EventManager eventManager) {
        PlayerVehicle vehicle = new PlayerVehicleImpl(id, modelId, location, color1, color2, ownerId, deaths, fueltank, mileage, license, insurance, alarm, lock, doors, panels, lights, tires, health, eventManager);
        playerVehicles.add(vehicle);
        VehiclePlugin.get(VehiclePlugin.class).getVehicles().add(vehicle);
        return vehicle;
    }

    public PlayerVehicleDao getVehicleDao() {
        return vehicleDao;
    }

    public PlayerVehicleFineDao getFineDao() {
        return vehicleFineDao;
    }

    public List<PlayerVehicle> getVehicles() {
        return playerVehicles;
    }

    public int[] getVehicleUUIDs(LtrpPlayer player) {
        return vehicleDao.getPlayerVehicles(player);
    }

    public PlayerVehicleMetadata getMetaData(int vehicleId) {
        return vehicleDao.getPlayerVehicleMeta(vehicleId);
    }

    public PlayerVehicleArrest getArrest(int vehicleId) {
        // Could also add caching here, if there's ever a need for it
        return vehicleArrestDao.get(vehicleId);
    }

    public PlayerVehicleArrest getArrest(String licensePlate) {
        return vehicleArrestDao.get(vehicleDao.getPlayerVehicleByLicense(licensePlate));
    }



    public int[] getArrestedVehicles(LtrpPlayer player) {
        return vehicleDao.getArrestedPlayerVehicles(player);
    }

    public int getInsurancePrice(PlayerVehicle vehicle) {
        return INSURANCE_BASE_PRICE + vehicle.getDeaths() * 300 + vehicle.getInsurance() * 100 + Math.round(vehicle.getMileage() * 0.1f);
    }

    public int getParkingSpaceCost(Location location) {
        return 150;
    }

    public int getScrapPrice(PlayerVehicle vehicle) {
        int averageShopPrice = 0;
        int shopCount = 0;
        VehicleShopPlugin shopPlugin = getShopPlugin();
        if(shopPlugin == null) {
            return SCRAP_BASE_PRICE;
        }
        VehicleShop[] shops = shopPlugin.getVehicleShops();
        for(VehicleShop shop : shops) {
            for(ShopVehicle v : shop.getVehicles()) {
                if(v.getModelId() == vehicle.getModelId()) {
                    averageShopPrice += v.getPrice();
                    shopCount++;
                }
            }
        }
        averageShopPrice /= shopCount;
        int lockPrice = 0;
        if(vehicle.getLock() != null) {
            for(VehicleLock l : PlayerVehicleCommands.LOCKS) {
                if(l.equals(vehicle.getLock())) {
                    lockPrice = l.getPrice();
                    break;
                }
            }
        }
        int alarmPrice = 0;
        if(vehicle.getAlarm() != null) {
            for(Pair<VehicleAlarm, Integer> p : PlayerVehicleCommands.ALARMS) {
                if(p.getKey().equals(vehicle.getAlarm())) {
                    alarmPrice = p.getValue();
                    break;
                }
            }
        }
        int price = averageShopPrice -
                (averageShopPrice / 20 * vehicle.getDeaths()) -
                (Math.round(vehicle.getMileage() / 10f)) -
                (Math.round(1000f - vehicle.getHealth()) / 2) +
                (alarmPrice / 3) +
                (lockPrice / 3);
        if(price <= 0) {
            price = SCRAP_BASE_PRICE;
        }
        return price;
    }

    public Collection<PlayerVehicle> getSpawnedVehicles(LtrpPlayer player) {
        return PlayerVehicle.Companion.get().stream()
                .filter(v -> {
                    for(int vehicleUId : playerVehicleUUIDs.get(player)) {
                        if(vehicleUId == v.getUUID()) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public int getPlayerOwnedVehicleCount(LtrpPlayer player) {
        return playerVehicleUUIDs.containsKey(player) ? playerVehicleUUIDs.get(player).length : 0;
    }

    public int getLicensePrice() {
        return 100;
    }

    public int getMaxOwnedVehicles(LtrpPlayer player) {
        return 10;// TODO implement donations perhaps?
    }


    public boolean isSpawned(int vehicleUUID) {
        return playerVehicles.stream().filter(v -> v.getUUID() == vehicleUUID).findFirst().isPresent();
    }

    public void setLicensePlate(PlayerVehicle vehicle) {
        String license = vehicleDao.generateLicensePlate();
        vehicle.setLicense(license);
        vehicleDao.update(vehicle);
    }

// TODO
    public PlayerVehicle loadVehicle(int uid) {
        PlayerVehicle vehicle = vehicleDao.get(uid);
        //Item[] items = ItemController.get().getItemDao().getItems(vehicle);
        //vehicle.getInventory().add(items);
        logger.info("PlayerVehicle " + uid + " loaded.");
        return vehicle;
    }


    public void destroyVehicle(PlayerVehicle vehicle) {
        vehicleDao.update(vehicle);
        vehicle.destroy();
        logger.info("PlayerVehicle " + vehicle.getUUID() + " destroyed.");
        playerVehicles.remove(vehicle);
    }


    public Collection<PlayerVehiclePermission> getPermissions(int vehicleUId, LtrpPlayer player) {
        // Caching would be nice
        if(permissionCache.containsKey(vehicleUId)) {
            SoftReference<Collection<PlayerVehiclePermission>> ref = permissionCache.get(vehicleUId);
            if(ref.get() != null) {
                return ref.get();
            } else {
                permissionCache.remove(vehicleUId);
            }
        }
        Collection<PlayerVehiclePermission> perms = vehiclePermissionDao.get(vehicleUId, player.getUUID());
        permissionCache.put(vehicleUId, new SoftReference<>(perms));
        return perms;
    }

    public VehicleAlarm createAlarm(PlayerVehicle playerVehicle, int level) {
        switch(level) {
            case 2:
                return new PoliceAlertAlarm(playerVehicle);
            case 3:
                return new PersonalAlarm(playerVehicle);
            default:
                return new SimpleAlarm(playerVehicle);
        }
    }

    public VehicleShopPlugin getShopPlugin() {
        return ResourceManager.get().getPlugin(VehicleShopPlugin.class);
    }

    public PlayerVehicle getByVehicle(Vehicle vehicle) {
        LtrpVehicle v = LtrpVehicle.getByVehicle(vehicle);
        return v instanceof PlayerVehicle ? (PlayerVehicle)v : null;
    }

    public PlayerVehicle getByLicense(String license) {
        Optional<PlayerVehicle> op = PlayerVehicle.Companion.get().stream().filter(v -> v.getLicense().equals(license)).findFirst();
        return op.isPresent() ? op.get() : null;
    }

    /**
     * Permanently destroys a Player vehicle
     * @param vehicle vehicle to be destroyed
     */
    public void scrapVehicle(PlayerVehicle vehicle) {
        vehicleDao.delete(vehicle);
        loadVehicles(LtrpPlayer.get(vehicle.getOwnerId()));
    }

    public void loadVehicles(LtrpPlayer p) {
        playerVehicleUUIDs.put(p, vehicleDao.getPlayerVehicles(p));
    }

    public PlayerVehiclePermissionDao getVehiclePermissionDao() {
        return vehiclePermissionDao;
    }

    public PlayerVehicleArrestDao getVehicleArrestDao() {
        return vehicleArrestDao;
    }
}
