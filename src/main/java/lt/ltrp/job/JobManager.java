package lt.ltrp.job;

import lt.ltrp.LoadingException;
import lt.ltrp.command.PlayerCommandManager;
import lt.ltrp.data.Color;
import lt.ltrp.job.drugdealer.DrugDealerManager;
import lt.ltrp.job.mechanic.MechanicManager;
import lt.ltrp.job.policeman.PolicemanManager;
import lt.ltrp.job.trashman.TrashmanManager;
import lt.ltrp.job.vehiclethief.VehicleThiefManager;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class JobManager implements Destroyable {

    private static final Logger logger = LoggerFactory.getLogger(JobManager.class);
    private static List<Faction> factions = new ArrayList<>();
    private static List<ContractJob> contractJobs = new ArrayList<>();

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

   // private EventManager eventManager = LtrpGamemode.get().getEventManager().createChildNode();
    private boolean destroyed;
    private TrashmanManager trashmanManager;
    private VehicleThiefManager vehicleThiefManager;
    private PolicemanManager policemanManager;
    private MechanicManager mechanicManager;
    private DrugDealerManager drugDealerManager;


    public JobManager(EventManager eventManager) throws LoadingException{

        trashmanManager = new TrashmanManager(eventManager, JobId.TrashMan.id);
        vehicleThiefManager = new VehicleThiefManager(eventManager, JobId.VehicleThief.id);
        policemanManager = new PolicemanManager(eventManager, JobId.Officer.id);
        mechanicManager = new MechanicManager(eventManager, JobId.Mechanic.id);
        drugDealerManager = new DrugDealerManager(eventManager, JobId.DrugDealer.id);

        // The code from hell

        contractJobs.add((ContractJob)trashmanManager.getJob());
        contractJobs.add((ContractJob)vehicleThiefManager.getJob());
        contractJobs.add((ContractJob)mechanicManager.getJob());
        contractJobs.add((ContractJob)drugDealerManager.getJob());

        PlayerCommandManager playerCommandManager = new PlayerCommandManager(HandlerPriority.NORMAL, eventManager);
        playerCommandManager.registerCommand("jobtest", new Class[]{}, new String[]{""}, (player, params) -> {
            player.sendMessage(Color.SILVER, "Darbø skaièius: " + get().size());
            for(Job job : get()) {
                player.sendMessage(Color.CADETBLUE, String.format("[%d]%s. Class:%s Rank count: %d Vehicle count: %d",
                        job.getId(),
                        job.getName(),
                        job.getClass().getSimpleName(),
                        job.getRanks() == null ? -1 : job.getRanks().size(),
                        job.getVehicles() == null ? -1 : job.getVehicles().size()));
                int propertyCount = 0;
                String propString = "";
                for(Field f : job.getClass().getFields()) {
                    if(f.isAnnotationPresent(JobProperty.class)) {
                        propertyCount++;
                        try {
                            propString += f.getName() + ":" + f.get(job);
                        } catch(IllegalAccessException e) {
                            propString += f.getName() + " unaccessible";
                        }
                    }
                }
                if(propertyCount > 0)
                    player.sendMessage(Color.ROYALBLUE, String.format("Properties: %d. %s", propertyCount, propString));
            }
            return true;
        });


        playerCommandManager.registerCommand("givemejob", new Class[]{Integer.class}, new String[]{"Darbo ID"}, (p, params) -> {
            for(Object o : params) {
                System.out.println(o);
                p.sendMessage(Color.LIGHTYELLOW, o.toString());
            }
            if(params.length == 0) {
                playerCommandManager.setUsageMessage("givemejob", "TAIP");
                p.sendErrorMessage("NO SHIT");
            } else {
                int jobid = (Integer)params[1];
                Job job = Job.get(jobid);
                if(job == null) {
                    p.sendErrorMessage("YO, nëra toki odarbo");
                } else {
                    p.setJob(job);
                    if(job.getRanks() != null)
                        p.setJobRank(job.getRank(1));
                    p.sendMessage(Color.CRIMSON, "Tavo naujas darbas :" + job.getName() + " id: "+ job.getId());
                }
                return true;
            }
            return false;
        });
        playerCommandManager.setUsageMessage("givemejob", "TAIP");

        playerCommandManager.registerCommand("givemerank", new Class[]{Integer.class}, new String[]{"Rank ID"}, (p, params) -> {
            if(p.getJob() == null) {
                p.sendErrorMessage("you got no job son");
            } else if(params.length < 1) {
                p.sendErrorMessage("learn to use kiddo");
            } else {
                int rankid = (Integer)params[1];
                Rank rank = p.getJob().getRank(rankid);
                if(rank == null) {
                    p.sendErrorMessage("No such rank.");
                } else {
                    p.setJobRank(rank);
                    p.sendMessage(Color.LIGHTGREEN, "gz kid, new rank: "+ rank.getName() + " Numb: "+ rank.getNumber());
                    return true;
                }
            }
            return false;
        });




        logger.info("Job manager initialized");
    }

    @Override
    public void destroy() {
        destroyed = true;
        trashmanManager.destroy();
        vehicleThiefManager.destroy();
        policemanManager.destroy();
        mechanicManager.destroy();
        drugDealerManager.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }


    private enum JobId {
        DrugDealer(4),
        Officer(2),
        TrashMan(3),
        VehicleThief(7),
        Mechanic(1);

        int id;


        JobId(int id) {
            this.id = id;
        }
    }

}
