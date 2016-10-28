package lt.ltrp;

import kotlin.reflect.jvm.internal.KClassImpl;
import lt.ltrp.command.TestCommands;
import lt.ltrp.constant.WorldZone;
import lt.ltrp.dao.impl.MySqlFactionLeaderDaoImpl;
import lt.ltrp.dao.impl.MySqlJobDaoImpl;
import lt.ltrp.data.Color;
import lt.ltrp.event.PaydayEvent;
import lt.ltrp.event.player.PlayerCallNumberEvent;
import lt.ltrp.job.JobController;
import lt.ltrp.job.dao.FactionLeaderDao;
import lt.ltrp.job.dao.JobDao;
import lt.ltrp.job.object.ContractJob;
import lt.ltrp.job.object.ContractJobRank;
import lt.ltrp.job.object.Job;
import lt.ltrp.object.ItemPhone;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.player.job.PlayerJobController;
import lt.ltrp.player.job.data.PlayerJobData;
import lt.ltrp.resource.DependentPlugin;
import lt.ltrp.util.StringUtils;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.resource.ResourceManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class JobPlugin extends DependentPlugin {

    private static final Logger logger = LoggerFactory.getLogger(JobPlugin.class);


   // private EventManager eventManager = LtrpGamemode.get().getEventManager().createChildNode();
    private EventManagerNode eventManager;
    private JobDao jobDao;
    private Map<LtrpPlayer, Pair<ItemPhone, Integer>> playerEmergencyCalls;
    private JobController jobController;
    private FactionLeaderDao leaderDao;

    public JobPlugin() {
        super.addDependency(new KClassImpl<DatabasePlugin>(DatabasePlugin.class));
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        this.eventManager = getEventManager().createChildNode();
        this.playerEmergencyCalls = new HashMap<>();
        addTypeParsers();
    }

    private void addCommands() {
        PlayerCommandManager playerCommandManager = new PlayerCommandManager(eventManager);
        playerCommandManager.installCommandHandler(HandlerPriority.LOW); // Low priority so specific job modules could override commands
        playerCommandManager.registerCommands(new TestCommands());
    }

    private void addEventHandlers() {
        eventManager.registerHandler(PlayerCallNumberEvent.class, e -> onPlayerCallNumber(e.getPlayer(), e.getCallerPhone(), e.getPhoneNumber()));
        eventManager.registerHandler(PlayerTextEvent.class, HandlerPriority.HIGH, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            onPlayerText(player, e.getText(), e);
        });

        eventManager.registerHandler(PaydayEvent.class, e -> onPayDay());
    }


    private void addTypeParsers() {
        PlayerCommandManager.replaceTypeParser(Job.class, s -> {
            return JobController.Companion.getInstance().get(Integer.parseInt(s));
        });
    }




    @Override
    protected void onDisable() {
        super.onDisable();
    }

    @Override
    public void onDependenciesLoaded() {
        eventManager.cancelAll();
        jobDao = new MySqlJobDaoImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource(), eventManager);
        leaderDao = new MySqlFactionLeaderDaoImpl(ResourceManager.get().getPlugin(DatabasePlugin.class).getDataSource());
        jobController = new JobControllerImpl(jobDao, leaderDao, eventManager);
        addEventHandlers();
        addCommands();
        logger.info(getDescription().getName() + " loaded");
    }

    private void onPlayerCallNumber(LtrpPlayer player, ItemPhone phone, int phoneNumber) {
        if(phoneNumber == 911) {
            playerEmergencyCalls.put(player, new MutablePair<ItemPhone, Integer>(phone, 0));
            player.setSpecialAction(SpecialAction.USE_CELLPHONE);
            player.sendMessage(Color.WHITE, "INFORMACIJA: Naudokite T, kad kalbëtumëte á telefonà. (/h)angup kad padëtumëte rageli.");
            player.sendMessage(Color.LIGHTGREY, "PAGALBOS LINIJA: Su kuo jus sujungti? Policija, medikais? Ar su abu?");
            phone.setBusy(true);
        }
    }

    private void onPlayerText(LtrpPlayer player, String text, PlayerTextEvent event) {
        if(playerEmergencyCalls.containsKey(player)) {
            WorldZone zone = WorldZone.get(player.getLocation());
            int phonenumber = playerEmergencyCalls.get(player).getLeft().getPhonenumber();
            Job medicJob = JobController.Companion.getInstance().get(JobId.Medic.id);
            Job officerJob = JobController.Companion.getInstance().get(JobId.Officer.id);
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
                    medicJob.sendMessage(Color.WHITE, "|________________Gautas praneðimas apie ávyki________________|");
                    medicJob.sendMessage(Color.WHITE, "| ávyki praneðë | " + phonenumber + ", nustatyta vieta: " + zone.getName());
                    medicJob.sendMessage(Color.WHITE, "| ávykio praneðimas | " + text);
                    player.setSpecialAction(SpecialAction.NONE);
                    playerEmergencyCalls.remove(player);
                    break;
                case 2:
                    player.sendMessage(Color.WHITE, "LOS SANTOS POLICIJOS DEPARTAMENTAS: Jûsø praneðimas uþfiksuotas ir praneðtas pareigûnams.");
                    player.sendMessage(Color.LIGHTGREY, "Aèiø Jums, kad praneðëte apie incidentà, pasistengsime Jums padëti.");
                    officerJob.sendMessage(Color.POLICE, "|________________Gautas praneðimas apie ávyki________________|");
                    officerJob.sendMessage(Color.POLICE, "| ávykis: " + text);
                    player.setSpecialAction(SpecialAction.NONE);
                    playerEmergencyCalls.remove(player);
                    break;
                case 3:
                    player.sendMessage(Color.WHITE, "LOS SANTOS pagalbos linija Jûsø ávykis buvo praneðtas visiems departamentams.");
                    player.sendMessage(Color.LIGHTGREY, "Aèiø Jums, kad praneðëte apie insidentà, pasistengsime Jums padëti.");
                    medicJob.sendMessage(Color.WHITE, "|________________Gautas praneðimas apie ávyki________________|");
                    officerJob.sendMessage(Color.POLICE, "|________________Gautas praneðimas apie ávyki________________|");
                    String s = String.format("| ávyki praneðë | %d, nustatyta vieta: %s", phonenumber, zone.getName());
                    medicJob.sendMessage(Color.WHITE, s);
                    officerJob.sendMessage(Color.POLICE, s);
                    s = String.format("| ávykio praneðimas | %s", text);
                    medicJob.sendMessage(Color.WHITE, s);
                    officerJob.sendMessage(Color.POLICE, s);
                    player.setSpecialAction(SpecialAction.NONE);
                    playerEmergencyCalls.remove(player);
                    break;
            }
        }
        event.interrupt();
    }

    private void onPayDay() {
        LtrpPlayer.get().forEach(p -> {
            PlayerJobData jobData = p.getJobData();
            if(jobData != null) {
                jobData.setHours(jobData.getHours());
                if(jobData.getJob() instanceof ContractJob) {
                    if(jobData.getRemainingContract() > 0)
                        jobData.setRemainingContract(jobData.getRemainingContract() - 1);
                }
                if(jobData.getRank() instanceof ContractJobRank) {
                    ContractJobRank nextRank = (ContractJobRank) jobData.getJob().getRankByNumber(jobData.getRank().getNumber() + 1);
                    // If the user doesn't have the highest rank yet
                    if (nextRank != null) {
                        if (nextRank.getXpNeeded() <= jobData.getXp()) {
                            PlayerJobController.Companion.getInstance().setRank(p, nextRank);
                            p.sendGameText(1, 1000, "Naujas rangas: " + StringUtils.replaceLtChars(nextRank.getName()));
                        }
                    }
                }
            }
        });
    }


    public enum JobId {
        DrugDealer(4),
        Officer(2),
        TrashMan(3),
        Medic(5),
        VehicleThief(7),
        Mechanic(1),
        Government(12),
        Trucker(8);

        public int id;


        JobId(int id) {
            this.id = id;
        }
    }

}
