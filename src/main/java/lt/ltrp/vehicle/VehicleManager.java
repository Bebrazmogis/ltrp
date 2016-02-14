package lt.ltrp.vehicle;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.Factorial;
import lt.ltrp.command.PlayerCommandManager;
import lt.ltrp.constant.LicenseType;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.job.Rank;
import lt.ltrp.job.Job;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.event.SpeedometerTickEvent;
import lt.ltrp.vehicle.event.VehicleEngineKillEvent;
import lt.ltrp.vehicle.event.VehicleEngineStartEvent;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.amx.AmxUnloadEvent;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.server.GameModeInitEvent;
import net.gtaun.shoebill.event.vehicle.VehicleEnterEvent;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * @author Bebras
 *         2015.12.06.
 */
public class VehicleManager {

    private static final Logger logger = LoggerFactory.getLogger(VehicleManager.class);


    private static VehicleManager manager;


    private EventManager eventManager;
    private Timer timer;
    private Map<LtrpVehicle, Timer> vehicleEngineTimers;
    private Map<LtrpPlayer, Float> maxSpeeds;
    private Random random;


    public static VehicleManager get() {
        if(manager == null) {
            manager = new VehicleManager();
        }
        return manager;
    }

    private VehicleManager() {
        this.maxSpeeds = new HashMap<>();
        random = new Random();
        eventManager = LtrpGamemode.get().getEventManager().createChildNode();
        this.vehicleEngineTimers = new HashMap<>();

        PlayerCommandManager commandManager = new PlayerCommandManager(HandlerPriority.NORMAL, eventManager);
        commandManager.registerCommands(new VehicleCommands(maxSpeeds));
        commandManager.registerCommands(new PlayerVehicleCommands());


        eventManager.registerHandler(AmxLoadEvent.class, e -> {
           registerPawnFunctions(e.getAmxInstance());

        });

        eventManager.registerHandler(AmxUnloadEvent.class, e -> {
           unregisterPawnFunctions(e.getAmxInstance());
        });

        eventManager.registerHandler(GameModeInitEvent.class, e -> {

        });

        eventManager.registerHandler(PlayerStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null) {
                LtrpVehicle vehicle = player.getVehicle();
                PlayerState newState = e.getPlayer().getState();
                PlayerState oldState = e.getOldState();
                if(vehicle != null) {
                    if(newState == PlayerState.DRIVER) {
                        vehicle.setDriver(player);
                        player.setLastUsedVehicle(vehicle);
                    }
                }
                if(newState == PlayerState.DRIVER) {
                    player.getInfoBox().showSpeedometer(player.getVehicle());
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

        eventManager.registerHandler(VehicleEnterEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            LtrpVehicle vehicle = LtrpVehicle.getByVehicle(e.getVehicle());
            if(player != null && vehicle != null) {
                VehicleModel.VehicleType type = VehicleModel.getType(vehicle.getModelId());
                // If it's an aicraft but the player isn't licensed to use one
                if (type == VehicleModel.VehicleType.AIRCRAFT && !player.getLicenses().contains(LicenseType.Aircraft)) {
                    Location location = player.getLocation();
                    location.setZ(location.getZ() + 1f);
                    player.setLocation(location);
                    player.sendErrorMessage("Jûs neturite teisës valdyti arba nemokate valdyti lëktuvo.");
                } else if(type == VehicleModel.VehicleType.BOAT && !player.getLicenses().contains(LicenseType.Ship)) {
                    Location location = player.getLocation();
                    location.setZ(location.getZ() + 1f);
                    player.setLocation(location);
                    player.sendErrorMessage("Jûs neturite teisës valdyti arba nemokate valdyti laivo.");
                }
            }
        });

        eventManager.registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer().getId());
            if(player != null && player.getKeyState().isKeyPressed(PlayerKey.FIRE) && !e.getOldState().isKeyPressed(PlayerKey.FIRE)) {
                LtrpVehicle vehicle = player.getVehicle();
                if(vehicle != null && LtrpVehicleModel.isMotorVehicle(vehicle.getModelId())) {
                    if(vehicle.getState().getEngine() != VehicleParam.PARAM_ON) {
                        if(!vehicleEngineTimers.containsKey(vehicle)) {
                            int time;
                            int percentage;
                            float dmg = 1000 - vehicle.getHealth();
                            if(vehicle instanceof PlayerVehicle) {
                                int deaths = ((PlayerVehicle)vehicle).getDeaths();
                                time = 1500 + deaths * 150;
                                percentage = (int) (100 - Factorial.get((int)(deaths * 1.5)) - dmg / 40f);
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
                                        //vehicle.sendStateMessage("variklis uþsikuria");
                                        vehicle.getState().setEngine(VehicleParam.PARAM_ON);
                                    } else {
                                        //vehicle.sendStateMessage("variklis neuþsikuria");
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

        eventManager.registerHandler(VehicleEngineStartEvent.class, e -> {
            LtrpVehicle vehicle = e.getVehicle();
            if(e.isSuccess())
                vehicle.sendStateMessage("Automobilio variklis uþsivedë.");
            else
                vehicle.sendStateMessage("Automobilio variklis neuþsiveda.");
        });

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

        logger.info("Vehicle manager initialized");
    }



    private void registerPawnFunctions(AmxInstance amx) {

        // createJobVehicle(vehuid, jobid, modelid, x, y, z, angle, color1, color2, min_rank_nr, plate[])
        amx.registerFunction("createJobVehicle", params -> {
            logger.info("createJobVehicle called amx handle:"  + amx.getHandle());
            int id = (Integer)params[0];
            Job job = Job.get((Integer)params[1]);
            int modelid = (Integer)params[2];
            AngledLocation location = new AngledLocation((Float)params[3], (Float)params[4],(Float)params[5],(Float)params[6]);
            Rank rank = null;
            if(job != null)
                rank = job.getRank((Integer)params[9]);

            JobVehicle jobVehicle = JobVehicle.create(id, job, modelid, location, (Integer)params[7], (Integer)params[8], rank);
            jobVehicle.setLicense((String)params[10]);
            return jobVehicle != null ? 1 : 0;
        }, Integer.class, Integer.class, Integer.class, Float.class, Float.class, Float.class, Float.class, Integer.class, Integer.class, Integer.class, String.class);

    }

    private void unregisterPawnFunctions(AmxInstance amx) {
        amx.unregisterFunction("createJobVehicle");
        amx.unregisterFunction("createPlayerVehicle");
    }

}
