package lt.ltrp.dao.impl;

import lt.ltrp.dao.DrugAddictionDao;
import lt.ltrp.item.drug.DrugItem;
import lt.ltrp.player.data.PlayerAddiction;
import lt.ltrp.player.data.PlayerDrugs;
import lt.ltrp.player.object.LtrpPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2016.04.07.
 */
public class MySqlDrugAddictionDaoImpl implements DrugAddictionDao {

    private static Logger logger;


    private DataSource dataSource;

    public MySqlDrugAddictionDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        if(logger == null) {
            logger = LoggerFactory.getLogger(getClass());
        }
    }


    @Override
    public void insert(PlayerAddiction addiction) {
        String sql = "INSERT INTO player_addictinos (player_id, drug, level, last_dose) VALUES (?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, addiction.getPlayer().getUUID());
            stmt.setString(2, addiction.getType().getName());
            stmt.setInt(3, addiction.getLevel());
            stmt.setTimestamp(4, addiction.getLastDose());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(PlayerAddiction addiction) {
        String sql = "UPDATE player_addictinos SET `level` = ?, last_dose = ? WHERE player_id = ? AND drug = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, addiction.getLevel());
            stmt.setTimestamp(2, addiction.getLastDose());
            stmt.setInt(3, addiction.getPlayer().getUUID());
            stmt.setString(4, addiction.getType().getName());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(PlayerAddiction addiction) {
        String sql = "DELETE FROM player_addictinos WHERE player_id = ? AND drug = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, addiction.getPlayer().getUUID());
            stmt.setString(2, addiction.getType().getName());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerDrugs get(LtrpPlayer player) {
        PlayerDrugs drugs = new PlayerDrugs(player);
        String sql = "SELECT * FROM player_addictinos WHERE player_id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, player.getUUID());
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                Class<? extends DrugItem> type = null;
                try {
                    type = (Class<? extends DrugItem>) Class.forName(r.getString("drug"));
                } catch (ClassNotFoundException e) {
                    logger.error("Unknown drug type " + r.getString("drug") + " found for user " + player.getUUID());
                }
                drugs.addAddiction(new PlayerAddiction(player, type, r.getInt("level"), r.getTimestamp("last_dose")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drugs;
    }

    @Override
    public void update(PlayerDrugs drugs) {
        drugs.getTypes().forEach(c -> update(drugs.getAddiction(c)));
    }
}
