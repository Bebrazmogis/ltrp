package lt.ltrp.shopplugin.dao;

import lt.ltrp.shopplugin.ShopVehicle;
import lt.ltrp.shopplugin.VehicleShop;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.03.15.
 */
public class MySqlVehicleShopVehicleDao implements VehicleShopVehicleDao {

    private DataSource dataSource;

    public MySqlVehicleShopVehicleDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ShopVehicle[] get(VehicleShop vehicleShop) {
        String sql = "SELECT * FROM vehicle_shop_vehicles WHERE shop_id = ?";
        Collection<ShopVehicle> vehicles = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicleShop.getId());
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                vehicles.add(new ShopVehicle(
                        resultSet.getInt("id"),
                        resultSet.getInt("model_id"),
                        resultSet.getInt("price"),
                        vehicleShop
                ));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return vehicles.toArray(new ShopVehicle[0]);
    }

    @Override
    public void insert(ShopVehicle vehicle) {
        String sql = "INSERT INTO vehicle_shop_vehicles (shop_id, model_id, price) VALUES (?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, vehicle.getShop().getId());
            stmt.setInt(2, vehicle.getModelId());
            stmt.setInt(3, vehicle.getPrice());
            stmt.execute();
            vehicle.setId(stmt.getGeneratedKeys().getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(ShopVehicle vehicle) {
        String sql = "DELETE FROM vehicle_shop_vehicles WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ShopVehicle vehicle) {
        String sql = "UPDATE vehicle_shop_vehicles SET model_id = ?, price = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getModelId());
            stmt.setInt(2, vehicle.getPrice());
            stmt.setInt(3, vehicle.getId());
            stmt.execute();
            vehicle.setId(stmt.getGeneratedKeys().getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
