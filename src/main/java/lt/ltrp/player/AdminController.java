package lt.ltrp.player;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.player.event.PlayerAcceptPlayerQuestion;
import lt.ltrp.player.event.PlayerRejectPlayerQuestion;
import lt.ltrp.player.event.PlayerToggleModDutyEvent;
import net.gtaun.shoebill.common.command.PlayerCommandManager;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

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
    private Map<LtrpPlayer, String> pendingPlayerQuestions;
    private boolean destroyed;

    public AdminController(EventManager eventManager, PlayerCommandManager commandManager) {
        instance = this;
        this.eventManagerNode = eventManager.createChildNode();
        this.moderatorsOnDuty = new ArrayList<>();
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

        eventManagerNode.registerHandler(PlayerDisconnectEvent.class, e -> {
            LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
            moderatorsOnDuty.remove(p);
            pendingPlayerQuestions.remove(p);
        });

        eventManagerNode.registerHandler(PlayerAcceptPlayerQuestion.class, e -> {
            pendingPlayerQuestions.remove(e.getTarget());
        });

        eventManagerNode.registerHandler(PlayerRejectPlayerQuestion.class, e -> {
            pendingPlayerQuestions.remove(e.getTarget());
        });
    }

    @Override
    public void destroy() {
        destroyed = true;
        eventManagerNode.cancelAll();
        moderatorsOnDuty.clear();
        pendingPlayerQuestions.clear();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    public Map<LtrpPlayer, String> getPendingPlayerQuestions() {
        return pendingPlayerQuestions;
    }

    public Collection<LtrpPlayer> getModeratorsOnDuty() {
        return instance.moderatorsOnDuty;
    }
}
