package lt.ltrp.dao.impl;

import lt.ltrp.constant.HouseUpgradeType;
import lt.ltrp.dao.HouseDao;
import lt.ltrp.data.Color;
import lt.ltrp.data.HouseWeedSapling;
import lt.ltrp.data.SpawnData;
import lt.ltrp.object.House;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.impl.HouseImpl;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class MySqlHouseDaoImpl extends MySqlPropertyDaoImpl implements HouseDao {

    private EventManager eventManager;

    public MySqlHouseDaoImpl(DataSource ds, EventManager eventManager) {
        super(ds);
        this.eventManager = eventManager;
    }

    @Override
    public List<HouseWeedSapling> getWeed(House house) {
        List<HouseWeedSapling> weed = new ArrayList<>();
        String sql = "SELECT * FROM house_weed WHERE house_id = ? AND harvested_by IS NULL ";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, house.getUUID());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                HouseWeedSapling sapling = new HouseWeedSapling();
                sapling.setId(result.getInt("id"));
                sapling.setHouse(House.get(result.getInt("house_id")));
                sapling.setLocation(new Location(result.getFloat("x"), result.getFloat("y"), result.getFloat("z")));
                sapling.setPlantTimestamp(result.getLong("plant_timestamp"));
                sapling.setPlantedByUser(result.getInt("planted_by"));
                sapling.setGrowthTimestamp(result.getLong("growth_timestamp"));
                sapling.setStage(HouseWeedSapling.GrowthStage.values()[result.getInt("stage")]);
                sapling.setYield(result.getInt("yield"));
                weed.add(sapling);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weed;
    }


    @Override
    public void update(HouseWeedSapling sapling) {
        String sql = "UPDATE house_weed SET house_id = ?, x = ?, y = ?, z = ?, plant_timestamp = ?, planted_by = ?, growth_timestamp = ?, growth_stage = ?, harvested_by = ?, yield = ? WHERE id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, sapling.getHouse().getUUID());
            stmt.setFloat(2, sapling.getLocation().getX());
            stmt.setFloat(3, sapling.getLocation().getY());
            stmt.setFloat(4, sapling.getLocation().getZ());
            stmt.setLong(5, sapling.getPlantTimestamp());
            stmt.setInt(6, sapling.getPlantedByUser());
            stmt.setLong(7, sapling.getGrowthTimestamp());
            stmt.setInt(8, sapling.getStage().ordinal());
            stmt.setInt(9, sapling.getHarvestedByUser());
            stmt.setInt(10, sapling.getYield());
            stmt.setInt(11, sapling.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(HouseWeedSapling sapling) {
        String sql = "INSERT INTO house_weed (house_id, x, y, z, plant_timestamp, planted_by, growth_timestamp, growth_stage, yield) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, sapling.getHouse().getUUID());
            stmt.setFloat(2, sapling.getLocation().getX());
            stmt.setFloat(3, sapling.getLocation().getY());
            stmt.setFloat(4, sapling.getLocation().getZ());
            stmt.setLong(5, sapling.getPlantTimestamp());
            stmt.setInt(6, sapling.getPlantedByUser());
            stmt.setLong(7, sapling.getGrowthTimestamp());
            stmt.setInt(8, sapling.getStage().ordinal());
            stmt.setInt(9, sapling.getYield());
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                sapling.setId(result.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(HouseWeedSapling houseWeedSapling) {
        String sql = "DELETE FROM house_weed WHERE id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, houseWeedSapling.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(House house) {
        String sql = "INSERT INTO houses (id, money, rent_price) VALUES (?, ?, ?)";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            super.insert(house);
            stmt.setInt(1, house.getUUID());
            stmt.setInt(2, house.getMoney());
            stmt.setInt(3, house.getRentPrice());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public House get(int i) {
        String sql = "SELECT * FROM properties LEFT JOIN houses ON properties.id = houses.id WHERE properties.id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, i);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {
                House h = resultToHouse(r);
                h.setWeedSaplings(getWeed(h));
                h.getUpgrades().forEach(h::addUpgrade);
                h.getTenants().addAll(getTenants(h, con));
                return h;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(House house) {
        String sql = "UPDATE houses SET money = ?, rent_price = ? WHERE id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            super.update(house);
            stmt.setInt(1, house.getMoney());
            stmt.setInt(2, house.getRentPrice());
            stmt.setInt(3, house.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(House house) {
        String sql = "DELETE FROM houses WHERE id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            super.remove(house);
            stmt.setInt(1, house.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<House> get() {
        List<House> houses = new ArrayList<>();
        String sql = "SELECT houses.*, properties.* FROM houses LEFT JOIN properties ON properties.id = houses.id";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                House h = resultToHouse(r);
                h.getTenants().addAll(getTenants(h, con));
                h.setWeedSaplings(getWeed(h));
                h.getUpgrades().forEach(h::addUpgrade);
                houses.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return houses;
    }

    @Override
    public void insert(House house, HouseUpgradeType houseUpgradeType) {
        String sql = "INSERT INTO house_upgrades (house_id, upgrade_id) VALUES (?, ?)";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, house.getUUID());
            stmt.setInt(2, houseUpgradeType.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(House house, HouseUpgradeType houseUpgradeType) {
        String sql = "DELETE FROM house_upgrades WHERE house_id = ? AND upgrade_id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, house.getUUID());
            stmt.setInt(2, houseUpgradeType.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<HouseUpgradeType> get(House house) {
        List<HouseUpgradeType> upgrades = new ArrayList<>();
        String sql = "SELECT * FROM house_upgrades WHERE house_id = ?";
        try (
                Connection con = getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, house.getUUID());
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                upgrades.add(HouseUpgradeType.get(r.getInt("upgrade_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return upgrades;
    }


    private House resultToHouse(ResultSet r) throws SQLException {
        Location exit = null;
        float x = r.getFloat("exit_x");
        if(!r.wasNull()) {
            exit = new Location(x, r.getFloat("exit_y"), r.getFloat("exit_z"), r.getInt("exit_interior"), r.getInt("exit_virtual"));
        }
        int owner = r.getInt("owner");
        if(r.wasNull())
            owner = LtrpPlayer.INVALID_USER_ID;
        return new HouseImpl(r.getInt("id"),
                r.getString("name"),
                owner,
                r.getInt("pickup_model"),
                r.getInt("price"),
                new Location(r.getFloat("entrance_x"), r.getFloat("entrance_y"), r.getFloat("entrance_z"), r.getInt("entrance_interior"), r.getInt("entrance_virtual")),
                exit,
                new Color(r.getInt("label_color")),
                r.getInt("money"),
                r.getInt("rent_price"),
                eventManager);
    }

    private List<Integer> getTenants(House house, Connection connection) throws SQLException {
        List<Integer> tenants = new ArrayList<>();
        String sql = "SELECT id FROM players WHERE spawn_type = ? AND spawn_ui = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, SpawnData.SpawnType.House.ordinal());
            stmt.setInt(2, house.getUUID());
            ResultSet r = stmt.executeQuery();
            while(r.next())
                tenants.add(r.getInt(1));
        }
        return tenants;
    }

}
