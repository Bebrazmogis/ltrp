package lt.ltrp.command;

import lt.ltrp.JobController;
import lt.ltrp.VehicleController;
import lt.ltrp.data.Color;
import lt.ltrp.data.NamedLocation;
import lt.ltrp.data.PlayerJobData;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PlayerVehicle;
import lt.ltrp.object.VehicleThiefJob;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.18.
 */
public class VehicleThiefCommands extends Commands {

    private VehicleThiefJob job;
    private Map<LtrpPlayer, Timer> playerVehicleSellDelay;

    public VehicleThiefCommands(VehicleThiefJob job) {
        this.job = job;
        this.playerVehicleSellDelay = new HashMap<>();
    }

    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = JobController.get().getJobData(player);
        if(jobData.getJob().equals(job)) {
            return true;
        } else
            player.sendErrorMessage("�i komanda skirta tik dirbantiems automobili� vagimis");
        return false;
    }


    @Command
    public boolean sellCar(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        PlayerJobData jobData = JobController.get().getJobData(player);
        if(!playerVehicleSellDelay.containsKey(player)) {
            if(player.getVehicle() != null) {
                PlayerVehicle vehicle = PlayerVehicle.getByVehicle(player.getVehicle());
                if (vehicle != null) {
                    for (NamedLocation location : job.getVehicleBuyPoints().keySet()) {
                        if (player.getLocation().distance(location) < 10.0f) {
                            if (vehicle.getModelId() == job.getVehicleBuyPoints().get(location)) {
                                int money = 1000 - (int) vehicle.getHealth();
                                player.sendMessage(String.format("SMS: Nustebinai mane, atgabenai �� %s, u� �� darbel� atsilyginsiu Tau %d$. Siunt�jas: Nenustatytas numeris", vehicle.getModelName(), money));
                                player.giveMoney(money);
                                VehicleController.get().getDao().update(vehicle);
                                vehicle.destroy();
                                jobData.addXp(1);
                                job.log("�aid�jas " + player.getUUID() + "(darbas:" + jobData.getJob().getUUID() + ") pardav� transporto priemon� " + vehicle.getModelName() + " kuri priklauso �aid�jui " + vehicle.getOwnerId());
                                return true;
                            } else
                                player.sendErrorMessage(vehicle.getModelName() + " man nereikia. Va�iuok i� �ia.");
                        }
                    }
                } else
                    player.sendErrorMessage("J�s neesate transporto priemon�je!");
            }
        } else
            player.sendErrorMessage("Klaida, j�s neseniai pridav�te automobil�. Pra�ome palaukti.");
        return false;
    }

    @Command
    public boolean info(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        for (NamedLocation location : job.getVehicleBuyPoints().keySet()) {
            if (player.getLocation().distance(location) < 10.0f) {
                player.sendMessage(Color.DARKGOLDENROD, String.format("SMS: Gird�jau ie�kai darbelio, o a� ie�kausi %s, pasistengsiu gerai atsilyginti jei tik tinka ir nebijai rizikuoti -Ne�inomas siunt�jas.", VehicleModel.getName(job.getVehicleBuyPoints().get(location))));
                return true;
            }
        }
        return false;
    }


    @Command
    public boolean spots(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        String list = "";
        for (NamedLocation location : job.getVehicleBuyPoints().keySet()) {
            list += location.getName() + ",";
        }
        player.sendMessage(Color.DARKGOLDENROD, "SMS: Tiesiog atve�k automobilius � �iuos gara�us: " + list + " ir baigta. -Ne�inomas siunt�jas");
        return false;
    }


}
