package lt.ltrp.command;

import lt.ltrp.JobController;
import lt.ltrp.TrashmanJobPlugin;
import lt.ltrp.data.Color;
import lt.ltrp.data.JobData;
import lt.ltrp.data.TrashMission;
import lt.ltrp.data.TrashMissions;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.object.impl.PlayerTrashMission;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;

import java.util.stream.Collectors;

/**
 * @author Bebras
 *         2015.12.17.
 */
public class TrashmanCommands extends Commands {


    private TrashmanJobPlugin trashPlugin;

    public TrashmanCommands() {
        this.trashPlugin = TrashmanJobPlugin.get(TrashmanJobPlugin.class);
    }


    @BeforeCheck
    public boolean beforeCheck(LtrpPlayer player, String cmd, String params) {
        JobData jobData = JobController.get().getJobData(player);
        if(!jobData.getJob().equals(trashPlugin.getJob())) {
            player.sendErrorMessage("�i komanda skirtai tik dirbantiems �iuk�li� i�ve��jais.");
            return false;
        } else {
            return true;
        }
    }

    @Command
    public boolean trashManHelp(Player pp) {
        LtrpPlayer p = LtrpPlayer.get(pp);
        p.sendMessage("/startmission [Rajono pavadinimas] - pradeda �iuk�li� surinkimo darb�");
        p.sendMessage("/endmission At�aukia prad�t� �iukli� rinkimo reis�");
        return true;
    }

    @Command
    @CommandHelp("Pradeda pasirinkt� �iuk�li� rinkimo reis�")
    public boolean startMission(Player pp, @CommandParameter(name = "Rajono pavadinimas")String locationName) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        PlayerTrashMission trashMission = trashPlugin.getPlayerTrashMission(player);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null || vehicle.getModelId() != VehicleModel.TRASHMASTER)
            player.sendErrorMessage("�i� komand� galite naudoti tik b�dami �iuk�liave�yje.");
        if(trashMission != null) {
            // If for some reason the checkpoint is now shown anymore we just show it again
            if(player.getCheckpoint() == null || (!player.getCheckpoint().equals(trashMission.getCheckpoint()) && !player.getCheckpoint().equals(trashPlugin.getDropOffCheckpoint()))) {
                if(trashMission.pickedAll())
                    trashPlugin.getDropOffCheckpoint().set(player);
                else
                    player.setCheckpoint(trashMission.getCheckpoint());
            }
            // Otherwise just tell the player he can't start a second mission.
            else
                player.sendErrorMessage("J�s jau esate prad�j�s reis� " + trashMission.getMission().getName() + " rajone. Nor�dami j� at�aukti naudokite /endmission");
        }
        else {
            // We begin validating the mission name
            TrashMissions missions = trashPlugin.getMissions();
            if(!missions.contains(locationName))
                player.sendErrorMessage("Tokio rajono n�ra. Galimi rajonai: " + missions.get().stream().map(TrashMission::getName).collect(Collectors.joining(", ")));
            else {
                trashPlugin.startMission(player, new PlayerTrashMission(missions.getByName(locationName), player, vehicle));
                player.sendMessage(Color.NEWS, "�i��kleve�io misija s�kmingai prad�ta.");
                player.sendMessage(Color.NEWS, "MISIJA: Va�iuokite surinkti �i�k�li� � pasirinkt� rajon�, kuris nustatytas J�s� �em�lapyje.");
                //player.sendMessage(Color.NEWS, "KOMANDOS: /takegarbage - paiimti �i��k�l�ms. /throwgarbage - i�mesti �i�k�l�ms � sunke�im�.");
            }
        }
        return true;
    }


    @Command
    @CommandHelp("At�aukia prad�t� �iukli� rinkimo reis�")
    public boolean endMission(Player pp) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        PlayerTrashMission mission = trashPlugin.getPlayerTrashMission(player);
        if(mission == null)
            player.sendErrorMessage("J�s neesate prad�j�s reiso!");
        else {
            trashPlugin.endPlayerMission(mission);
            player.sendMessage(Color.LIGHTRED, "S�kmingai nutrauk�t� misij�.");
        }
        return true;
    }
/*
    @Command
    public boolean takeGarbage(LtrpPlayer player) {
        TrashmanManager.PlayerTrashMission playerTrashMission = playerTrashMissions.get(player);
        if(playerTrashMission != null) {
            // If for some reason the player has started a mission and isn't shown a checkpoint, lets show it now.
            if(player.getCheckpoint() == null) {
                playerTrashMission.showCheckpoint();
            }
            if(player.getCheckpoint().equals(playerTrashMission.getCheckpoint())) {
                if(!playerTrashMission.isHoldingTrash()) {
                    playerTrashMission.pickupTrash();
                    playerTrashMission.disableCheckpoint();
                    player.sendMessage(Color.NEWS, "S�kmingai paiim�te mai�� su �i�k�l�mis, dabar prieikite prie sunkve�imio galo ir �meskite su komanda: /throwgarbage");
                } else
                    player.sendErrorMessage("J�s jau laikote �iuk�li� mai��!");
            } else
                player.sendErrorMessage("Prie j�s� n�ra �iuk�li� mai�o!");
        } else
            player.sendErrorMessage("J�s neesate prad�j�s �iuk�li� surinkimoe reiso, tai galima padaryti su /startmission");
        return false;
    }

    @Command
    public boolean throwGarbage(LtrpPlayer player) {
        TrashmanManager.PlayerTrashMission playerTrashMission = playerTrashMissions.get(player);
        if(playerTrashMission != null) {
            if(playerTrashMission.isHoldingTrash()) {
                JobVehicle vehicle = JobVehicle.getClosest(player, 6.0f);
                if(vehicle != null && vehicle.getJob().equals(job)) {
                    if(!vehicleTrashCount.containsKey(vehicle)) {
                        vehicleTrashCount.put(vehicle, 0);
                    }
                    if(vehicleTrashCount.get(vehicle) < job.getTrashMasterCapacity()) {
                        playerTrashMission.throwGarbage(200);
                        player.applyAnimation("GRENADE", "WEAPON_THROWU", 4.1f, 0, 0, 0, 0, 0, 0);
                        vehicleTrashCount.put(vehicle, vehicleTrashCount.get(vehicle)+1);
                    } else
                        player.sendErrorMessage("�is �iuk�liave�is jau pilnas, ve�kite �iuk�les � savartyn�!");
                } else
                    player.sendErrorMessage("Prie j�s� n�ra �iuk�liave�io");
            } else
                player.sendErrorMessage("J�s� rankose n�ra �iuk�li� mai�o!");
        } else
            player.sendErrorMessage("J�s neesate prad�j�s �iuk�li� surinkimoe reiso, tai galima padaryti su /startmission");
        return false;
    }
*/

}
