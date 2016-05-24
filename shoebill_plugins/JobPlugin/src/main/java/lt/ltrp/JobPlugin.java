package lt.ltrp;

import lt.ltrp.constant.WorldZone;
import lt.ltrp.dao.JobDao;
import lt.ltrp.dao.JobVehicleDao;
import lt.ltrp.data.Color;
import lt.ltrp.data.JobData;
import lt.ltrp.drugdealer.DrugDealerManager;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.job.PlayerChangeJobEvent;
import lt.ltrp.event.player.PlayerCallNumberEvent;
import lt.ltrp.mechanic.MechanicManager;
import lt.ltrp.medic.MedicManager;
import lt.ltrp.object.*;
import lt.ltrp.policeman.PolicemanManager;
import lt.ltrp.trashman.TrashmanManager;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.event.resource.ResourceEnableEvent;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.Resource;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class JobPlugin extends Plugin implements JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobPlugin.class);
/*
    public static List<Job> get() {
        List<Job> jobs = new ArrayList<>();
        jobs.addAll(factions);
        jobs.addAll(contractJobs);
        return jobs;
    }

    public static List<ContractJobImpl> getContractJobs() {
        return contractJobs;
    }

    public static ContractJobImpl getContractJob(int id) {
        for(ContractJobImpl job : getContractJobs()) {
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
    }*/

   // private EventManager eventManager = LtrpGamemode.get().getEventManager().createChildNode();
    private boolean destroyed;
    private TrashmanManager trashmanManager;
    private PolicemanManager policemanManager;
    private MechanicManager mechanicManager;
    private DrugDealerManager drugDealerManager;
    private MedicManager medicManager;
    private JobDao jobDao;
    private EventManagerNode eventManager;

    private Map<LtrpPlayer, Pair<ItemPhone, Integer>> playerEmergencyCalls;

    @Override
    protected void onEnable() throws Throwable {
        Instance.instance = this;
        this.eventManager = getEventManager().createChildNode();
        this.playerEmergencyCalls = new HashMap<>();
        addTypeParsers();

        final Collection<Class<? extends Plugin>> dependencies = new ArrayBlockingQueue<>(5);
        dependencies.add(DatabasePlugin.class);
        dependencies.add(VehiclePlugin.class);
        int missing = 0;
        for(Class<? extends Plugin> clazz : dependencies) {
            if(ResourceManager.get().getPlugin(clazz) == null)
                missing++;
            else
                dependencies.remove(clazz);
        }
        if(missing > 0) {
            eventManager.registerHandler(ResourceEnableEvent.class, e -> {
                Resource r = e.getResource();
                logger.debug("R:" + r);
                if(r instanceof Plugin && dependencies.contains(r.getClass())) {
                    dependencies.remove(r.getClass());
                    logger.debug("Removing r");
                    if(dependencies.size() == 0)
                        load();
                }
            });
        } else load();

    }
    
    private void load() {
        eventManager.cancelAll();

        /*
        trashmanManager = new TrashmanManager(eventManager, JobId.TrashMan.id);
        policemanManager = new PolicemanManager(eventManager, JobId.Officer.id);
        mechanicManager = new MechanicManager(eventManager, JobId.Mechanic.id);
        drugDealerManager = new DrugDealerManager(eventManager, JobId.DrugDealer.id);
        medicManager = new MedicManager(eventManager, JobId.Medic.id);
         */

        addEventHandlers();
        addCommands();
    }

    private void addCommands() {
        PlayerCommandManager playerCommandManager = new PlayerCommandManager(eventManager);
        playerCommandManager.installCommandHandler(HandlerPriority.NORMAL);
        playerCommandManager.registerCommands(new FactionLeaderCommands(eventManager, this));

        CommandGroup group = new CommandGroup();
        group.registerCommands(new FactionAcceptCommands());
        playerCommandManager.registerChildGroup(group, "accept");

        playerCommandManager.registerCommand("takejob", new Class[0], (p, params) -> {
            LtrpPlayer player = LtrpPlayer.get(p);
            if(getJob(player) != null) {
                player.sendErrorMessage("Jûs jau turite darbà.");
            } else {
                Optional<ContractJob> contractJobOptional = getContractJobs().stream()
                        .filter(j -> j instanceof ContractJob && j.getLocation().distance(player.getLocation()) < 10f).findFirst();
                if(!contractJobOptional.isPresent()){
                    player.sendErrorMessage("Ðià komandà galite naudoti tik prie darbovieèiø.");
                } else {
                    Job job = contractJobOptional.get();
                    setJob(player, job);
                    player.sendMessage(Color.NEWS, "* Jûs ásidarbinote, jeigu reikia daugiau pagalbos raðykite /help.");
                    LtrpPlayer.getPlayerDao().update(player);
                }
            }
            return true;
        }, null, null, null, null);

        playerCommandManager.registerCommand("jobtest", new Class[0], (p, params) -> {
            LtrpPlayer player = LtrpPlayer.get(p);
            player.sendMessage(Color.SILVER, "Darbø skaièius: " + getJobs().size());
            for(Job job : getJobs()) {
                player.sendMessage(Color.CADETBLUE, String.format("[%d]%s. Class:%s Rank count: %d Vehicle count: %d",
                        job.getUUID(),
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
                    setJob(player, job);
                    p.sendMessage(Color.CRIMSON, "Tavo naujas darbas :" + job.getName() + " id: " + job.getUUID());
                }
                return true;
            }
            return false;
        }, null, null, null, null);

        playerCommandManager.registerCommand("givemerank", new Class[]{Integer.class}, (player, params) -> {
            LtrpPlayer p = LtrpPlayer.get(player);
            if(getJob(p) == null) {
                p.sendErrorMessage("you got no job son");
            } else if(params.size() < 1) {
                p.sendErrorMessage("learn to use kiddo");
            } else {
                int rankid = (Integer)params.poll();
                Rank rank = getJob(p).getRank(rankid);
                if(rank == null) {
                    p.sendErrorMessage("No such rank.");
                } else {
                    setRank(p, rank);
                    p.sendMessage(Color.LIGHTGREEN, "gz kid, new rank: "+ rank.getName() + " Numb: "+ rank.getNumber());
                    return true;
                }
            }
            return false;
        }, null, null, null, null);
    }

    private void addEventHandlers() {
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
                        MedicFaction.get().sendMessage(Color.WHITE, "|________________Gautas praneðimas apie ávyki________________|");
                        MedicFaction.get().sendMessage(Color.WHITE, "| ávyki praneðë | " + phonenumber + ", nustatyta vieta: " + zone.getName());
                        MedicFaction.get().sendMessage(Color.WHITE, "| ávykio praneðimas | " + text);
                        player.setSpecialAction(SpecialAction.NONE);
                        playerEmergencyCalls.remove(player);
                        break;
                    case 2:
                        player.sendMessage(Color.WHITE, "LOS SANTOS POLICIJOS DEPARTAMENTAS: Jûsø praneðimas uþfiksuotas ir praneðtas pareigûnams.");
                        player.sendMessage(Color.LIGHTGREY, "Aèiø Jums, kad praneðëte apie incidentà, pasistengsime Jums padëti.");
                        PoliceFaction.get().sendMessage(Color.POLICE, "|________________Gautas praneðimas apie ávyki________________|");
                        PoliceFaction.get().sendMessage(Color.POLICE, "| ávykis: " + text);
                        player.setSpecialAction(SpecialAction.NONE);
                        playerEmergencyCalls.remove(player);
                        break;
                    case 3:
                        player.sendMessage(Color.WHITE, "LOS SANTOS pagalbos linija Jûsø ávykis buvo praneðtas visiems departamentams.");
                        player.sendMessage(Color.LIGHTGREY, "Aèiø Jums, kad praneðëte apie insidentà, pasistengsime Jums padëti.");
                        MedicFaction.get().sendMessage(Color.WHITE, "|________________Gautas praneðimas apie ávyki________________|");
                        PoliceFaction.get().sendMessage(Color.POLICE, "|________________Gautas praneðimas apie ávyki________________|");
                        String s = String.format("| ávyki praneðë | %d, nustatyta vieta: %s", phonenumber, zone.getName());
                        MedicFaction.get().sendMessage(Color.WHITE, s);
                        PoliceFaction.get().sendMessage(Color.POLICE, s);
                        s = String.format("| ávykio praneðimas | %s", text);
                        MedicFaction.get().sendMessage(Color.WHITE, s);
                        PoliceFaction.get().sendMessage(Color.POLICE, s);
                        player.setSpecialAction(SpecialAction.NONE);
                        playerEmergencyCalls.remove(player);
                        break;
                }
            }
        });

        eventManager.registerHandler(PaydayEvent.class, e -> {
            LtrpPlayer.get().forEach(p -> {
                JobData jobData = getJobData(p);
                jobData.addHours(1);
                if(jobData instanceof ContractJob) {
                    if(jobData.getRemainingContract() > 0)
                        jobData.setRemainingContract(jobData.getRemainingContract() - 1);
                    if(jobData.getJobRank() instanceof ContractJobRank) {
                        ContractJobRank nextRank = (ContractJobRank) jobData.getJob().getRank(jobData.getJobRank().getNumber() + 1);
                        // If the user doesn't have the highest rank yet
                        if (nextRank != null) {
                            if (nextRank.getXpNeeded() <= jobData.getXp()) {
                                jobData.setJobRank(nextRank);
                                p.sendGameText(1, 1000, "Naujas rangas: " + StringUtils.replaceLtChars(nextRank.getName()));
                            }
                        }
                    }

                }
            });
        });

        logger.info("Job manager initialized");
    }

    @Override
    public List<Job> getJobs() {
        return AbstractJob.jobList;
    }

    @Override
    public List<ContractJob> getContractJobs() {
        ArrayList<ContractJob> jobs = new ArrayList<>();
        getJobs().stream().filter(j -> j instanceof ContractJob).forEach(j -> jobs.add((ContractJob)j));
        return jobs;
    }

    @Override
    public List<Faction> getFactions() {
        ArrayList<Faction> jobs = new ArrayList<>();
        getJobs().stream().filter(j -> j instanceof Faction).forEach(j -> jobs.add((Faction)j));
        return jobs;
    }


    @Override
    public Job getJob(int id) {
        Optional<Job> op = getJobs().stream().filter(j -> j.getUUID() == id).findFirst();
        return op.isPresent() ? op.get() : null;
    }

    public Faction getFaction(int id) {
        Optional<Faction> op = getFactions().stream().filter(j -> j.getUUID() == id).findFirst();
        return op.isPresent() ? op.get() : null;
    }

    public ContractJob getContractJob(int id) {
        Optional<ContractJob> op = getContractJobs().stream().filter(j -> j.getUUID() == id).findFirst();
        return op.isPresent() ? op.get() : null;
    }


    @Override
    public Job getJob(LtrpPlayer ltrpPlayer) {
        JobData data = getJobData(ltrpPlayer);
        return data != null ? data.getJob() : null;
    }

    @Override
    public Faction getFaction(LtrpPlayer ltrpPlayer) {
        JobData data = getJobData(ltrpPlayer);
        return data != null && data.getJob() instanceof Faction ? (Faction)data.getJob() : null;
    }

    @Override
    public ContractJob getContractJob(LtrpPlayer ltrpPlayer) {
        JobData data = getJobData(ltrpPlayer);
        return data != null && data.getJob() instanceof ContractJob ? (ContractJob)data.getJob() : null;
    }

    @Override
    public void setJob(LtrpPlayer player, Job job) {
        setJob(player, job, job.getRanks().stream().min((r1, r2) -> Integer.compare(r1.getNumber(), r2.getNumber())).get());
    }

    @Override
    public void setJob(LtrpPlayer player, Job job, Rank rank) {
        Job oldJob = getJob(player);
        JobData jobData = new JobData(player, job, rank);
        if(oldJob == null) {
            jobDao.insert(jobData);
        } else if(job == null) {
            jobDao.remove(jobData);
        } else {
            jobDao.update(jobData);
        }
        eventManager.dispatchEvent(new PlayerChangeJobEvent(player, oldJob, job));
    }

    @Override
    public void setRank(LtrpPlayer ltrpPlayer, Rank rank) {
        JobData data = getJobData(ltrpPlayer);
        if(data != null) {
            data.setJobRank(rank);
            jobDao.update(data);
        }
    }

    @Override
    public JobVehicle createVehicle(int id, Job job, int modelId, AngledLocation location, int color1, int color2, Rank requiredRank, String license, float mileage) {
        return JobVehicleImpl.create(id, job, modelId, location, color1, color2, requiredRank, license, mileage);
    }

    @Override
    public JobDao getDao() {
        return jobDao;
    }

    @Override
    public JobVehicleDao getVehicleDao() {
        return null;
    }

    @Override
    public boolean isLeader(LtrpPlayer player) {
        return isLeader(player.getUUID());
    }

    @Override
    public boolean isLeader(int userId) {
        return getFactions().stream().filter(f -> f.getLeaders().contains(userId)).findFirst().isPresent();
    }

    @Override
    public JobData getJobData(LtrpPlayer player) {
        // TODO caching MAY be implemented here, if needed
        return jobDao.get(player);
    }

    private void addTypeParsers() {
        PlayerCommandManager.replaceTypeParser(Job.class, s -> {
            return Job.get(Integer.parseInt(s));
        });
        PlayerCommandManager.replaceTypeParser(Faction.class, s -> getFaction(Integer.parseInt(s)));
        PlayerCommandManager.replaceTypeParser(ContractJob.class, s -> getContractJob(Integer.parseInt(s)));
    }



    @Override
    protected void onDisable() throws Throwable {
        destroyed = true;
        trashmanManager.destroy();
        policemanManager.destroy();
        mechanicManager.destroy();
        drugDealerManager.destroy();
        medicManager.destroy();
    }


    public enum JobId {
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
