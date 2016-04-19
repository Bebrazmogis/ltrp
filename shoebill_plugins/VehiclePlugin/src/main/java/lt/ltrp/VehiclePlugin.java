package lt.ltrp;

import lt.ltrp.constant.ItemType;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.dao.VehicleDao;
import lt.ltrp.dao.impl.MySqlVehicleDaoImpl;
import lt.ltrp.data.FuelTank;
import lt.ltrp.event.vehicle.SpeedometerTickEvent;
import lt.ltrp.event.vehicle.VehicleDestroyEvent;
import lt.ltrp.event.vehicle.VehicleEngineKillEvent;
import lt.ltrp.event.vehicle.VehicleEngineStartEvent;
import lt.ltrp.object.*;
import lt.ltrp.object.impl.LtrpVehicleImpl;
import lt.ltrp.object.impl.PersonalAlarm;
import lt.ltrp.object.impl.PoliceAlertAlarm;
import lt.ltrp.object.impl.SimpleAlarm;
import lt.ltrp.util.Factorial;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.resource.ResourceLoadEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * @author Bebras
 *         2015.12.06.
 */
public class VehiclePlugin extends Plugin implements VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehiclePlugin.class);

    private List<LtrpVehicle> vehicles;
    private VehicleDao vehicleDao;
    private EventManagerNode eventManager;
    private Timer timer, fuelUseTimer;
    private Map<LtrpVehicle, Timer> vehicleEngineTimers;
    private Map<LtrpPlayer, Float> maxSpeeds;
    private Random random;
    private PlayerVehicleManager playerVehicleManager;



    @Override
    protected void onEnable() throws Throwable {
        this.vehicles = new ArrayList<>();
        this.maxSpeeds = new HashMap<>();
        this.random = new Random();
        this.eventManager = getEventManager().createChildNode();
        this.vehicleEngineTimers = new HashMap<>();
        eventManager.registerHandler(ResourceLoadEvent.class, e -> {
            if(e.getResource().getClass().equals(DatabasePlugin.class)) {
                this.vehicleDao = new MySqlVehicleDaoImpl((((DatabasePlugin)e.getResource())).getDataSource(), eventManager);
                PlayerCommandManager commandManager = new PlayerCommandManager(eventManager);
                this.playerVehicleManager = new PlayerVehicleManager(eventManager, vehicleDao, commandManager);
                commandManager.registerCommands(new VehicleCommands(maxSpeeds, eventManager));
                commandManager.installCommandHandler(HandlerPriority.NORMAL);

                replaceCommandTypeParsers();
                createTimers();
                addEventHandlers();
                logger.info("Vehicle manager initialized");
            }
        });
    }
    private void replaceCommandTypeParsers() {
        PlayerCommandManager.replaceTypeParser(LtrpVehicle.class, s -> {
            return LtrpVehicle.getById(Integer.parseInt(s));
        });
    }

    private void addEventHandlers() {
        eventManager.registerHandler(VehicleDestroyEvent.class, e -> {
            vehicleDao.update(e.getVehicle());
        });
        eventManager.registerHandler(VehicleDeathEvent.class, e -> {
            LtrpVehicle vehicle = LtrpVehicle.getByVehicle(e.getVehicle());
            if(vehicle != null) {
                // Once a vehicle dies, car audio is removed. Sort of item sink
                if(vehicle.getInventory().containsType(ItemType.CarAudio)) {
                    Item item = vehicle.getInventory().getItem(ItemType.CarAudio);
                    vehicle.getInventory().remove(item);
                    item.destroy();
                }
            }
        });
        eventManager.registerHandler(PlayerStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {
                LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
                PlayerState newState = e.getPlayer().getState();
                PlayerState oldState = e.getOldState();
                if(vehicle != null) {
                    if(newState == PlayerState.DRIVER) {
                        vehicle.setDriver(player);
                        player.setLastUsedVehicle(vehicle);
                    }
                }
                if(newState == PlayerState.DRIVER) {
                    player.getInfoBox().showSpeedometer(LtrpVehicle.getByVehicle(player.getVehicle()));
                }

                if(oldState == PlayerState.DRIVER) {
                    player.getLastUsedVehicle().setDriver(null);
                    player.getInfoBox().hideSpeedometer();
                }
                if(newState == PlayerState.ONFOOT) {
                    if(player.isSeatbelt()) {
                        player.sendActionMessage("iðlipdamas atsisega saugos dirþus");
                        player.setSeatbelt(false);
                    }
                }
            }
        });
        eventManager.registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer().getId());
            if(player != null && player.getKeyState().isKeyPressed(PlayerKey.FIRE) && !e.getOldState().isKeyPressed(PlayerKey.FIRE)) {
                LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
                if(vehicle != null && LtrpVehicleModel.isMotorVehicle(vehicle.getModelId())) {
                    if(vehicle.getState().getEngine() != VehicleParam.PARAM_ON) {
                        if(!vehicleEngineTimers.containsKey(vehicle)) {
                            int time;
                            int percentage;
                            float dmg = 1000 - vehicle.getHealth();
                            if(vehicle instanceof PlayerVehicle) {
                                int deaths = ((PlayerVehicle)vehicle).getDeaths();
                                time = 1500 + deaths * 150;
                                percentage = (int) (100 - Factorial.get((int) (deaths * 1.5)) - dmg / 40f);
                                logger.debug("Vehicle start percentage:" + percentage);
                            } else {
                                time = 1500;
                                percentage = 100 - (int)dmg / 40;
                            }

                            // Special cased of failure:
                            if(vehicle.getHealth() < 400f) {
                                percentage = 0;
                            } else if(vehicle.getFuelTank().getFuel() == 0) {
                                percentage = 0;
                            }

                            int value = random.nextInt(100);
                            final boolean success = value < percentage;
                            VehicleEngineStartEvent event = new VehicleEngineStartEvent(vehicle, player, success);
                            eventManager.dispatchEvent(event);
                            if(!event.isDenied()) {
                                player.sendStateMessage("pasuka automobilio raktelá ir bando uþvesti variklá.");
                                Timer t = Timer.create(time, 1, ticks -> {
                                    if (success) {
                                        vehicle.sendStateMessage("variklis uþsikuria");
                                        vehicle.getState().setEngine(VehicleParam.PARAM_ON);
                                    } else {
                                        vehicle.sendStateMessage("variklis neuþsikuria");
                                    }
                                    vehicleEngineTimers.remove(vehicle);
                                });
                                t.start();
                                vehicleEngineTimers.put(vehicle, t);
                            } else
                                logger.debug("VehicleEngineStartEvent denied for vehicle " + vehicle + " by " + player);
                        }
                    } else {
                        VehicleEngineKillEvent event = new VehicleEngineKillEvent(vehicle, player);
                        eventManager.dispatchEvent(event);
                        if(!event.isDenied()) {
                            player.sendActionMessage("pasuka automobilio raktelá ir iðjungia variklá.");
                            vehicle.getState().setEngine(VehicleParam.PARAM_OFF);
                        } else
                            logger.debug("VehicleEngineKillEvent for " + vehicle.toString() + " denied.");
                    }
                }
            }
        });
    }

    private void createTimers() {
        timer = Timer.create(160, ticks -> {
            for(LtrpVehicle vehicle : LtrpVehicle.get()) {
                LtrpPlayer player = vehicle.getDriver();
                if(player != null) {
                    float speed = vehicle.getSpeed();
                    Velocity velocity = vehicle.getVelocity();
                    eventManager.dispatchEvent(new SpeedometerTickEvent(player, vehicle, speed));
                    player.getInfoBox().update();

                    if(maxSpeeds.containsKey(player)) {
                        float maxSpeed = maxSpeeds.get(player);
                        if(speed > maxSpeed) {
                            float cap = maxSpeeds.get(player);
                            double change = (cap-7) / speed;
                            velocity.setX((float)(velocity.getX() * change));
                            velocity.setY((float)(velocity.getY() * change));
                            vehicle.setVelocity(velocity);
                        }
                    }

                }
            }
        });
        timer.start();

        this.fuelUseTimer = Timer.create(60000, (i) -> {
            LtrpVehicle.get()
                    .stream()
                    .filter(v -> v.getState().getEngine() == VehicleParam.PARAM_ON)
                    .forEach(v -> {
                        FuelTank fuelTank = v.getFuelTank();
                        fuelTank.setFuel(fuelTank.getFuel() - LtrpVehicleModel.getFuelConsumption(v.getModelId()));
                        if(fuelTank.getFuel() <= 0) {
                            fuelTank.setFuel(0f);
                            v.getState().setEngine(VehicleParam.PARAM_OFF);
                            v.sendActionMessage("Variklis uþgæsta..");
                        }
                    });
        });
        this.fuelUseTimer.start();
    }

    @Override
    protected void onDisable() throws Throwable {
        timer.stop();
        timer.destroy();
        fuelUseTimer.stop();
        fuelUseTimer.destroy();
        playerVehicleManager.destroy();
        eventManager.cancelAll();
    }

    public PlayerVehicleManager getPlayerVehicleManager() {
        return playerVehicleManager;
    }


    @Override
    public LtrpVehicle getById(int i) {
        Optional<LtrpVehicle> op = vehicles.stream().filter(v -> v.getId() == i).findFirst();
        return op.isPresent() ? op.get() : null;
    }

    @Override
    public LtrpVehicle getByVehicle(Vehicle vehicle) {
        Optional<LtrpVehicle> op = vehicles.stream().filter(v -> ((LtrpVehicleImpl) v).getVehicleObject().equals(vehicle)).findFirst();
        return op.isPresent() ? op.get() : null;
    }

    @Override
    public LtrpVehicle getByUniqueId(int uuid) {
        Optional<LtrpVehicle> op = vehicles.stream().filter(v -> v.getUUID() == uuid).findFirst();
        return op.isPresent() ? op.get() : null;
    }

    @Override
    public LtrpVehicle getClosest(Location location, float maxDistance) {
        Optional<LtrpVehicle> op = vehicles
                .stream()
                .filter(v -> v.getLocation().distance(location) < maxDistance)
                .min((v1, v2) -> Float.compare(v1.getLocation().distance(location), v2.getLocation().distance(location)));
        return op.isPresent() ? op.get() : null;
    }

    @Override
    public List<LtrpVehicle> getVehicles() {
        return vehicles;
    }

    @Override
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

    @Override
    public LtrpVehicle createVehicle(int id, int modelId, AngledLocation location, int color1, int color2, String license, float mileage) {
        float tankSize = LtrpVehicleModel.getFuelTankSize(modelId);
        LtrpVehicle vehicle = createVehicle(id, modelId, location, color1, color2, new FuelTank(tankSize, tankSize), license, mileage);
        return vehicle;
    }

    @Override
    public LtrpVehicle createVehicle(int id, int modelId, AngledLocation location, int color1, int color2, FuelTank fueltank, String license, float mileage) {
        LtrpVehicleImpl impl = new LtrpVehicleImpl(id, modelId, location, color1, color2, fueltank, license, mileage, eventManager);
        vehicles.add(impl);
        return impl;
    }

    @Override
    public VehicleDao getDao() {
        return vehicleDao;
    }
}
