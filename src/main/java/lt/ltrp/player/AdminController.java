package lt.ltrp.player;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.Util.AdminLog;
import lt.ltrp.data.Color;
import lt.ltrp.event.player.*;
import lt.ltrp.event.player.PlayerDataLoadEvent;
import lt.ltrp.player.event.*;
import lt.maze.streamer.object.DynamicLabel;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bebras
 *         2016.04.10.
 */
public class AdminController implements Destroyable {

    private static AdminController instance;

    private EventManagerNode eventManagerNode;
    private Collection<LtrpPlayer> moderatorsOnDuty;
    private Collection<LtrpPlayer> adminsOnDuty;
    private Map<LtrpPlayer, String> pendingPlayerQuestions;
    private Map<LtrpPlayer, DynamicLabel> adminDutyLabels;
    private Map<LtrpPlayer, Instant> adminDutyStartInstants;
    private boolean destroyed;

    public AdminController(EventManager eventManager, PlayerCommandManager commandManager) {
        instance = this;
        this.eventManagerNode = eventManager.createChildNode();
        this.moderatorsOnDuty = new ArrayList<>();
        this.adminsOnDuty = new ArrayList<>();
        this.adminDutyLabels = new HashMap<>();
        this.adminDutyStartInstants = new HashMap<>();
        this.pendingPlayerQuestions = new HashMap<>();
        commandManager.registerCommands(new AdminCommands(this, eventManagerNode));
        commandManager.registerCommands(new ModeratorCommands(this, eventManagerNode));

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
            pendingPlayerQuestions.remove(p);
            if(adminsOnDuty.contains(p))
                endAdminWatch(p);
        });

        eventManagerNode.registerHandler(PlayerAcceptPlayerQuestion.class, e -> {
            pendingPlayerQuestions.remove(e.getTarget());
        });

        eventManagerNode.registerHandler(PlayerRejectPlayerQuestion.class, e -> {
            pendingPlayerQuestions.remove(e.getTarget());
        });

        eventManagerNode.registerHandler(PlayerAskQuestionEvent.class, e -> {
            LtrpPlayer p = e.getPlayer();
            String question = e.getQuestion();
            pendingPlayerQuestions.put(p, question);
            p.sendMessage(Color.MODERATOR, "Jûsø pateiktas klausimas buvo nusiûstas budintiems moderatoriams, palaukite (Prisijungusiu moderatoriø " + moderatorsOnDuty.size() + ")");
        });

        // Fail-safe.. Actually, it's not. This data is never inserted and I couldn't think of a better way
        eventManagerNode.registerHandler(PlayerDataLoadEvent.class, e -> {
            AdminLog.insertWatchIfNotExists(e.getPlayer());
        });
    }

    private void endAdminWatch(LtrpPlayer player) {
        player.setHealth(100f);
        player.setArmour(0f);
        player.setColor(PlayerController.DEFAULT_PLAYER_COLOR);
        player.getAttach().getSlotByBone(PlayerAttachBone.NECK).remove();
        Instant startInstant = adminDutyStartInstants.get(player);
        Duration dutyDuration = Duration.between(startInstant, Instant.now());
        LtrpPlayer.sendAdminMessage(String.format("[ID:%d]%s iðjungë budinèio Administratoriaus statusà, dirbo %s.", player.getId(), player.getName(), dutyDuration.toString()));

        AdminLog.updateWatchDuration(player, (int)dutyDuration.getSeconds());
        adminDutyLabels.get(player).destroy();
        adminDutyStartInstants.remove(player);
        adminDutyLabels.remove(player);
    }

    @Override
    public void destroy() {
        destroyed = true;
        eventManagerNode.cancelAll();
        moderatorsOnDuty.clear();
        pendingPlayerQuestions.clear();
        adminsOnDuty.clear();
        adminDutyLabels.values().forEach(Destroyable::destroy);
        adminDutyLabels.clear();
        adminDutyStartInstants.clear();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    public Collection<LtrpPlayer> getAdminsOnDuty() {
        return adminsOnDuty;
    }

    public Map<LtrpPlayer, String> getPendingPlayerQuestions() {
        return pendingPlayerQuestions;
    }

    public Collection<LtrpPlayer> getModeratorsOnDuty() {
        return instance.moderatorsOnDuty;
    }

    public static Collection<LtrpPlayer> getOndutyModerators() {
        return instance.getModeratorsOnDuty();
    }
}
