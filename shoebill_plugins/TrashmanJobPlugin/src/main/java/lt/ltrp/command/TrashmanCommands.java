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
            player.sendErrorMessage("Ði komanda skirtai tik dirbantiems ðiukðliø iðveþëjais.");
            return false;
        } else {
            return true;
        }
    }

    @Command
    public boolean trashManHelp(Player pp) {
        LtrpPlayer p = LtrpPlayer.get(pp);
        p.sendMessage("/startmission [Rajono pavadinimas] - pradeda ðiukðliø surinkimo darbà");
        p.sendMessage("/endmission Atðaukia pradëtà ðiukliø rinkimo reisà");
        return true;
    }

    @Command
    @CommandHelp("Pradeda pasirinktà ðiukðliø rinkimo reisà")
    public boolean startMission(Player pp, @CommandParameter(name = "Rajono pavadinimas")String locationName) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        PlayerTrashMission trashMission = trashPlugin.getPlayerTrashMission(player);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null || vehicle.getModelId() != VehicleModel.TRASHMASTER)
            player.sendErrorMessage("Ðià komandà galite naudoti tik bûdami ðiukðliaveþyje.");
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
                player.sendErrorMessage("Jûs jau esate pradëjæs reisà " + trashMission.getMission().getName() + " rajone. Norëdami já atðaukti naudokite /endmission");
        }
        else {
            // We begin validating the mission name
            TrashMissions missions = trashPlugin.getMissions();
            if(!missions.contains(locationName))
                player.sendErrorMessage("Tokio rajono nëra. Galimi rajonai: " + missions.get().stream().map(TrashMission::getName).collect(Collectors.joining(", ")));
            else {
                trashPlugin.startMission(player, new PlayerTrashMission(missions.getByName(locationName), player, vehicle));
                player.sendMessage(Color.NEWS, "Ðiûðkleveþio misija sëkmingai pradëta.");
                player.sendMessage(Color.NEWS, "MISIJA: Vaþiuokite surinkti ðiûkðliø á pasirinktà rajonà, kuris nustatytas Jûsø þemëlapyje.");
                //player.sendMessage(Color.NEWS, "KOMANDOS: /takegarbage - paiimti ðiûðkðlëms. /throwgarbage - iðmesti ðiûkðlëms á sunkeþimá.");
            }
        }
        return true;
    }


    @Command
    @CommandHelp("Atðaukia pradëtà ðiukliø rinkimo reisà")
    public boolean endMission(Player pp) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        PlayerTrashMission mission = trashPlugin.getPlayerTrashMission(player);
        if(mission == null)
            player.sendErrorMessage("Jûs neesate pradëjæs reiso!");
        else {
            trashPlugin.endPlayerMission(mission);
            player.sendMessage(Color.LIGHTRED, "Sëkmingai nutraukëtæ misijà.");
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
                    player.sendMessage(Color.NEWS, "Sëkmingai paiimëte maiðà su ðiûkðlëmis, dabar prieikite prie sunkveþimio galo ir ámeskite su komanda: /throwgarbage");
                } else
                    player.sendErrorMessage("Jûs jau laikote ðiukðliø maiðà!");
            } else
                player.sendErrorMessage("Prie jûsø nëra ðiukðliø maiðo!");
        } else
            player.sendErrorMessage("Jûs neesate pradëjæs ðiukðliø surinkimoe reiso, tai galima padaryti su /startmission");
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
                        player.sendErrorMessage("Ðis ðiukðliaveþis jau pilnas, veþkite ðiukðles á savartynà!");
                } else
                    player.sendErrorMessage("Prie jûsø nëra ðiukðliaveþio");
            } else
                player.sendErrorMessage("Jûsø rankose nëra ðiukðliø maiðo!");
        } else
            player.sendErrorMessage("Jûs neesate pradëjæs ðiukðliø surinkimoe reiso, tai galima padaryti su /startmission");
        return false;
    }
*/

}
