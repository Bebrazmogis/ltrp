package lt.ltrp.dao.impl;

import lt.ltrp.dao.HouseDao;
import lt.ltrp.property.House;
import lt.ltrp.property.HouseWeedSapling;
import net.gtaun.shoebill.data.Location;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class SqlHouseDao implements HouseDao {

    private DataSource ds;

    public SqlHouseDao(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<HouseWeedSapling> getWeed(House house) {
        List<HouseWeedSapling> weed = new ArrayList<>();
        String sql = "SELECT * FROM house_weed WHERE house_id = ? AND harvested_by IS NULL ";
        try (
                Connection con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, house.getUid());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                HouseWeedSapling sapling = new HouseWeedSapling();
                sapling.setId(result.getInt("id"));
                sapling.setHouse(House.get(result.getInt("house_id")));
                sapling.setLocation(new Location(result.getFloat("x"), result.getFloat("y"), result.getFloat("z")));
                sapling.setPlantTimestamp(result.getLong("plant_timestamp"));
                sapling.setPlantedByUser(result.getInt("planted_by"));
                sapling.setGrowthTimestamp(result.getLong("growth_timestamp"));
                sapling.setStage(result.getInt("stage"));
                sapling.setYield(result.getInt("yield"));
                weed.add(sapling);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weed;
    }


    @Override
    public void updateWeed(HouseWeedSapling sapling) {
        String sql = "UPDATE house_weed SET house_id = ?, x = ?, y = ?, z = ?, plant_timestamp = ?, planted_by = ?, growth_timestamp = ?, growth_stage = ?, harvested_by = ?, yield = ? WHERE id = ?";
        try (
                Connection con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, sapling.getHouse().getUid());
            stmt.setFloat(2, sapling.getLocation().getX());
            stmt.setFloat(3, sapling.getLocation().getY());
            stmt.setFloat(4, sapling.getLocation().getZ());
            stmt.setLong(5, sapling.getPlantTimestamp());
            stmt.setInt(6, sapling.getPlantedByUser());
            stmt.setLong(7, sapling.getGrowthTimestamp());
            stmt.setInt(8, sapling.getStage());
            stmt.setInt(9, sapling.getHarvestedByUser());
            stmt.setInt(10, sapling.getYield());
            stmt.setInt(11, sapling.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertWeed(HouseWeedSapling sapling) {
        String sql = "INSERT INTO house_weed (house_id, x, y, z, plant_timestamp, planted_by, growth_timestamp, growth_stage, yield) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = ds.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, sapling.getHouse().getUid());
            stmt.setFloat(2, sapling.getLocation().getX());
            stmt.setFloat(3, sapling.getLocation().getY());
            stmt.setFloat(4, sapling.getLocation().getZ());
            stmt.setLong(5, sapling.getPlantTimestamp());
            stmt.setInt(6, sapling.getPlantedByUser());
            stmt.setLong(7, sapling.getGrowthTimestamp());
            stmt.setInt(8, sapling.getStage());
            stmt.setInt(9, sapling.getYield());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                sapling.setId(result.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
