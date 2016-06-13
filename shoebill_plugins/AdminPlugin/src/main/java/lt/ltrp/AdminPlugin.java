package lt.ltrp;

import lt.ltrp.command.*;
import lt.ltrp.data.Color;
import lt.ltrp.data.PlayerQuestion;
import lt.ltrp.data.PlayerReport;
import lt.ltrp.data.Vote;
import lt.ltrp.event.VoteEndEvent;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.event.player.PlayerToggleAdminDutyEvent;
import lt.ltrp.event.player.PlayerToggleModDutyEvent;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.ltrp.util.AdminLog;
import lt.maze.streamer.object.DynamicLabel;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.command.CommandGroup;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.resource.ResourceLoadEvent;
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author Bebras
 *         2016.05.19.
 */
public class AdminPlugin extends Plugin implements AdminController {

    public static final int REPORT_COMMAND_DELAY_SECONDS = 25;
    public static final int QUESTION_LOG_SIZE = 20;
    public static final int REPORT_CACHE_SIZE = 25;

    private Logger logger;
    private EventManagerNode eventManagerNode;
    private Collection<LtrpPlayer> moderatorsOnDuty;
    private Collection<LtrpPlayer> adminsOnDuty;
    private Map<LtrpPlayer, DynamicLabel> adminDutyLabels;
    private Map<LtrpPlayer, Instant> adminDutyStartInstants;
    private ArrayDeque<PlayerReport> playerReports;
    private ArrayDeque<PlayerQuestion> playerQuestions;
    private Vote currentVote;

    @Override
    protected void onEnable() throws Throwable {
        Instance.instance = this;
        logger = getLogger();

        this.eventManagerNode = getEventManager().createChildNode();
        this.moderatorsOnDuty = new ArrayList<>();
        this.adminsOnDuty = new ArrayList<>();
        this.adminDutyLabels = new HashMap<>();
        this.adminDutyStartInstants = new HashMap<>();
        this.playerReports = new ArrayDeque<>();
        this.playerQuestions = new ArrayDeque<>();

        if(Shoebill.get().getResourceManager().getPlugin(PlayerPlugin.class) != null) {
            load();
        } else {
            eventManagerNode.registerHandler(ResourceLoadEvent.class, e -> {
                if(e.getResource().getClass().equals(PlayerPlugin.class)) {
                    load();
                }
            });
        }
    }

    private void load() {
        PlayerCommandManager commandManager = new PlayerCommandManager(eventManagerNode);
        CommandGroup setStatGroup = new CommandGroup();
        setStatGroup.registerCommands(new SetStatGroupCommands(eventManagerNode));
        commandManager.registerCommands(new AdminCommands(this, eventManagerNode));
        commandManager.registerCommands(new ModeratorCommands(this, eventManagerNode));
        commandManager.registerCommands(new PlayerReportCommands());
        commandManager.registerCommands(new PlayerCommands(this, eventManagerNode));
        commandManager.registerChildGroup(setStatGroup, "setstat");
        commandManager.installCommandHandler(HandlerPriority.NORMAL);
        addEventHandlers();
        logger.debug(getDescription().getName() + " loaded");
    }

    private void addEventHandlers() {
        eventManagerNode.registerHandler(PlayerToggleModDutyEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            if(e.isStatus()) {
                moderatorsOnDuty.add(player);
            } else {
                moderatorsOnDuty.remove(player);
            }
        });

        eventManagerNode.registerHandler(PlayerToggleAdminDutyEvent.class, e -> {
            LtrpPlayer player = e.getPlayer();
            if(e.isOnDuty()) {
                player.setHealth(999f);
                player.setArmour(999f);
                player.setColor(new Color(0x33CC0000));
                Location labelLocation = player.getLocation();
                labelLocation.z += 0.7f;
                adminDutyLabels.put(player, DynamicLabel.create("BUDINTIS\n" + "Administratorius\n" + "Að galiu padëti.",
                        Color.GREEN, labelLocation, 20f, player, true));
                LtrpPlayer.sendAdminMessage(String.format("[ID:%d]%s ásijungë budinèio Administratoriaus statusà.", player.getId(), player.getName()));
                Vector3D scaleVector = new Vector3D(1f, 1f, player.getAdminLevel());
                player.getAttach().getSlotByBone(PlayerAttachBone.NECK).set(PlayerAttachBone.NECK, 19270, new Vector3D(-0.018133f, -0.025358f, 0.0f), new Vector3D(0.0f, 259.281860f, 0.0f), scaleVector, 0, 0);
                adminDutyStartInstants.put(player, Instant.now());
                AdminLog.updateWatchDates(player);
            } else {
                endAdminWatch(player);
            }
        });

