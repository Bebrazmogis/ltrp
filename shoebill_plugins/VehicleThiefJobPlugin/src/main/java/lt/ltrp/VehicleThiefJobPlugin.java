package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.constant.ItemType;
import lt.ltrp.dao.VehicleThiefDao;
import lt.ltrp.dao.impl.MySqlVehicleThiefDaoImpl;
import lt.ltrp.job.JobController;
import lt.ltrp.object.impl.VehicleThiefJobImpl;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.data.VehicleLock;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.vehicle.VehicleEngineStartEvent;
import lt.ltrp.object.*;
import lt.ltrp.resource.DependentPlugin;
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
public class VehicleThiefJobPlugin extends DependentPlugin {

    private VehicleThiefJob vehicleThiefJob;
    private EventManagerNode eventManagerNode;
    private Logger logger;
    private VehicleThiefDao vehicleThiefDao;

    public VehicleThiefJobPlugin() {
        addDependency(new KClassImpl<>(DatabasePlugin.class));
        addDependency(new KClassImpl<>(JobPlugin.class));
    }

    @Override
    public void onDependenciesLoaded() {
        eventManagerNode = getEventManager().createChildNode();
        logger = getLogger();
        DatabasePlugin databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin.class);
        vehicleThiefDao = new MySqlVehicleThiefDaoImpl(databasePlugin.getDataSource(), null, eventManagerNode);
        vehicleThiefJob = new VehicleThiefJobImpl(JobPlugin.JobId.VehicleThief.id, eventManagerNode);

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
            PlayerJobData jobData = player.getJobData();
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
    protected void onDisable()  {
        super.onDisable();
        eventManagerNode.cancelAll();
        logger.info(getDescription().getName() + " disabled.");
    }
}
