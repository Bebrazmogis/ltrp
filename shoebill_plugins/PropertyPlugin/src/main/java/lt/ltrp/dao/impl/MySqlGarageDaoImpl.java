package lt.ltrp.dao.impl;

import lt.ltrp.dao.GarageDao;
import lt.ltrp.dao.PropertyDao;
import lt.ltrp.data.Color;
import lt.ltrp.object.Garage;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2016.04.20.
 */
public class MySqlGarageDaoImpl implements GarageDao {

    private DataSource dataSource;
    private PropertyDao propertyDao;
    private EventManager eventManager;

    public MySqlGarageDaoImpl(DataSource dataSource, PropertyDao propertyDao, EventManager eventManager) {
        this.dataSource = dataSource;
        this.propertyDao = propertyDao;
        this.eventManager = eventManager;
    }


    @Override
    public void update(Garage garage) {
        String sql = "UPDATE garages SET vehicle_entrance_x = ?, vehicle_entrance_y = ?, vehicle_entrance_z = ?, vehicle_entrance_interior = ?, " +
                "vehicle_entrance_virtual = ?, vehicle_entrance_angle = ?, vehicle_exit_x = ?, vehicle_exit_y = ?, vehicle_exit_z = ?, " +
                "vehicle_exit_interior = ?, vehicle_exit_virtual = ?, vehicle_exit_angle = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            propertyDao.update(garage);
            AngledLocation en = garage.getVehicleEntrance();
            AngledLocation ex = garage.getVehicleExit();
            stmt.setFloat(1, en.x);
            stmt.setFloat(2, en.y);
            stmt.setFloat(3, en.z);
            stmt.setInt(4, en.interiorId);
            stmt.setInt(5, en.worldId);
            stmt.setFloat(6, ex.angle);
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
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            propertyDao.insert(garage);
            AngledLocation en = garage.getVehicleEntrance();
            AngledLocation ex = garage.getVehicleExit();
            stmt.setInt(1, garage.getUUID());
            stmt.setFloat(2, en.x);
            stmt.setFloat(3, en.y);
            stmt.setFloat(4, en.z);
            stmt.setInt(5, en.interiorId);
            stmt.setInt(6, en.worldId);
            stmt.setFloat(7, ex.angle);
            stmt.setFloat(8, ex.x);
            stmt.setFloat(9, ex.y);
            stmt.setFloat(10, ex.z);
            stmt.setInt(11, ex.interiorId);
            stmt.setInt(12, ex.worldId);
            stmt.setFloat(13, ex.angle);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Garage garage) {
        String sql = "DELETE FROM garages WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            propertyDao.remove(garage);
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
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, i);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next())
                return resultToGarage(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void read() {
        String sql = "SELECT * FROM garages LEFT JOIN properties ON properties.id = garages.id";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next())
                resultToGarage(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        return Garage.create(r.getInt("id"),
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