        eventManagerNode.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            moderatorsOnDuty.remove(p);
            if(adminsOnDuty.contains(p))
                endAdminWatch(p);
        });

        // Fail-safe.. Actually, it's not. This data is never inserted and I couldn't think of a better way
        eventManagerNode.registerHandler(PlayerDataLoadEvent.class, e -> {
            AdminLog.insertWatchIfNotExists(e.getPlayer());
        });

        eventManagerNode.registerHandler(VehicleDeathEvent.class, e -> {
            LtrpVehicle vehicle = LtrpVehicle.getByVehicle(e.getVehicle());
            LtrpPlayer killer = LtrpPlayer.get(e.getKiller());
            if(vehicle != null && killer != null) {
                LtrpPlayer.sendAdminMessage(String.format("%s[%d] sunaikino transporto priemonæ %s", killer.getName(), killer.getId(), vehicle.getModelName()));
            }
        });

        eventManagerNode.registerHandler(VoteEndEvent.class, e -> {
            Vote vote = e.getVote();
            LtrpPlayer.sendGlobalMessage("Balsavimas baigësi! Klausimas: " + vote.getQuestion());
            LtrpPlayer.sendGlobalMessage(String.format("TAIP balsavo %d(%d%%) þaidëjai, NE balsavo %d(%d%%).",
                    vote.getVoteCount(true),
                    vote.getVoteCount() / 100 * vote.getVoteCount(true),
                    vote.getVoteCount(false),
                    vote.getVoteCount() / 100 * vote.getVoteCount(false)
            ));
        });
    }

    @Override
    protected void onDisable() throws Throwable {
        eventManagerNode.cancelAll();
        moderatorsOnDuty.clear();
        adminsOnDuty.clear();
        adminDutyLabels.values().forEach(Destroyable::destroy);
        adminDutyLabels.clear();
        adminDutyStartInstants.clear();
        playerReports.clear();
        playerReports = null;
        playerQuestions.clear();
        playerQuestions = null;
    }


    private void endAdminWatch(LtrpPlayer player) {
        player.setHealth(100f);
        player.setArmour(0f);
        player.setColor(LtrpPlayer.DEFAULT_PLAYER_COLOR);
        player.getAttach().getSlotByBone(PlayerAttachBone.NECK).remove();
        Instant startInstant = adminDutyStartInstants.get(player);
        Duration dutyDuration = Duration.between(startInstant, Instant.now());
        LtrpPlayer.sendAdminMessage(String.format("[ID:%d]%s iðjungë budinèio Administratoriaus statusà, dirbo %s.", player.getId(), player.getName(), dutyDuration.toString()));

        AdminLog.updateWatchDuration(player, (int)dutyDuration.getSeconds());
        adminDutyLabels.get(player).destroy();
        adminDutyStartInstants.remove(player);
        adminDutyLabels.remove(player);
    }

    public Collection<LtrpPlayer> getAdminsOnDuty() {
        return adminsOnDuty;
    }

    public Collection<LtrpPlayer> getModeratorsOnDuty() {
        return moderatorsOnDuty;
    }


    public void addReport(LtrpPlayer player, LtrpPlayer target, String reason) {
        playerReports.add(new PlayerReport(player, target, reason, Instant.now()));
        if(playerReports.size() > REPORT_CACHE_SIZE) {
            playerReports.removeLast();
        }
    }

    public PlayerReport getPlayerReport(LtrpPlayer player) {
        Optional<PlayerReport> report = playerReports.stream().filter(r -> !r.isAnswered() && r.getPlayer().equals(player)).findFirst();
        return report.isPresent() ? report.get() : null;
    }

    public Collection<PlayerReport> getReports() {
        return playerReports;
    }

    public Instant getPlayerLastReportInstant(LtrpPlayer player) {
        Optional<PlayerReport> optional = playerReports.stream().filter(r -> r.getPlayer().equals(player)).findFirst();
        return optional.isPresent() ? optional.get().getInstant() : null;
    }

    public void addQuestion(LtrpPlayer player, String question) {
        playerQuestions.add(new PlayerQuestion(player, question, Instant.now()));
        if(playerQuestions.size() > QUESTION_LOG_SIZE)
            playerQuestions.removeLast();
    }

    public PlayerQuestion getPlayerQuestion(LtrpPlayer player) {
        Optional<PlayerQuestion> question = playerQuestions.stream().filter(r -> !r.isAnswered() && r.getPlayer().equals(player)).findFirst();
        return question.isPresent() ? question.get() : null;
    }

    public Collection<PlayerQuestion> getQuestions() {
        return playerQuestions;
    }

    public void startVote(LtrpPlayer player, String question) {
        if(currentVote == null) {
            currentVote = new Vote(question, 120, eventManagerNode);
            AdminLog.log(player, "Started a vote:" + question);
            LtrpPlayer.sendGlobalMessage("Administratorius " + player.getName() + " pradëjo balsavimà.");
            LtrpPlayer.sendGlobalMessage("Klausimas:" + question);
            LtrpPlayer.sendGlobalMessage("Naudokite komandas /taip ir /ne balsavimui, jis tæsis 2 minutes!");
        }
    }

    public Vote getCurrentVote() {
        return currentVote;
    }
}
