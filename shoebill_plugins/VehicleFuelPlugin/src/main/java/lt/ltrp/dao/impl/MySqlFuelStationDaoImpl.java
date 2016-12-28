package lt.ltrp.business.dao.impl;

import lt.ltrp.dao.FuelStationDao;
import lt.ltrp.data.FuelStation;
import net.gtaun.shoebill.data.Radius;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.06.08.
 */
public class MySqlFuelStationDaoImpl implements FuelStationDao {

    private DataSource dataSource;

    public MySqlFuelStationDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private FuelStation toStation(ResultSet r) throws SQLException {
        return new FuelStation(
                r.getInt("id"),
                new Radius(
                        r.getFloat("x"),
                        r.getFloat("y"),
                        r.getFloat("z"),
                        r.getInt("interior"),
                        r.getInt("virtual_world"),
                        r.getFloat("radius")
                )
        );
    }

    @Override
    public Collection<FuelStation> get() {
        Collection<FuelStation> stations = new ArrayList<>();
        String sql = "SELECT * FROM fuel_stations";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
                ) {
            ResultSet r = stmt.executeQuery();
            while(r.next())
                stations.add(toStation(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stations;
    }

    @Override
    public FuelStation get(int uuid) {
        String sql = "SELECT * FROM fuel_stations WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, uuid);
            ResultSet r = stmt.executeQuery();
            if(r.next())
                return toStation(r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(FuelStation fuelStation) {
        String sql = "UPDATE fuel_stations SET x = ?, y = ?, z = ?, interior = ?, virtual_world = ?, radius = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setFloat(1, fuelStation.getRadius().x);
            stmt.setFloat(2, fuelStation.getRadius().y);
            stmt.setFloat(3, fuelStation.getRadius().z);
            stmt.setInt(4, fuelStation.getRadius().interiorId);
            stmt.setInt(5, fuelStation.getRadius().getWorldId());
            stmt.setFloat(6, fuelStation.getRadius().getRadius());
            stmt.setInt(7, fuelStation.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(FuelStation fuelStation) {
        String sql = "DELETE FROM fuel_stations WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, fuelStation.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(FuelStation fuelStation) {
        String sql = "INSERT INTO fuel_stations (x, y, z, interior, virtual_world, radius) VALUES (?, ?, ?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setFloat(1, fuelStation.getRadius().x);
            stmt.setFloat(2, fuelStation.getRadius().y);
            stmt.setFloat(3, fuelStation.getRadius().z);
            stmt.setInt(4, fuelStation.getRadius().interiorId);
            stmt.setInt(5, fuelStation.getRadius().getWorldId());
            stmt.setFloat(6, fuelStation.getRadius().getRadius());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next())
                return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
