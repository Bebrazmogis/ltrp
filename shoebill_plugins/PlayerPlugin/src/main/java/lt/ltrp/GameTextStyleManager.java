package lt.ltrp;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.textdraw.AbstractGameTextCustomStyle;
import lt.ltrp.textdraw.GameTextStyle7Textdraw;
import net.gtaun.shoebill.common.timers.TemporaryTimer;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

import java.util.*;

/**
 * @author Bebras
 *         2016.04.29.
 *
 *         This manager is used to add new, custom GameText styles used in {@link lt.ltrp.object.LtrpPlayer#sendGameText} and {@link net.gtaun.shoebill.object.Player#gameTextForAll(int i, int i, String text}
 */
public class GameTextStyleManager {

    private static GameTextStyleManager instance;

    public static GameTextStyleManager get() {
        return instance;
    }

    public static void sendGameText(LtrpPlayer player, String gameText, int styleId, int time) {
        if(styleId > 6) {
            AbstractGameTextCustomStyle style = getStyle(player, styleId);
            if(style != null) {
                style.show(gameText);
                TemporaryTimer.create(time, 1, i -> {
                    style.hide();
                }).start();
            }
        }
    }

    public static AbstractGameTextCustomStyle getStyle(LtrpPlayer player, int styleId) {
        if(instance.textdrawStyles.containsKey(player)) {
            Optional<AbstractGameTextCustomStyle> op = instance.textdrawStyles.get(player).stream().filter(s -> s.getStyleId() == styleId).findFirst();
            if(op.isPresent()) return op.get();
        }
        return null;
    }

    private EventManagerNode eventManagerNode;
    private Map<LtrpPlayer, Collection<AbstractGameTextCustomStyle>> textdrawStyles;

    public GameTextStyleManager(EventManager eventManager) {
        instance = this;
        this.eventManagerNode = eventManager.createChildNode();
        this.textdrawStyles = new HashMap<>();

        // Support for reloading
        LtrpPlayer.get().forEach(p -> {
            if(textdrawStyles.containsKey(p))
                textdrawStyles.get(p).clear();
            else
                textdrawStyles.put(p, new ArrayList<>());
            createStyles(p);
        });

        this.eventManagerNode.registerHandler(PlayerConnectEvent.class, e -> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            System.out.println("Player:" + player);
            if(!textdrawStyles.containsKey(player)) {
                textdrawStyles.put(player, new ArrayList<>());
            }
            createStyles(player);
        });

        this.eventManagerNode.registerHandler(PlayerDisconnectEvent.class, e-> {
            LtrpPlayer player = LtrpPlayer.get(e.getPlayer());
            if(player != null)
                textdrawStyles.get(player).forEach(Destroyable::destroy);
        });
    }

    public void destroy() {
        eventManagerNode.cancelAll();
        textdrawStyles.values().forEach(c -> c.forEach(Destroyable::destroy));
        textdrawStyles.clear();
    }

    private void createStyles(LtrpPlayer player) {
        textdrawStyles.get(player).add(GameTextStyle7Textdraw.create(player));
    }
}
