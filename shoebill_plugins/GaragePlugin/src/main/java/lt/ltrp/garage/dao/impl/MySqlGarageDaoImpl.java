package lt.ltrp.garage.dao.impl;

import lt.ltrp.dao.GarageDao;
import lt.ltrp.data.Color;
import lt.ltrp.garage.event.GarageLoadedEvent;
import lt.ltrp.object.Garage;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.garage.object.impl.GarageImpl;
import lt.ltrp.property.dao.impl.MySqlPropertyDaoImpl;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class MySqlGarageDaoImpl extends MySqlPropertyDaoImpl implements GarageDao {
    
    private EventManager eventManager;

    public MySqlGarageDaoImpl(DataSource dataSource, EventManager eventManager) {
        super(dataSource);
        this.eventManager = eventManager;
    }


    @Override
    public void update(Garage garage) {
        String sql = "UPDATE garages SET vehicle_entrance_x = ?, vehicle_entrance_y = ?, vehicle_entrance_z = ?, vehicle_entrance_interior = ?, " +
                "vehicle_entrance_virtual = ?, vehicle_entrance_angle = ?, vehicle_exit_x = ?, vehicle_exit_y = ?, vehicle_exit_z = ?, " +
                "vehicle_exit_interior = ?, vehicle_exit_virtual = ?, vehicle_exit_angle = ? WHERE id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            super.update(garage);
            AngledLocation en = garage.getVehicleEntrance();
            AngledLocation ex = garage.getVehicleExit();
            stmt.setFloat(1, en.x);
            stmt.setFloat(2, en.y);
            stmt.setFloat(3, en.z);
            stmt.setInt(4, en.interiorId);
            stmt.setInt(5, en.worldId);
            stmt.setFloat(6, en.angle);
            if(ex != null) {
                stmt.setFloat(7, ex.x);
                stmt.setFloat(8, ex.y);
                stmt.setFloat(9, ex.z);
                stmt.setInt(10, ex.interiorId);
                stmt.setInt(11, ex.worldId);
                stmt.setFloat(12, ex.angle);
            } else {
                stmt.setNull(7, Types.FLOAT);
                stmt.setNull(8, Types.FLOAT);
                stmt.setNull(9, Types.FLOAT);
                stmt.setNull(10, Types.INTEGER);
                stmt.setNull(11, Types.INTEGER);
                stmt.setNull(12, Types.FLOAT);
            }
            stmt.setFloat(7, ex.x);
            stmt.setFloat(8, ex.y);
            stmt.setFloat(9, ex.z);
            stmt.setInt(10, ex.interiorId);
            stmt.setInt(11, ex.worldId);
            stmt.setFloat(12, ex.angle);
            stmt.setInt(13, garage.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(Garage garage) {
        String sql = "INSERT INTO garages (id, vehicle_entrance_x, vehicle_entrance_y, vehicle_entrance_z, vehicle_entrance_interior, " +
                "vehicle_entrance_virtual, vehicle_entrance_angle, vehicle_exit_x, vehicle_exit_y, vehicle_exit_z, vehicle_exit_interior, " +
                "vehicle_exit_virtual, vehicle_exit_angle) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            super.insert(garage);
            AngledLocation en = garage.getVehicleEntrance();
            AngledLocation ex = garage.getVehicleExit();
            stmt.setInt(1, garage.getUUID());
            stmt.setFloat(2, en.x);
            stmt.setFloat(3, en.y);
            stmt.setFloat(4, en.z);
            stmt.setInt(5, en.interiorId);
            stmt.setInt(6, en.worldId);
            stmt.setFloat(7, en.angle);
            if(ex != null) {
                stmt.setFloat(8, ex.x);
                stmt.setFloat(9, ex.y);
                stmt.setFloat(10, ex.z);
                stmt.setInt(11, ex.interiorId);
                stmt.setInt(12, ex.worldId);
                stmt.setFloat(13, ex.angle);
            } else {
                stmt.setNull(8, Types.FLOAT);
                stmt.setNull(9, Types.FLOAT);
                stmt.setNull(10, Types.FLOAT);
                stmt.setNull(11, Types.INTEGER);
                stmt.setNull(12, Types.INTEGER);
                stmt.setNull(13, Types.FLOAT);
            }
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Garage garage) {
        String sql = "DELETE FROM garages WHERE id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            super.remove(garage);
            stmt.setInt(1, garage.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Garage get(int i) {
        String sql = "SELECT * FROM garages LEFT JOIN properties ON properties.id = garages.id WHERE garages.id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, i);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()) {
                Garage g = resultToGarage(resultSet);
                eventManager.dispatchEvent(new GarageLoadedEvent(g));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Garage> get() {
        Collection<Garage> garages = new ArrayList<>();
        String sql = "SELECT * FROM garages LEFT JOIN properties ON properties.id = garages.id";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                Garage g = resultToGarage(resultSet);
                eventManager.dispatchEvent(new GarageLoadedEvent(g));
                garages.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return garages;
    }

    private Garage resultToGarage(ResultSet r) throws SQLException {
        Location exit = null;
        float x = r.getFloat("exit_x");
        if(!r.wasNull()) {
            exit = new Location(x, r.getFloat("exit_y"), r.getFloat("exit_z"), r.getInt("exit_interior"), r.getInt("exit_virtual"));
        }
        int owner = r.getInt("owner");
        if(r.wasNull())
            owner = LtrpPlayer.INVALID_USER_ID;
        return new GarageImpl(r.getInt("id"),
                r.getString("name"),
                owner,
                0,
                r.getInt("price"),
                new Location(r.getFloat("entrance_x"),
                        r.getFloat("entrance_y"),
                        r.getFloat("entrance_z"),
                        r.getInt("entrance_interior"),
                        r.getInt("entrance_virtual")),
                exit,
                new AngledLocation(r.getFloat("vehicle_entrance_x"),
                        r.getFloat("vehicle_entrance_y"),
                        r.getFloat("vehicle_entrance_z"),
                        r.getInt("vehicle_entrance_interior"),
                        r.getInt("vehicle_entrance_virtual"),
                        r.getFloat("vehicle_entrance_angle")),
                new AngledLocation(r.getFloat("vehicle_exit_x"),
                        r.getFloat("vehicle_exit_y"),
                        r.getFloat("vehicle_exit_z"),
                        r.getInt("vehicle_exit_interior"),
                        r.getInt("vehicle_exit_virtual"),
                        r.getFloat("vehicle_exit_angle")),
                new Color(r.getInt("label_color")),
                eventManager);
    }
}
