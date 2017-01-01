package lt.ltrp.player.util;

import lt.ltrp.player.event.PlayerSendPrivateMessageEvent;
import lt.ltrp.event.player.PlayerActionMessageEvent;
import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.event.player.PlayerStateMessageEvent;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.entities.Destroyable;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.util.event.Event;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Bebras
 *         2016.04.10.
 */
public final class PlayerLog implements Destroyable {

    private static PlayerLog instance;

    public static void init(DataSource dataSource, EventManager eventManager) {
        instance = new PlayerLog(dataSource, eventManager);
    }

    private DataSource dataSource;
    private EventManagerNode node;
    private Thread worker;

    private PlayerLog(DataSource dataSource, EventManager eventManager1) {
        this.node = eventManager1.createChildNode();
        this.dataSource = dataSource;

        worker = new Thread(() -> {
            node.registerHandler(PlayerTextEvent.class, HandlerPriority.HIGH, e -> {
                LtrpPlayer p = LtrpPlayer.Companion.get(e.getPlayer());
                logChat(p, e.getText());
            });


            node.registerHandler(PlayerActionMessageEvent.class, e -> {
                logAction(e.getPlayer(), e.getText());
            });

            node.registerHandler(PlayerStateMessageEvent.class, e -> {
                logState(e.getPlayer(), e.getText());
            });

            node.registerHandler(PlayerSendPrivateMessageEvent.class, e -> {
                logPm(e.getPlayer(), e.getTarget(), e.getMessage());
            });


            node.registerHandler(net.gtaun.shoebill.event.player.PlayerEvent.class, this::log);
            node.registerHandler(PlayerEvent.class, this::log);
        });
    }

    public static void logAction(LtrpPlayer player, String message) {
        instance.log(player, "action", message);
    }

    public static void logState(LtrpPlayer player, String state) {
        instance.log(player, "state", state);
    }

    public static void logChat(LtrpPlayer player, String text) {
        instance.log(player, "chat", text);
    }

    public static void logPm(LtrpPlayer player, LtrpPlayer target, String text) {
        instance.log(player, "pm", target.getName() + ";" + text);
    }

    public void log(LtrpPlayer player, String type, String text) {
        String sql = "INSERT INTO logs_player (username, user_id, `date`, `type`, `action`) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player.getName());
            stmt.setInt(2, player.getUUID());
            stmt.setDate(3, new java.sql.Date(new Date().getTime()));
            stmt.setString(4, type);
            stmt.setString(5, text);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void log(PlayerEvent event) {
        log(event.getPlayer(), event);
    }

    public void log(net.gtaun.shoebill.event.player.PlayerEvent event) {
        log(LtrpPlayer.Companion.get(event.getPlayer()), event);
    }

    private void log(LtrpPlayer player, Event event) {
        String sql = "INSERT INTO logs_player_event (username, user_id, `date`, `event_name`, `event_data`) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player.getName());
            stmt.setInt(2, player.getUUID());
            stmt.setDate(3, new java.sql.Date(new Date().getTime()));
            stmt.setString(4, event.getClass().getName());
            stmt.setString(5, event.toString());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        node.cancelAll();
        node.destroy();
        worker.interrupt();
    }

    @Override
    public boolean isDestroyed() {
        return node.isDestroy();
    }
}
