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
            player.sendErrorMessage("Ði komanda skirta tik dirbantiems automobiliø vagimis");
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
                                player.sendMessage(String.format("SMS: Nustebinai mane, atgabenai ðá %s, uþ ðá darbelá atsilyginsiu Tau %d$. Siuntëjas: Nenustatytas numeris", vehicle.getModelName(), money));
                                player.giveMoney(money);
                                VehicleController.get().getDao().update(vehicle);
                                vehicle.destroy();
                                jobData.addXp(1);
                                job.log("Þaidëjas " + player.getUUID() + "(darbas:" + jobData.getJob().getUUID() + ") pardavë transporto priemonæ " + vehicle.getModelName() + " kuri priklauso þaidëjui " + vehicle.getOwnerId());
                                return true;
                            } else
                                player.sendErrorMessage(vehicle.getModelName() + " man nereikia. Vaþiuok ið èia.");
                        }
                    }
                } else
                    player.sendErrorMessage("Jûs neesate transporto priemonëje!");
            }
        } else
            player.sendErrorMessage("Klaida, jûs neseniai pridavëte automobilá. Praðome palaukti.");
        return false;
    }

    @Command
    public boolean info(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        for (NamedLocation location : job.getVehicleBuyPoints().keySet()) {
            if (player.getLocation().distance(location) < 10.0f) {
                player.sendMessage(Color.DARKGOLDENROD, String.format("SMS: Girdëjau ieðkai darbelio, o að ieðkausi %s, pasistengsiu gerai atsilyginti jei tik tinka ir nebijai rizikuoti -Neþinomas siuntëjas.", VehicleModel.getName(job.getVehicleBuyPoints().get(location))));
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
        player.sendMessage(Color.DARKGOLDENROD, "SMS: Tiesiog atveðk automobilius á ðiuos garaþus: " + list + " ir baigta. -Neþinomas siuntëjas");
        return false;
    }


}
