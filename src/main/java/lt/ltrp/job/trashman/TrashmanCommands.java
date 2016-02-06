package lt.ltrp.job.trashman;

import lt.ltrp.command.Commands;
import lt.ltrp.data.Color;
import lt.ltrp.job.ContractJob;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.JobVehicle;
import lt.ltrp.job.trashman.TrashmanManager;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;

import java.util.Map;

/**
 * @author Bebras
 *         2015.12.17.
 */
public class TrashmanCommands extends Commands {

    private ContractJob job;
    private TrashmanManager trashmanManager;
    private Map<LtrpPlayer, TrashmanManager.PlayerTrashMission> playerTrashMissions;
    private Map<JobVehicle, Integer> vehicleTrashCount;

    public TrashmanCommands(ContractJob job, TrashmanManager manager, Map<LtrpPlayer, TrashmanManager.PlayerTrashMission> pTrashMissions, Map<JobVehicle, Integer> trashCount) {
        this.job = job;
        this.trashmanManager = manager;
        this.playerTrashMissions = pTrashMissions;
        this.vehicleTrashCount = trashCount;
    }


    @BeforeCheck
    public boolean beforeCheck(LtrpPlayer player, String cmd, String params) {
        if(player.getJob().getId() != TrashmanManager.JOB_ID) {
            return false;
        } else {
            return true;
        }
    }

    @Command
    public boolean startmission(LtrpPlayer player, String locationname) {
        if(playerTrashMissions.get(player) == null) {
            TrashMission mission;
            if(locationname != null && !locationname.isEmpty() && (mission = trashmanManager.getTrashMissions().getByName(locationname)) != null) {
                JobVehicle vehicle = JobVehicle.getById(player.getVehicle().getId());
                if(vehicle != null && job.getVehicles().values().contains(vehicle)) {
                    TrashmanManager.PlayerTrashMission playerTrashMission = trashmanManager.new PlayerTrashMission(mission, player);
                    playerTrashMission.showCheckpoint();
                    playerTrashMissions.put(player, playerTrashMission);
                    player.sendMessage(Color.NEWS, "Ðiûðkleveþio misija sëkmingai pradëta.");
                    player.sendMessage(Color.NEWS, "MISIJA: Vaþiuokite surinkti ðiûkðliø á pasirinktà rajonà, kuris nustatytas Jûsø þemëlapyje.");
                    player.sendMessage(Color.NEWS, "KOMANDOS: /takegarbage - paiimti ðiûðkðlëms. /throwgarbage - iðmesti ðiûkðlëms á sunkeþimá.");
                } else
                    player.sendErrorMessage("Jûs neesate darbinëje transporto priemonëje!");
            } else
                player.sendErrorMessage("Tokio rajono nëra!");
        } else
            player.sendErrorMessage("Jûs jau esate pradëjæs reisà. Norëdami já atðaukti naudokite /endmission");
        return false;
    }


    @Command
    public boolean endMission(LtrpPlayer player) {
        if(playerTrashMissions.containsKey(player)) {
            TrashmanManager.PlayerTrashMission playerTrashMission = playerTrashMissions.get(player);
            playerTrashMission.end();
            playerTrashMissions.remove(player);
            player.sendMessage(Color.LIGHTRED, "Sëkmingai nutraukëtæ misijà, bet Jûsø surinktos ðiûkðlës sunkveþime niekur nedings.");
            return true;
        } else
            player.sendErrorMessage("Jûs neesate pradëjæs reiso!");
        return false;
    }

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
                    if(vehicleTrashCount.get(vehicle) < TrashmanManager.TRASHMASTER_CAPACITY) {
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


}
