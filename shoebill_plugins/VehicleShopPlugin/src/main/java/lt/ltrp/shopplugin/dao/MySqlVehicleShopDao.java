package lt.ltrp.shopplugin.dao;

import lt.ltrp.shopplugin.VehicleShop;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.03.15.
 */
public class MySqlVehicleShopDao implements VehicleShopDao {

    private DataSource dataSource;

    public MySqlVehicleShopDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public VehicleShop[] get() {
        String sql = "SELECT * FROM vehicle_shops";
        Collection<VehicleShop> shops = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement();
                ) {
            ResultSet resultSet = stmt.executeQuery(sql);
            while(resultSet.next()) {
                shops.add(new VehicleShop(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        new Location(
                                resultSet.getFloat("x"),
                                resultSet.getFloat("y"),
                                resultSet.getFloat("z"),
                                resultSet.getInt("interior"),
                                resultSet.getInt("virtual_world")
                        ),
                        getSpawns(connection, resultSet.getInt("id"))
                ));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return shops.toArray(new VehicleShop[0]);
    }

    @Override
    public void insert(VehicleShop shop) {
        String sql = "INSERT INTO vehicle_shops (name, x, y, z, interior, virtual_world) VALUES (?, ?, ?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ) {
            stmt.setString(1, shop.getName());
            stmt.setFloat(2, shop.getLocation().getX());
            stmt.setFloat(3, shop.getLocation().getY());
            stmt.setFloat(4, shop.getLocation().getZ());
            stmt.setInt(5, shop.getLocation().getInteriorId());
            stmt.setInt(6, shop.getLocation().getWorldId());
            stmt.execute();
            shop.setId(stmt.getGeneratedKeys().getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(VehicleShop shop) {
        String sql = "UPDATE vehicle_shops SET name = ?, x = ?, y = ?, z = ?, interior = ?, virtual_world = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, shop.getName());
            stmt.setFloat(2, shop.getLocation().getX());
            stmt.setFloat(3, shop.getLocation().getY());
            stmt.setFloat(4, shop.getLocation().getZ());
            stmt.setInt(5, shop.getLocation().getInteriorId());
            stmt.setInt(6, shop.getLocation().getWorldId());
            stmt.setInt(7, shop.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(VehicleShop shop) {
        String sql = "DELETE FROM vehicle_shops WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, shop.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private AngledLocation[] getSpawns(Connection connection, int id) throws SQLException {
        String sql = "SELECT * FROM vehicle_shop_spawns WHERE shop_id = ?";
        ArrayList<Location> locations = new ArrayList<>();
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                locations.add( new AngledLocation(
                        resultSet.getFloat("x"),
                        resultSet.getFloat("y"),
                        resultSet.getFloat("z"),
                        resultSet.getInt("interior"),
                        resultSet.getInt("virtual_world"),
                        resultSet.getInt("angle")
                ));
            }
        }
        return locations.toArray(new AngledLocation[0]);
    }
}
