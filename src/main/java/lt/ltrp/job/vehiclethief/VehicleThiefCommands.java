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
            player.sendErrorMessage("�i komanda skirta tik dirbantiems automobili� vagimis");
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
                                player.sendMessage(String.format("SMS: Nustebinai mane, atgabenai �� %s, u� �� darbel� atsilyginsiu Tau %d$. Siunt�jas: Nenustatytas numeris", vehicle.getModelName(), money));
                                player.giveMoney(money);
                                LtrpGamemode.getDao().getVehicleDao().update(vehicle);
                                vehicle.destroy();
                                player.addJobExperience(1);
                                job.log("�aid�jas " + player.getUserId() + "(darbas:" + job.getId() + ") pardav� transporto priemon� " + vehicle.getModelName() + " kuri priklauso �aid�jui " + vehicle.getOwnerId());
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
    public boolean info(LtrpPlayer player) {
        for (NamedLocation location : job.getVehicleBuyPoints().keySet()) {
            if (player.getLocation().distance(location) < 10.0f) {
                player.sendMessage(Color.DARKGOLDENROD, String.format("SMS: Gird�jau ie�kai darbelio, o a� ie�kausi %s, pasistengsiu gerai atsilyginti jei tik tinka ir nebijai rizikuoti -Ne�inomas siunt�jas.", VehicleModel.getName(job.getVehicleBuyPoints().get(location))));
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
        player.sendMessage(Color.DARKGOLDENROD, "SMS: Tiesiog atve�k automobilius � �iuos gara�us: " + list + " ir baigta. -Ne�inomas siunt�jas");
        return false;
    }


}
