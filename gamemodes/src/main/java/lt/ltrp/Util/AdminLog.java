package lt.ltrp.Util;

import lt.ltrp.LtrpGamemode;
import lt.ltrp.player.LtrpPlayer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.11.13.
 */
public class AdminLog {


    public static void log(LtrpPlayer player, String s) {
        new Thread(() -> {
            Connection connection = null;
            PreparedStatement stmt = null;
            java.util.Calendar cal = java.util.Calendar.getInstance();
            java.util.Date utilDate = cal.getTime();
            java.sql.Date sqlDate = new Date(utilDate.getTime());
            String sql = "INSERT INTO logs_admin (admin_name, user_id, `date`, action) VALUES (?, ?, ?, ?)";
            try {
                connection = LtrpGamemode.getDao().getConnection();
                stmt = connection.prepareStatement(sql);
                stmt.setString(1, player.getName());
                stmt.setInt(2, player.getUserId());
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

}
