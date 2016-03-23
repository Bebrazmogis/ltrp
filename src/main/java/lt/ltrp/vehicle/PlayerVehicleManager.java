package lt.ltrp.vehicle;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.constant.Currency;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.dao.VehicleDao;
import lt.ltrp.data.Color;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.item.Item;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.shopplugin.ShopVehicle;
import lt.ltrp.shopplugin.VehicleShop;
import lt.ltrp.shopplugin.VehicleShopPlugin;
import lt.ltrp.vehicle.event.*;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.CommandHandler;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.destroyable.DestroyEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.util.event.EventManager;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2016.03.09.
 */
public class PlayerVehicleManager {

    protected static final Collection<PlayerVehicle> playerVehiclesList = new ArrayList<>();
    private static final Logger logger = LtrpGamemode.get().getLogger();
    private static final int INSURANCE_BASE_PRICE = 800;
    private static final int SCRAP_BASE_PRICE = 200;

    private static final CommandHandler V_HELP_HANDLER = (p, params) -> {
        p.sendMessage(Color.GREEN, "|______________________Tr. Priemoniu komandos ir naudojimas__________________________|");
        p.sendMessage(Color.LIGHTRED, "  KOMANDOS NAUDOJIMAS: /v [KOMANDA], pavyzdþiui: /v list");
        p.sendMessage(Color.WHITE, "  PAGRINDINËS KOMANDOS: list, get, park, buypark, lock, find, documents ");
        p.sendMessage(Color.WHITESMOKE, "  TR. PRIEMONËS SKOLINIMAS: setpermission managePerms");
        p.sendMessage(Color.WHITE, "  TOBULINIMAS/TVARKYMAS: register buy buyalarm buylock buyinsurance");
        p.sendMessage(Color.WHITESMOKE, "  VALDYMAS: /trunk /trunko /bonnet /windows /seatbelt /maxspeed /vradio ");
        //  player.sendMessage(Color.WHITE, "  KITA: destroy scrap payticket faction buy ");
        return true;
    };


    private EventManager eventManager;
    private Map<LtrpPlayer, int[]> playerVehicles;
    private Map<Integer, SoftReference<Collection<PlayerVehiclePermission>>> permissionCache = new HashMap<>();
    private VehicleDao vehicleDao;

    public PlayerVehicleManager(EventManager manager, VehicleDao vehicleDao, PlayerCommandManager commandManager) {
        this.eventManager = manager;
        this.playerVehicles = new HashMap<>();
        this.vehicleDao = vehicleDao;

        CommandGroup vGroup = new CommandGroup();
        vGroup.setNotFoundHandler((p, g, cmd) -> {
            V_HELP_HANDLER.handle(p, null);
            return true;
        });
        vGroup.registerCommands(new PlayerVehicleCommands(this, vGroup));
        commandManager.registerChildGroup(vGroup, "v");
       // commandManager.registerCommand("v", new Class[0], V_HELP_HANDLER, null, null, null);
       // commandManager.registerCommand("v", new Class[]{String.class}, null, null, (short)-1000, false, V_HELP_HANDLER, null, null, null);

        CommandGroup acceptGroup = new CommandGroup();
        acceptGroup.registerCommand("car", new Class[0], (p, params) -> {
            LtrpPlayer player = LtrpPlayer.get(p);
            BuyVehicleOffer offer = player.getOffer(BuyVehicleOffer.class);
            if(offer == null) {
                player.sendErrorMessage("Jums niekas nesiûlo pirkti automobilio!");
            } else if(offer.getVehicle().getOwnerId() != offer.getOfferedBy().getUserId()) {
                player.sendErrorMessage("Automobilis jau parduotas.");
            } else if(getPlayerOwnedVehicleCount(player) >= getMaxOwnedVehicles(player)) {
                player.sendErrorMessage("Daugiau transporto priemoniø turëti negalite.");
            } else if(player.getMoney() < offer.getPrice()) {
                player.sendErrorMessage("Jums neuþtenka pinigø. Siûlomos transporto priemonës kaina " + Currency.SYMBOL + offer.getPrice());
            } else if(player.getDistanceToPlayer(offer.getOfferedBy()) > 5f) {
                player.sendErrorMessage(offer.getOfferedBy().getCharName() + " yra per toli!");
            } else {
                int price = offer.getPrice();
                LtrpPlayer offerer = offer.getOfferedBy();
                PlayerVehicle vehicle = offer.getVehicle();
                player.sendMessage(Color.NEWS, "Nusipirkote " + vehicle.getName() + " uþ " + Currency.SYMBOL + price + " ið " + offerer.getCharName());
                offerer.sendMessage(Color.NEWS, "Pardavëte " + vehicle.getName() + " uþ " + Currency.SYMBOL + price + " " + player.getCharName());

                eventManager.dispatchEvent(new PlayerVehicleSellEvent(offerer, vehicle, player, price));
            }
            player.getOffers().remove(offer);
            return true;
        }, null, null, null);
        commandManager.registerChildGroup(acceptGroup, "accept");

        eventManager.registerHandler(PlayerDataLoadEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            loadVehicles(p);
        });

