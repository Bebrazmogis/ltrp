package lt.ltrp.job.vehiclethief;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.item.ItemType;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerCountdown;
import lt.ltrp.vehicle.PlayerVehicle;
import lt.ltrp.vehicle.VehicleAlarm;
import lt.ltrp.vehicle.VehicleLock;
import lt.ltrp.vehicle.event.VehicleEngineStartEvent;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.18.
 */
public class VehicleThiefManager {

    private static final int JOB_ID = 7;


    private static VehicleThiefManager ourInstance;

    public static VehicleThiefManager getInstance() {
        if(ourInstance == null) {
            ourInstance = new VehicleThiefManager();
        }
        return ourInstance;
    }

    private EventManager eventManager;
    private VehicleThiefJob job;



    private VehicleThiefManager() {
        this.eventManager = LtrpGamemode.get().getEventManager().createChildNode();
        this.job = LtrpGamemode.getDao().getJobDao().getVehicleThiefJob(JOB_ID);

        this.eventManager.registerHandler(PaydayEvent.class, e -> {
            job.resetRequiredModels();
        });

        this.eventManager.registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null && player.getKeyState().isKeyPressed(PlayerKey.FIRE) && player.getJob() != null && player.getJob().equals(job) && player.getVehicle() != null) {
                final PlayerVehicle vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                if(vehicle != null && vehicle.getState().getEngine() != VehicleParam.PARAM_ON) {
                    if(player.getInventory().containsType(ItemType.Toolbox)) {
                        job.log("Þaidëjas " + player.getUserId() + " bando pavogti transporto priemonæ " + vehicle.getId() + ". Automobilio savininkas " + vehicle.getOwnerId());
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
                        player.sendActionMessage("ið árankiø dëþutës iðsitraukia reples, atsuktuvà ir bando ardyti spynelæ, kad uþvestu automobilá.", 30.0f);

                        PlayerCountdown playerCountdown = new PlayerCountdown(player, time, true, p -> {
                            boolean success = vehicle.getFuelTank().getFuel() != 0 && vehicle.getHealth() >= 400f;
                            if(success) {
                                vehicle.getState().setEngine(VehicleParam.PARAM_ON);
                                eventManager.dispatchEvent(new VehicleEngineStartEvent(vehicle, player, success));
                                job.log("Þaidëjas " + player.getUserId() + " sëkmingai uþkûrë automobilá " + vehicle.getId() + " kuris priklauso þaidëjui " + vehicle.getOwnerId());
                            }
                        });
                        player.setCountdown(playerCountdown);
                    } else {
                        player.sendErrorMessage("Jûs neturite árankiu dëþës!");
                    }
                }
            }
        });



    }
}
