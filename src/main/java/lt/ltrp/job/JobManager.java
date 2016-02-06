package lt.ltrp.job;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.command.PlayerCommandManager;
import lt.ltrp.data.Color;
import lt.ltrp.job.policeman.PolicemanManager;
import lt.ltrp.job.trashman.TrashmanManager;
import lt.ltrp.job.vehiclethief.VehicleThiefJob;
import lt.ltrp.job.vehiclethief.VehicleThiefManager;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.plugin.streamer.DynamicLabel;
import lt.ltrp.plugin.streamer.DynamicSampObject;
import lt.ltrp.vehicle.JobVehicle;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.event.server.GameModeExitEvent;
import net.gtaun.shoebill.event.server.GameModeInitEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class JobManager {

    private static final Logger logger = LoggerFactory.getLogger(JobManager.class);
    private static List<Faction> factions;
    private static List<ContractJob> contractJobs;
    private static final JobManager instance = new JobManager();

    public static List<Job> get() {
        List<Job> jobs = new ArrayList<>();
        jobs.addAll(factions);
        jobs.addAll(contractJobs);
        return jobs;
    }

    public static List<ContractJob> getContractJobs() {
        return contractJobs;
    }

    public static ContractJob getContractJob(int id) {
        for(ContractJob job : getContractJobs()) {
            if(job.getId() == id) {
                return job;
            }
        }
        return null;
    }

    public static List<Faction> getFactions() {
        return factions;
    }

    public static Faction getFaction(int id) {
        for(Faction job : getFactions()) {
            if(job.getId() == id) {
                return job;
            }
        }
        return null;
    }

    public static Job get(int id) {
        for(Job job : get()) {
            if(job.getId() == id) {
                return job;
            }
        }
        return null;
    }

    public static JobManager getInstance() {
        return instance;
    }

    private EventManager eventManager = LtrpGamemode.get().getEventManager().createChildNode();


    private JobManager() {
        factions = LtrpGamemode.getDao().getJobDao().getFactions();
        contractJobs = LtrpGamemode.getDao().getJobDao().getContractJobs();
        logger.info("Loaded " + factions.size() + " factions and " + contractJobs.size() + " contract jobs");

        TrashmanManager manager = TrashmanManager.getInstance();
        VehicleThiefManager vehicleThiefManager = VehicleThiefManager.getInstance();
        PolicemanManager policemanManager = PolicemanManager.getInstance();

        eventManager.registerHandler(GameModeExitEvent.class, e -> {

        });


        PlayerCommandManager playerCommandManager = new PlayerCommandManager(HandlerPriority.NORMAL, eventManager);
        playerCommandManager.registerCommand("jobtest", new Class[]{LtrpPlayer.class}, new String[]{""}, (player, params) -> {
            player.sendMessage(Color.SILVER, "Darbø skaièius: " + get().size());
            for(Job job : get()) {
                player.sendMessage(Color.CADETBLUE, String.format("[%d]%s. Class:%s Rank count: %d Vehicle count: %d",
                        job.getId(), job.getName(), job.getClass(), job.getRanks().size(), job.getVehicles().size()));
            }
            return true;
        });




        logger.info("Job manager initialized");
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
