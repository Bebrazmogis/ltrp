package lt.ltrp.job;

import lt.ltrp.LoadingException;
import lt.ltrp.LtrpGamemode;
import lt.ltrp.dao.JobDao;
import lt.ltrp.data.Color;
import lt.ltrp.job.drugdealer.DrugDealerManager;
import lt.ltrp.job.mechanic.MechanicManager;
import lt.ltrp.job.policeman.PolicemanManager;
import lt.ltrp.job.trashman.TrashmanManager;
import lt.ltrp.job.vehiclethief.VehicleThiefManager;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.vehicle.VehicleManager;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private JobDao jobDao;


    public JobManager(EventManager eventManager, JobDao jobDao, VehicleManager vehicleManager) throws LoadingException{
        this.jobDao = jobDao;

        trashmanManager = new TrashmanManager(eventManager, JobId.TrashMan.id);
        vehicleThiefManager = new VehicleThiefManager(eventManager, JobId.VehicleThief.id);
        policemanManager = new PolicemanManager(eventManager, JobId.Officer.id, vehicleManager);
        mechanicManager = new MechanicManager(eventManager, JobId.Mechanic.id);
        drugDealerManager = new DrugDealerManager(eventManager, JobId.DrugDealer.id);

        // The code from hell

        contractJobs.add((ContractJob)trashmanManager.getJob());
        contractJobs.add((ContractJob)vehicleThiefManager.getJob());
        contractJobs.add((ContractJob)mechanicManager.getJob());
        contractJobs.add((ContractJob)drugDealerManager.getJob());

        PlayerCommandManager playerCommandManager = new PlayerCommandManager(eventManager);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        playerCommandManager.registerCommands(new FactionLeaderCommands(eventManager, this));

        CommandGroup group = new CommandGroup();
        group.registerCommands(new FactionAcceptCommands());
        playerCommandManager.registerChildGroup(group, "accept");

        playerCommandManager.registerCommand("takejob", new Class[0], (p, params) -> {
            LtrpPlayer player = LtrpPlayer.get(p);
            if(player.getJob() != null) {
                player.sendErrorMessage("Jûs jau turite darbà.");
            } else {
                Optional<ContractJob> contractJobOptional = getContractJobs().stream().filter(j -> j.getLocation().distance(player.getLocation()) < 10f).findFirst();
                if(!contractJobOptional.isPresent()){
                    player.sendErrorMessage("Ðià komandà galite naudoti tik prie darbovieèiø.");
                } else {
                    Job job = contractJobOptional.get();
                    player.setJob(job);
                    player.setJobRank(job.getRanks().stream().min((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber())).get());
                    player.sendMessage(Color.NEWS, "* Jûs ásidarbinote, jeigu reikia daugiau pagalbos raðykite /help.");
                    LtrpGamemode.getDao().getPlayerDao().update(player);
                }
            }
            return true;
        }, null, null, null);

        playerCommandManager.registerCommand("jobtest", new Class[0], (p, params) -> {
            LtrpPlayer player = LtrpPlayer.get(p);
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
        }, null, null, null);




        playerCommandManager.registerCommand("givemejob", new Class[]{Integer.class}, (p, params) -> {
            LtrpPlayer player = LtrpPlayer.get(p);
            for(Object o : params) {
                System.out.println(o);
                p.sendMessage(Color.LIGHTYELLOW, o.toString());
            }
            if(params.size() == 0) {
                player.sendErrorMessage("NO SHIT");
            } else {
                int jobid = (Integer)params.poll();
                Job job = Job.get(jobid);
                if(job == null) {
                    player.sendErrorMessage("YO, nëra toki odarbo");
                } else {
                    player.setJob(job);
                    if(job.getRanks() != null)
                        player.setJobRank(job.getRank(1));
                    p.sendMessage(Color.CRIMSON, "Tavo naujas darbas :" + job.getName() + " id: " + job.getId());
                }
                return true;
            }
            return false;
        }, null, null, null);

        playerCommandManager.registerCommand("givemerank", new Class[]{Integer.class}, (player, params) -> {
            LtrpPlayer p = LtrpPlayer.get(player);
            if(p.getJob() == null) {
                p.sendErrorMessage("you got no job son");
            } else if(params.size() < 1) {
                p.sendErrorMessage("learn to use kiddo");
            } else {
                int rankid = (Integer)params.poll();
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
        }, null, null, null);




        logger.info("Job manager initialized");
    }

    public void addFactionLeader(Faction f, int userId) {
        f.addLeader(userId);
        jobDao.addLeader(f, userId);
    }

    public void removeFactionLeader(Faction f, int userId) {
        f.removeLeader(userId);
        jobDao.removeLeader(f, userId);
    }

    public boolean isJobLeader(LtrpPlayer player) {
        return isJobLeader(player.getUserId());
    }

    public boolean isJobLeader(int userId) {
        return factions.stream().filter(f -> f.getLeaders().contains(userId)).findFirst().isPresent();
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
        Medic(5),
        VehicleThief(7),
        Mechanic(1);

        int id;


        JobId(int id) {
            this.id = id;
        }
    }

}
