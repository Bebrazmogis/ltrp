package lt.ltrp.vehicle.dao.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.vehicle.dao.VehicleDao
import net.gtaun.util.event.EventManager
import org.slf4j.Logger
import java.sql.*
import java.time.LocalDateTime
import javax.sql.DataSource

/**
 * @author Bebras
 * *         2015.12.20.
 */
abstract class AbstractMySqlVehicleDaoImpl(protected val dataSource: DataSource,
                                           protected val eventManager: EventManager,
                                           protected val logger: Logger) : VehicleDao {

    override fun insert(): Int {
        val sql = "INSERT INTO vehicles (created_at) VALUES (?)"
        var uuid = Entity.INVALID_ID
        var con: Connection? = null
        var stmt: PreparedStatement? = null
        var keys: ResultSet? = null
        try {
            con = dataSource.connection
            stmt = con.prepareStatement(sql)
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()))
            stmt.execute()
            keys = stmt.generatedKeys
            if(keys.next()) {
                uuid = keys.getInt(1)
            }
        } catch(e: SQLException) {
            logger.error("Could not create a vehicle. ", e)
        } finally {
            con?.close()
            stmt?.close()
            keys?.close()
        }
        return uuid
    }

    /*
    @Override
    public String generateLicensePlate() {
        String sql = "SELECT license_plate FROM player_vehicles";
        List<String> plates = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                Statement stmt = con.createStatement();
                ) {
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()) {
                plates.add(result.getString("license_plate"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        String plate = null;
        Random random = new Random();
        while(plate == null || plates.contains(plate)) {
            plate = String.format("%3d-%c%c%c", random.nextInt(1000), (char)random.nextInt(24)+65, (char)random.nextInt(24)+65, (char)random.nextInt(24)+65);
        }
        return plate;
    }


    @Override
    public void update(LtrpVehicle vehicle) {
        String vehicleSql = "UPDATE vehicles SET model = ?, x = ?, y = ?, z = ?, angle = ?, license = ?, interior = ?, virtual_world = ?, color1 = ?, " +
                "color2 = ?, fuel = ?, mileage = ?  WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement vehicleStmt = con.prepareStatement(vehicleSql);
        ) {
            vehicleStmt.setInt(1, vehicle.getModelId());
            vehicleStmt.setFloat(2, vehicle.getSpawnLocation().getX());
            vehicleStmt.setFloat(3, vehicle.getSpawnLocation().getY());
            vehicleStmt.setFloat(4, vehicle.getSpawnLocation().getZ());
            vehicleStmt.setFloat(5, vehicle.getSpawnLocation().getAngle());
            vehicleStmt.setString(6, vehicle.getLicense());
            vehicleStmt.setInt(7, vehicle.getSpawnLocation().getInteriorId());
            vehicleStmt.setInt(8, vehicle.getSpawnLocation().getWorldId());
            vehicleStmt.setInt(9, vehicle.getColor1());
            vehicleStmt.setInt(10, vehicle.getColor2());
            vehicleStmt.setFloat(11, vehicle.getFuelTank().getFuel());
            vehicleStmt.setFloat(12, vehicle.getMileage());
            vehicleStmt.setInt(13, vehicle.getUUID());
            vehicleStmt.execute();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(LtrpVehicle vehicle) {
        int id = insert(
                vehicle.getModelId(),
                vehicle.getSpawnLocation(),
                vehicle.getLicense(),
                vehicle.getColor1(),
                vehicle.getColor2(),
                vehicle.getFuelTank().getFuel(),
                vehicle.getMileage()
        );
        vehicle.setUUID(id);
        return id;
    }

    @Override
    public int insert(int modelId, AngledLocation location, String license, int color1, int color2, float fuel, float mileage) {
        String vehicleSql = "INSERT INTO vehicles (model, x, y, z, angle, license, interior, virtual_world, color1, color2, fuel, mileage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement vehicleStmt = con.prepareStatement(vehicleSql, Statement.RETURN_GENERATED_KEYS);
        ) {
            vehicleStmt.setInt(1, modelId);
            vehicleStmt.setFloat(2, location.getX());
            vehicleStmt.setFloat(3, location.getY());
            vehicleStmt.setFloat(4, location.getZ());
            vehicleStmt.setFloat(5, location.getAngle());
            vehicleStmt.setString(6, license);
            vehicleStmt.setInt(7, location.getInteriorId());
            vehicleStmt.setInt(8, location.getWorldId());
            vehicleStmt.setInt(9, color1);
            vehicleStmt.setInt(10, color2);
            vehicleStmt.setFloat(11, fuel);
            vehicleStmt.setFloat(12, mileage);
            vehicleStmt.execute();
            ResultSet resultSet = vehicleStmt.getGeneratedKeys();
            int id = 0;
            if(resultSet.next()) {
                id = resultSet.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void delete(LtrpVehicle vehicle) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, vehicle.getUUID());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
*/
}
