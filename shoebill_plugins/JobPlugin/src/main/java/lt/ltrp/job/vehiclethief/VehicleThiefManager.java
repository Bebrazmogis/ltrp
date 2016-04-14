package lt.ltrp.job.vehiclethief;

import lt.ltrp.LoadingException;
import lt.ltrp.LtrpGamemodeImpl;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.item.ItemType;
import lt.ltrp.job.AbstractJobManager;
import lt.ltrp.job.Job;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.player.object.PlayerCountdown;
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
public class VehicleThiefManager extends AbstractJobManager {

    private VehicleThiefJob job;

    public VehicleThiefManager(EventManager manager, int jobid) throws LoadingException {
        super(manager);
        this.job = LtrpGamemodeImpl.getDao().getJobDao().getVehicleThiefJob(jobid);

        this.eventManagerNode.registerHandler(PaydayEvent.class, e -> {
            job.resetRequiredModels();
        });

        this.eventManagerNode.registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null && player.getKeyState().isKeyPressed(PlayerKey.FIRE) && player.getJob() != null && player.getJob().equals(job) && player.getVehicle() != null) {
                final PlayerVehicle vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                if(vehicle != null && vehicle.getState().getEngine() != VehicleParam.PARAM_ON) {
                    if(player.getInventory().containsType(ItemType.Toolbox)) {
                        job.log("�aid�jas " + player.getUUID() + " bando pavogti transporto priemon� " + vehicle.getId() + ". Automobilio savininkas " + vehicle.getOwnerId());
                        int time = 60;
                        if(vehicle.getAlarm() != null) {
                            VehicleAlarm alarm = vehicle.getAlarm();
                            alarm.activate();
                        }
                        if(vehicle.getLock() != null) {
                            VehicleLock lock = vehicle.getLock();
                            time = lock.getCrackTime();
                        }
                        LtrpPlayer.sendAdminMessage(player.getName() + " prad�jo vogti automobil�. Galb�t nor�site tai steb�ti.");
                        player.sendActionMessage("i� �ranki� d��ut�s i�sitraukia reples, atsuktuv� ir bando ardyti spynel�, kad u�vestu automobil�.", 30.0f);

                        PlayerCountdown playerCountdown = PlayerCountdown.create(player, time, true, (p, finished) -> {
                            if(finished) {
                                boolean success = vehicle.getFuelTank().getFuel() != 0 && vehicle.getHealth() >= 400f;
                                if(success) {
                                    vehicle.getState().setEngine(VehicleParam.PARAM_ON);
                                    eventManagerNode.dispatchEvent(new VehicleEngineStartEvent(vehicle, player, success));
                                    job.log("�aid�jas " + player.getUUID() + " s�kmingai u�k�r� automobil� " + vehicle.getId() + " kuris priklauso �aid�jui " + vehicle.getOwnerId());
                                }
                            }
                        });
                        player.setCountdown(playerCountdown);
                    } else {
                        player.sendErrorMessage("J�s neturite �rankiu d���s!");
                    }
                }
            }
        });



    }

    @Override
    public Job getJob() {
        return job;
    }

}
