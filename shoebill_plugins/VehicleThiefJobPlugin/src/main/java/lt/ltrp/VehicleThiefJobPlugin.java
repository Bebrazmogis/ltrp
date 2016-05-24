package lt.ltrp;

import lt.ltrp.constant.ItemType;
import lt.ltrp.dao.VehicleThiefDao;
import lt.ltrp.dao.impl.MySqlVehicleThiefDaoImpl;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.data.VehicleLock;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.vehicle.VehicleEngineStartEvent;
import lt.ltrp.object.*;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2016.05.22.
 */
public class VehicleThiefJobPlugin extends Plugin {

    private VehicleThiefJob vehicleThiefJob;
    private EventManagerNode eventManagerNode;
    private Logger logger;
    private VehicleThiefDao vehicleThiefDao;

    @Override
    protected void onEnable() throws Throwable {
        eventManagerNode = getEventManager().createChildNode();
        logger = getLogger();

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

    private void load() {
        DatabasePlugin databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin.class);
        vehicleThiefDao = new MySqlVehicleThiefDaoImpl(databasePlugin.getDataSource(), null, eventManagerNode);
        vehicleThiefJob = vehicleThiefDao.get(JobPlugin.JobId.VehicleThief.id);

        eventManagerNode.cancelAll();
        addEventHandlers();
        logger.info(getDescription().getName() + " loaded.");
    }

    private void addEventHandlers() {
        this.eventManagerNode.registerHandler(PaydayEvent.class, e -> {
            vehicleThiefJob.resetRequiredModels();
        });

        this.eventManagerNode.registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            PlayerJobData jobData = JobController.get().getJobData(player);
            // If player is pressing key FIRE and he's working as a vehicle thief AND he is in a vehicle
            if(player != null && player.getKeyState().isKeyPressed(PlayerKey.FIRE) && jobData.getJob() != null && jobData.getJob().equals(vehicleThiefJob) && player.getVehicle() != null) {
                final PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
                if(vehicle != null && vehicle.getState().getEngine() != VehicleParam.PARAM_ON) {
                    if(player.getInventory().containsType(ItemType.Toolbox)) {
                        vehicleThiefJob.log("Þaidëjas " + player.getUUID() + " bando pavogti transporto priemonæ " + vehicle.getId() + ". Automobilio savininkas " + vehicle.getOwnerId());
                        int time = 60;
                        if(vehicle.getAlarm() != null) {
                            VehicleAlarm alarm = vehicle.getAlarm();
                            alarm.activate();
                        }
                        if(vehicle.getLock() != null) {
                            VehicleLock lock = vehicle.getLock();
                            time = lock.getCrackTime();
                        }
                        LtrpPlayer.sendAdminMessage(player.getName() + " pradëjo vogti automobilá. Galbût norësite tai stebëti.");
                        player.sendActionMessage("ið árankiø dëþutës iðsitraukia reples, atsuktuvà ir bando ardyti spynelæ, kad uþvestø automobilá.", 30.0f);

                        PlayerCountdown playerCountdown = PlayerCountdown.create(player, time, true, (p, finished) -> {
                            if(finished) {
                                boolean success = vehicle.getFuelTank().getFuel() != 0 && vehicle.getHealth() >= 400f;
                                if(success) {
                                    vehicle.getState().setEngine(VehicleParam.PARAM_ON);
                                    eventManagerNode.dispatchEvent(new VehicleEngineStartEvent(vehicle, player, success));
                                    vehicleThiefJob.log("Þaidëjas " + player.getUUID() + " sëkmingai uþkûrë automobilá " + vehicle.getId() + " kuris priklauso þaidëjui " + vehicle.getOwnerId());
                                } else
                                    vehicle.sendStateMessage("pasigirsta bandymas uþkurti, bet variklis ið karto iðsijungia");
                            } else
                                vehicle.sendStateMessage("pasigirsta bandymas uþkurti, bet variklis ið karto iðsijungia");
                        });
                        player.setCountdown(playerCountdown);
                    } else {
                        player.sendErrorMessage("Jûs neturite árankiu dëþës!");
                    }
                }
            }
        });
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManagerNode.cancelAll();
        logger.info(getDescription().getName() + " disabled.");
    }
}
