package lt.ltrp.property.dao.impl;

import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.Property;
import lt.ltrp.property.dao.PropertyDao;

import javax.sql.DataSource;
import java.lang.IllegalArgumentException;
import java.lang.Override;
import java.lang.String;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * @author Bebras
 *         2016.04.19.
 */
public abstract class MySqlPropertyDaoImpl implements PropertyDao {

    private DataSource dataSource;

    public MySqlPropertyDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        if(dataSource == null)
            throw new IllegalArgumentException("Datasource cannot be null");
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void insert(Property property) {
        String sql = "INSERT INTO properties (`owner`, `name`, price, entrance_x, entrance_y, entrance_z, entrance_interior, entrance_virtual, exit_x, exit_y, exit_z, exit_interior, exit_virtual, locked, label_color, pickup_model)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            if(property.getOwner() == LtrpPlayer.INVALID_USER_ID)
                stmt.setNull(1, Types.INTEGER);
            else
                stmt.setInt(1, property.getOwner());
            stmt.setString(2, property.getName());
            stmt.setInt(3, property.getPrice());
            stmt.setFloat(4, property.getEntrance().x);
            stmt.setFloat(5, property.getEntrance().y);
            stmt.setFloat(6, property.getEntrance().z);
            stmt.setInt(7, property.getEntrance().getInteriorId());
            stmt.setInt(8, property.getEntrance().getInteriorId());
            if(property.getExit() != null) {
                stmt.setFloat(9, property.getExit().x);
                stmt.setFloat(10, property.getExit().y);
                stmt.setFloat(11, property.getExit().z);
                stmt.setInt(12, property.getExit().getInteriorId());
                stmt.setInt(13, property.getExit().getInteriorId());
            } else {
                stmt.setNull(9, Types.FLOAT);
                stmt.setNull(10, Types.FLOAT);
                stmt.setNull(11, Types.FLOAT);
                stmt.setNull(12, Types.INTEGER);
                stmt.setNull(13, Types.INTEGER);
            }
            stmt.setBoolean(14, property.isLocked());
            stmt.setInt(15, property.getLabelColor().getValue());
            stmt.setInt(16, property.getPickupModelId());
            stmt.execute();
            ResultSet r = stmt.getGeneratedKeys();
            if(r.next())
                property.setUUID(r.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Property property) {
        String sql = "UPDATE properties SET `owner` = ?, `name` = ?, price = ?, entrance_x = ?, entrance_y = ?, entrance_z = ?, " +
                "entrance_interior = ?, entrance_virtual = ?, exit_x = ?, exit_y = ?, exit_z = ?, exit_interior = ?, " +
                "exit_virtual = ?, locked = ?,  label_color = ?, pickup_model = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            if(property.getOwner() == LtrpPlayer.INVALID_USER_ID)
                stmt.setNull(1, Types.INTEGER);
            else
                stmt.setInt(1, property.getOwner());
            stmt.setString(2, property.getName());
            stmt.setInt(3, property.getPrice());
            stmt.setFloat(4, property.getEntrance().x);
            stmt.setFloat(5, property.getEntrance().y);
            stmt.setFloat(6, property.getEntrance().z);
            stmt.setInt(7, property.getEntrance().getInteriorId());
            stmt.setInt(8, property.getEntrance().getWorldId());
            if(property.getExit() != null) {
                stmt.setFloat(9, property.getExit().x);
                stmt.setFloat(10, property.getExit().y);
                stmt.setFloat(11, property.getExit().z);
                stmt.setInt(12, property.getExit().getInteriorId());
                stmt.setInt(13, property.getExit().getWorldId());
            } else {
                stmt.setNull(9, Types.FLOAT);
                stmt.setNull(10, Types.FLOAT);
                stmt.setNull(11, Types.FLOAT);
                stmt.setNull(12, Types.INTEGER);
                stmt.setNull(13, Types.INTEGER);
            }
            stmt.setBoolean(14, property.isLocked());
            stmt.setInt(15, property.getLabelColor().getValue());
            stmt.setInt(16, property.getPickupModelId());
            stmt.setInt(17, property.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Property property) {
        String sql = "DELETE FROM properties WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, property.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
