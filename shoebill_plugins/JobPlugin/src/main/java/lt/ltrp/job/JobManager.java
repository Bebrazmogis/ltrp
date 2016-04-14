package lt.ltrp.job;

import lt.ltrp.LoadingException;
import lt.ltrp.LtrpGamemodeImpl;
import lt.ltrp.constant.WorldZone;
import lt.ltrp.dao.JobDao;
import lt.ltrp.data.Color;
import lt.ltrp.item.ItemPhone;
import lt.ltrp.item.phone.event.PlayerCallNumberEvent;
import lt.ltrp.job.drugdealer.DrugDealerManager;
import lt.ltrp.job.mechanic.MechanicManager;
import lt.ltrp.job.medic.MedicManager;
import lt.ltrp.job.policeman.PolicemanManager;
import lt.ltrp.job.trashman.TrashmanManager;
import lt.ltrp.job.vehiclethief.VehicleThiefManager;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.StringUtils;
import lt.ltrp.vehicle.VehicleManager;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

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
    private MedicManager medicManager;
    private JobDao jobDao;
    private Map<LtrpPlayer, Pair<ItemPhone, Integer>> playerEmergencyCalls;


    public JobManager(EventManager eventManager, JobDao jobDao, VehicleManager vehicleManager) throws LoadingException{
        this.jobDao = jobDao;
        this.playerEmergencyCalls = new HashMap<>();

        PlayerCommandManager.replaceTypeParser(ContractJob.class, s -> {
            int id = ContractJob.INVALID_ID;
            try {
                id = Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return null;
            }
            return JobManager.getContractJob(id);
        });

        PlayerCommandManager.replaceTypeParser(Faction.class, s -> {
            int id = Faction.INVALID_ID;
            try {
                id = Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return null;
            }
            return JobManager.getFaction(id);
        });

        trashmanManager = new TrashmanManager(eventManager, JobId.TrashMan.id);
        vehicleThiefManager = new VehicleThiefManager(eventManager, JobId.VehicleThief.id);
        policemanManager = new PolicemanManager(eventManager, JobId.Officer.id, vehicleManager);
        mechanicManager = new MechanicManager(eventManager, JobId.Mechanic.id);
        drugDealerManager = new DrugDealerManager(eventManager, JobId.DrugDealer.id);
        medicManager = new MedicManager(eventManager, JobId.Medic.id);

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
                    LtrpPlayer.getPlayerDao().update(player);
                }
            }
            return true;
        }, null, null, null, null);

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
        }, null, null, null, null);




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
        }, null, null, null, null);

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
        }, null, null, null, null);


        eventManager.registerHandler(PlayerCallNumberEvent.class, e -> {
            if(e.getPhoneNumber() == 911) {
                playerEmergencyCalls.put(e.getPlayer(), new MutablePair<>(e.getCallerPhone(), 0));
                e.getPlayer().setSpecialAction(SpecialAction.USE_CELLPHONE);
                e.getPlayer().sendMessage(Color.WHITE, "INFORMACIJA: Naudokite T, kad kalbëtumëte á telefonà. (/h)angup kad padëtumëte rageli.");
                e.getPlayer().sendMessage(Color.LIGHTGREY, "PAGALBOS LINIJA: Su kuo jus sujungti? Policija, medikais? Ar su abu?");
                e.getCallerPhone().setBusy(true);
            }
        });

        eventManager.registerHandler(PlayerTextEvent.class, HandlerPriority.HIGH, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            String text = e.getText();
            if(playerEmergencyCalls.containsKey(player)) {
                WorldZone zone = WorldZone.get(player.getLocation());
                int phonenumber = playerEmergencyCalls.get(player).getLeft().getPhonenumber();
                switch(playerEmergencyCalls.get(player).getRight()) {
                    case 0:
                        if(StringUtils.equalsIgnoreLtCharsAndCase(text, "medikais")) {
                            player.sendMessage(Color.WHITE, "LOS SANTOS pagalbos linija: tuojaus sujungsime Jus su policijos departamentu.");
                            player.sendMessage(Color.LIGHTGREY, "LOS SANTOS POLICIJOS DEPARTAMENTAS: Los Santos policija klauso, koks Jûsø praneðimas ir vieta?");
                            playerEmergencyCalls.get(player).setValue(1);
                        } else if(StringUtils.equalsIgnoreLtCharsAndCase(text, "policija")) {
                            player.sendMessage(Color.WHITE, "LOS SANTOS pagalbos linija: tuojaus sujungsime Jus su ligonine ar kita medicinos ástaiga.");
                            player.sendMessage(Color.LIGHTGREY, "LOS SANTOS ligoninë: Los Santos ligoninë klauso, apibûdinkite kas nutiko ir kur nutiko.");
                            playerEmergencyCalls.get(player).setValue(2);
                        } else if(StringUtils.equalsIgnoreLtCharsAndCase(text, "abu")) {
                            player.sendMessage(Color.WHITE, "LOS SANTOS pagalbos linija: tuojaus sujungsime su bendros pagalbos centru.");
                            player.sendMessage(Color.LIGHTGREY, "LOS SANTOS bendra pagalbos linija: apibûdinkite savo ávyki, bei ávykio vietà.");
                            playerEmergencyCalls.get(player).setValue(3);
                        } else {
                            player.sendMessage(Color.WHITE, "LOS SANTOS pagalbos linija: Atleiskite, bet að nesuprantu su kuo Jûs reikia sujungti: Policija ar medikais?");
                        }
                        break;
                    case 1:
                        player.sendMessage(Color.WHITE, "LOS SANTOS LIGONINË: Jûsø praneðimas uþfiksuotas ir praneðtas mûsø medikams.");
                        player.sendMessage(Color.LIGHTGREY, "Aèiø Jums, kad praneðëte apie insidentà, pasistengsime Jums padëti, medikai jau atvyksta");
                        medicManager.getJob().sendMessage(Color.WHITE, "|________________Gautas praneðimas apie ávyki________________|");
                        medicManager.getJob().sendMessage(Color.WHITE, "| ávyki praneðë | " + phonenumber + ", nustatyta vieta: " + zone.getName());
                        medicManager.getJob().sendMessage(Color.WHITE, "| ávykio praneðimas | " + text);
                        player.setSpecialAction(SpecialAction.NONE);
                        playerEmergencyCalls.remove(player);
                        break;
                    case 2:
                        player.sendMessage(Color.WHITE, "LOS SANTOS POLICIJOS DEPARTAMENTAS: Jûsø praneðimas uþfiksuotas ir praneðtas pareigûnams.");
                        player.sendMessage(Color.LIGHTGREY, "Aèiø Jums, kad praneðëte apie incidentà, pasistengsime Jums padëti.");
                        policemanManager.getJob().sendMessage(Color.POLICE, "|________________Gautas praneðimas apie ávyki________________|");
                        policemanManager.getJob().sendMessage(Color.POLICE, "| ávykis: " + text);
                        player.setSpecialAction(SpecialAction.NONE);
                        playerEmergencyCalls.remove(player);
                        break;
                    case 3:
                        player.sendMessage(Color.WHITE, "LOS SANTOS pagalbos linija Jûsø ávykis buvo praneðtas visiems departamentams.");
                        player.sendMessage(Color.LIGHTGREY, "Aèiø Jums, kad praneðëte apie insidentà, pasistengsime Jums padëti.");
                        medicManager.getJob().sendMessage(Color.WHITE, "|________________Gautas praneðimas apie ávyki________________|");
                        policemanManager.getJob().sendMessage(Color.POLICE, "|________________Gautas praneðimas apie ávyki________________|");
                        String s = String.format("| ávyki praneðë | %d, nustatyta vieta: %s", phonenumber, zone.getName());
                        medicManager.getJob().sendMessage(Color.WHITE, s);
                        policemanManager.getJob().sendMessage(Color.POLICE, s);
                        s = String.format("| ávykio praneðimas | %s", text);
                        medicManager.getJob().sendMessage(Color.WHITE, s);
                        policemanManager.getJob().sendMessage(Color.POLICE, s);
                        player.setSpecialAction(SpecialAction.NONE);
                        playerEmergencyCalls.remove(player);
                        break;
                }
            }
        });

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
        return isJobLeader(player.getUUID());
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
        medicManager.destroy();
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
