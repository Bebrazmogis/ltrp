package lt.ltrp.job.vehiclethief;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.Commands;
import lt.ltrp.data.Color;
import lt.ltrp.data.NamedLocation;
import lt.ltrp.job.ContractJob;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.LtrpVehicle;
import lt.ltrp.vehicle.PlayerVehicle;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.constant.VehicleModel;
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
    public boolean beforeCheck(LtrpPlayer player, String cmd, String params) {
        if(player.getJob().equals(job)) {
            return true;
        } else
            player.sendErrorMessage("Ði komanda skirta tik dirbantiems automobiliø vagimis");
        return false;
    }


    @Command
    public boolean sellCar(LtrpPlayer player) {
        if(!playerVehicleSellDelay.containsKey(player)) {
            if(player.getVehicle() != null) {
                PlayerVehicle vehicle = PlayerVehicle.getById(player.getVehicle().getId());
                if (vehicle != null) {
                    for (NamedLocation location : job.getVehicleBuyPoints().keySet()) {
                        if (player.getLocation().distance(location) < 10.0f) {
                            if (vehicle.getModelId() == job.getVehicleBuyPoints().get(location)) {
                                int money = 1000 - (int) vehicle.getHealth();
                                player.sendMessage(String.format("SMS: Nustebinai mane, atgabenai ðá %s, uþ ðá darbelá atsilyginsiu Tau %d$. Siuntëjas: Nenustatytas numeris", vehicle.getModelName(), money));
                                player.giveMoney(money);
                                LtrpGamemode.getDao().getVehicleDao().update(vehicle);
                                vehicle.destroy();
                                player.addJobExperience(1);
                                job.log("Þaidëjas " + player.getUserId() + "(darbas:" + job.getId() + ") pardavë transporto priemonæ " + vehicle.getModelName() + " kuri priklauso þaidëjui " + vehicle.getOwnerId());
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
    public boolean info(LtrpPlayer player) {
        for (NamedLocation location : job.getVehicleBuyPoints().keySet()) {
            if (player.getLocation().distance(location) < 10.0f) {
                player.sendMessage(Color.DARKGOLDENROD, String.format("SMS: Girdëjau ieðkai darbelio, o að ieðkausi %s, pasistengsiu gerai atsilyginti jei tik tinka ir nebijai rizikuoti -Neþinomas siuntëjas.", VehicleModel.getName(job.getVehicleBuyPoints().get(location))));
                return true;
            }
        }
        return false;
    }


    @Command
    public boolean spots(LtrpPlayer player) {
        String list = "";
        for (NamedLocation location : job.getVehicleBuyPoints().keySet()) {
            list += location.getName() + ",";
        }
        player.sendMessage(Color.DARKGOLDENROD, "SMS: Tiesiog atveðk automobilius á ðiuos garaþus: " + list + " ir baigta. -Neþinomas siuntëjas");
        return false;
    }


}