        eventManager.registerHandler(PlayerDisconnectEvent.class, e -> playerVehicles.remove(LtrpPlayer.get(e.getPlayer())));

        eventManager.registerHandler(DestroyEvent.class, e -> {
            Optional<PlayerVehicle> vehicle = playerVehiclesList.stream().filter(v -> v.equals(e.getDestroyable())).findFirst();
            logger.debug("DestroyEvent. Vehicle: "+ vehicle.isPresent());
            if(vehicle.isPresent()) {
                destroyVehicle(vehicle.get());
            }
        });

        eventManager.registerHandler(VehicleDeathEvent.class, e -> {
            PlayerVehicle vehicle = PlayerVehicle.getByVehicle(e.getVehicle());
            logger.debug("VehicleDeathEvent:" + vehicle);
            if(vehicle != null) {
                // If the vehicle did not have insurance, it we set it's health low #28
                if(vehicle.getInsurance() == 0) {
                    vehicle.setHealth(300f + new Random().nextFloat() * 10);
                }
                vehicle.setDeaths(vehicle.getDeaths() + 1);
                vehicle.setInterior(vehicle.getInsurance() - 1);
                vehicle.destroy();
            }
        });

        eventManager.registerHandler(PlayerVehicleSellEvent.class, e -> {
            // An owner changes all the permissions he had given anyone must be removed
            // But the new owner must have all permissions as well as actual ownership
            PlayerVehicle vehicle = e.getVehicle();
            LtrpPlayer newOwner = e.getNewOwner();
            vehicle.getPermissions().keySet().forEach(vehicle::removePermissions);
            vehicleDao.removePermissions(vehicle);

            for(PlayerVehiclePermission perm : PlayerVehiclePermission.values()) {
                vehicle.addPermission(newOwner, perm);
                vehicleDao.addPermission(vehicle, newOwner, perm);
            }
            vehicleDao.setOwner(vehicle, newOwner);

            // Also we need to reload the vehicle UID arrays, well because that's the design of this whole damn thing
            loadVehicles(e.getPlayer());
            loadVehicles(newOwner);
        });

        eventManager.registerHandler(PlayerBuyNewVehicleEvent.class, e -> {
            int uid = vehicleDao.insert(
                    e.getModelId(),
                    e.getSpawnLocation(),
                    "",
                    e.getColor1(),
                    e.getColor2(),
                    0f,
                    LtrpVehicleModel.getFuelTankSize(e.getModelId()),
                    e.getPlayer().getUserId(),
                    0,
                    null,
                    null,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    1000f
            );
            for(PlayerVehiclePermission perm : PlayerVehiclePermission.values())
                    vehicleDao.addPermission(uid, e.getPlayer().getUserId(), perm);

            // Retrieve the vehicle UID list again so it would contain the newly inserted vehicle
            loadVehicles(e.getPlayer());
        });

        eventManager.registerHandler(PlayerVehicleScrapEvent.class, e -> {
            PlayerVehicle playerVehicle = e.getVehicle();
            vehicleDao.delete(playerVehicle);
            loadVehicles(e.getPlayer());
        });

        eventManager.registerHandler(PlayerVehicleParkEvent.class, e -> {
           //vehicleDao.update(e.getVehicle());
            e.getVehicle().destroy();
        });

        eventManager.registerHandler(PlayerVehicleUpdateParkEvent.class, e -> {
            e.getVehicle().setSpawnLocation(e.getLocation());
            vehicleDao.update(e.getVehicle());
        });

        eventManager.registerHandler(PlayerVehicleRemovePermissionEvent.class, e -> {
            vehicleDao.removePermission(e.getVehicle(), e.getTarget(), e.getPermission());
            e.getPlayer().sendMessage(e.getPermission().name() + " teisë sëkmingai pridëta.");
            LtrpPlayer target = LtrpPlayer.getByUserId(e.getTarget());
            if(target != null) {
                target.sendMessage(Color.NEWS, e.getPlayer().getCharName() + " suteikë jums teisæ \"" + e.getPermission().name() + "\" su jo automobiliu");
            }
        });

