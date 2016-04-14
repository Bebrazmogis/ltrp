package lt.ltrp.player.util;

import lt.ltrp.DatabasePlugin;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.Shoebill;

import javax.sql.DataSource;
import java.sql.*;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class AdminLog {

    private static final DataSource ds = Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class).getDataSource();

    public static void log(LtrpPlayer player, String s) {
        new Thread(() -> {
            Connection connection = null;
            PreparedStatement stmt = null;
            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.util.Date utilDate = cal.getTime();
            java.sql.Date sqlDate = new Date(utilDate.getTime());
            String sql = "INSERT INTO logs_admin (admin_name, user_id, `date`, `action`) VALUES (?, ?, ?, ?)";
            try {
                connection = ds.getConnection();
                stmt = connection.prepareStatement(sql);
                stmt.setString(1, player.getName());
                stmt.setInt(2, player.getUUID());
                stmt.setDate(3, sqlDate);
                stmt.setString(4, s);
            } catch(SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if(connection != null)
                        connection.close();
                    if(stmt != null)
                        stmt.close();
                } catch(SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void incrementReportAccepted(LtrpPlayer player) {
        incrementReports(player, "reports_accepted");
    }

    public static void incrementRepportRejected(LtrpPlayer player) {
        incrementReports(player, "reports_rejected");
    }

    private static void incrementReports(LtrpPlayer player, String column) {
        String sql = String.format("UPDATE logs_admin_duty SET %s = %s + 1 WHERE user_id = ?", column, column);
        try (
                Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, player.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateWatchDates(LtrpPlayer player) {
        String sql = "UPDATE logs_admin_duty SET first_watch = IF(first_watch IS NULL, CURRENT_TIMESTAMP, first_watch), last_watch = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (
                Connection c = getConnection();
                PreparedStatement stmt = c.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateWatchDuration(LtrpPlayer player, int watchDurationSeconds) {
        String sql = "UPDATe logs_admin_duty SET admin_name = ?, last_watch = GREATEST(last_watch, ?), total_watch_time = total_watch_time + ? WHERE user_id = ?";
        try (
                Connection c = getConnection();
                PreparedStatement stmt = c.prepareStatement(sql);
                ) {
            stmt.setString(1, player.getName());
            stmt.setInt(2, watchDurationSeconds);
            stmt.setInt(3, watchDurationSeconds);
            stmt.setInt(4, player.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return Shoebill.get().getResourceManager().getPlugin(DatabasePlugin.class).getDataSource().getConnection();
    }

    public static void insertWatchIfNotExists(LtrpPlayer player) {
        String sql = "SELECT COUNT(id) FROM logs_admin_duty WHERE user_id = ? OR admin_name = ?";
        String insertSql = "INSERT INTO logs_admin_duty (admin_name, user_id) VALUES (?, ?)";
        try (
                Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                PreparedStatement insertStmt = connection.prepareCall(insertSql);
        ) {
            stmt.setInt(1, player.getUUID());
            stmt.setString(2, player.getName());
            ResultSet r = stmt.executeQuery();
            if(!r.next() || r.getInt(0) == 0) {
                insertStmt.setString(1, player.getName());
                insertStmt.setInt(2, player.getUUID());
                insertStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
