package lt.ltrp.vehiclethief;


import lt.ltrp.AbstractJobManager;
import lt.ltrp.JobController;
import lt.ltrp.LoadingException;
import lt.ltrp.constant.ItemType;
import lt.ltrp.data.JobData;
import lt.ltrp.data.VehicleLock;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.vehicle.VehicleEngineStartEvent;
import lt.ltrp.object.*;
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
        this.job = JobController.get().getDao().getVehicleThiefJob(jobid);

        this.eventManagerNode.registerHandler(PaydayEvent.class, e -> {
            job.resetRequiredModels();
        });

        this.eventManagerNode.registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            JobData jobData = JobController.get().getJobData(player);
            if(player != null && player.getKeyState().isKeyPressed(PlayerKey.FIRE) && jobData.getJob() != null && jobData.getJob().equals(job) && player.getVehicle() != null) {
                final PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
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
    public VehicleThiefJob getJob() {
        return job;
    }

}