        eventManager.registerHandler(PlayerVehicleAddPermissionEvent.class, e -> {
           vehicleDao.addPermission(e.getVehicle(), e.getTarget(), e.getPermission());
            e.getPlayer().sendMessage(e.getPermission().name() + " teisë sëkmingai paðalinta.");
            LtrpPlayer target = LtrpPlayer.getByUserId(e.getTarget());
            if(target != null) {
                target.sendMessage(Color.NEWS, e.getPlayer().getCharName() + " atëmë ið jûsø teisæ \"" + e.getPermission().name() + "\" su jo automobiliu");
            }
        });

        eventManager.registerHandler(PlayerVehicleBuyLockEvent.class, e -> {
            vehicleDao.update(e.getVehicle());
        });

        eventManager.registerHandler(PlayerVehicleArrestEvent.class, e -> {
            PlayerVehicle vehicle = e.getVehicle();
            LtrpPlayer officer = e.getPlayer();
            vehicleDao.insertArrest(vehicle.getUUID(), officer.getUserId(), e.getReason());
            vehicle.destroy();
        });

        eventManager.registerHandler(PlayerVehicleArrestDeleteEvent.class, e -> {
            vehicleDao.removeArrest(e.getArrest());
        });

        eventManager.registerHandler(PlayerVehicleBuyAlarmEvent.class, e -> {
            vehicleDao.update(e.getVehicle());
        });

        eventManager.registerHandler(PlayerVehicleBuyInsuranceEvent.class, e -> {
            vehicleDao.update(e.getVehicle());
        });
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
        Collection<PlayerVehiclePermission> perms = vehicleDao.getPermissions(vehicleUId, player.getUserId());
        permissionCache.put(vehicleUId, new SoftReference<>(perms));
        return perms;
    }

    private void loadVehicles(LtrpPlayer p) {
        playerVehicles.put(p, vehicleDao.getPlayerVehicles(p));
    }

    public PlayerVehicle loadVehicle(int uid) {
        PlayerVehicle vehicle = vehicleDao.get(uid);
        Item[] items = LtrpGamemode.getDao().getItemDao().getItems(vehicle.getClass(), vehicle.getUUID());
        vehicle.getInventory().add(items);
        logger.info("PlayerVehicle " + uid + " loaded.");
        playerVehiclesList.add(vehicle);
        return vehicle;
    }

    public boolean isSpawned(int vehicleId) {
        return playerVehiclesList.stream().filter(v -> v.getUUID() == vehicleId).findFirst().isPresent();
    }

    public void setLicensePlate(PlayerVehicle vehicle) {
        String license = vehicleDao.generateLicensePlate();
        vehicle.setLicense(license);
        vehicleDao.update(vehicle);
    }

    public void destroyVehicle(PlayerVehicle vehicle) {
        vehicleDao.update(vehicle);
        logger.info("PlayerVehicle " + vehicle.getUUID() + " destroyed.");
        playerVehiclesList.remove(vehicle);
    }

    public PlayerVehicleArrest getArrest(int vehicleId) {
        // Could also add caching here, if there's ever a need for it
        return vehicleDao.getArrest(vehicleId);
    }

    public PlayerVehicleArrest getArrest(String licensePlate) {
        return vehicleDao.getArrest(vehicleDao.getPlayerVehicleByLicense(licensePlate));
    }

    public PlayerVehicleMetadata getMetaData(int vehicleId) {
        return vehicleDao.getPlayerVehicleMeta(vehicleId);
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
        return PlayerVehicle.get().stream()
                .filter(v -> {
                    for(int vehicleUId : playerVehicles.get(player)) {
                        if(vehicleUId == v.getUUID()) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public int getPlayerOwnedVehicleCount(LtrpPlayer player) {
        return playerVehicles.containsKey(player) ? playerVehicles.get(player).length : 0;
    }

    public int getLicensePrice() {
        return 100;
    }

    public int getMaxOwnedVehicles(LtrpPlayer player) {
        return 10;// TODO implement donations perhaps?
    }

    public int[] getVehicles(LtrpPlayer player) {
        return playerVehicles.get(player);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public VehicleShopPlugin getShopPlugin() {
        return Shoebill.get().getResourceManager().getPlugin(VehicleShopPlugin.class);
    }

    public void destroy() {
        this.playerVehicles.clear();
    }

}
