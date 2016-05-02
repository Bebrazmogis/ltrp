package lt.ltrp.dao.impl;

import lt.ltrp.constant.BusinessType;
import lt.ltrp.constant.ItemType;
import lt.ltrp.dao.BusinessDao;
import lt.ltrp.dao.PropertyDao;
import lt.ltrp.data.Color;
import lt.ltrp.data.property.business.commodity.BusinessCommodity;
import lt.ltrp.data.property.business.commodity.BusinessCommodityDrink;
import lt.ltrp.data.property.business.commodity.BusinessCommodityFood;
import lt.ltrp.data.property.business.commodity.BusinessCommodityItem;
import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.04.19.
 */
public class MySqlBusinessDaoImpl implements BusinessDao {

    private DataSource dataSource;
    private EventManager eventManager;
    private PropertyDao propertyDao;

    public MySqlBusinessDaoImpl(DataSource dataSource, PropertyDao propertyDao, EventManager eventManager) {
        this.dataSource = dataSource;
        this.propertyDao = propertyDao;
        this.eventManager = eventManager;
    }

    @Override
    public void update(Business business) {
        String sql = "UPDATE businesses SET entrance_price = ?, money = ?, type = ?, resources = ?, pickup_model = ?, commodity_limit = ?, resources_price = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            propertyDao.update(business);
            stmt.setInt(1, business.getEntrancePrice());
            stmt.setInt(2, business.getMoney());
            stmt.setInt(3, business.getBusinessType().getId());
            stmt.setInt(4, business.getResources());
            stmt.setInt(5, business.getPickupModelId());
            stmt.setInt(6, business.getCommodityLimit());
            stmt.setInt(7, business.getResourcePrice());
            stmt.setInt(8, business.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(Business business) {
        String sql = "INSERT INTO businesses (id, entrance_price, money, type, resources, pickup_model, commodity_limit, resources_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            propertyDao.insert(business);
            stmt.setInt(1, business.getUUID());
            stmt.setInt(2, business.getEntrancePrice());
            stmt.setInt(3, business.getMoney());
            stmt.setInt(4, business.getBusinessType().getId());
            stmt.setInt(5, business.getResources());
            stmt.setInt(6, business.getPickupModelId());
            stmt.setInt(7, business.getCommodityLimit());
            stmt.setInt(8, business.getResourcePrice());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Business business) {
        String sql = "DELETE FROM businesses WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            propertyDao.remove(business);
            stmt.setInt(1, business.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Business get(int i) {
        String sql = "SELECT * FROM businesses LEFT JOIN properties ON properties.id = businesses.id WHERE businesses.id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, i);
            ResultSet r = stmt.executeQuery();
            if(r.next()) {
                Business b = resultToBusiness(r);
                get(b).forEach(b::addCommodity);
                return b;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void read() {
        String sql = "SELECT * FROM businesses LEFT JOIN properties ON properties.id = businesses.id";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            ResultSet r = stmt.executeQuery();
            while(r.next()) {
                Business b = resultToBusiness(r);
                get(b).forEach(b::addCommodity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BusinessCommodity> get(Business business) {
        List<BusinessCommodity> list = new ArrayList<>();
        String sql = "SELECT businesses_commodities.*, businesses_available_commodities.* FROM businesses_commodities " +
                "LEFT JOIN businesses_available_commodities ON businesses_available_commodities.id = businesses_commodities.commodity_id " +
                "WHERE business_id = ? ORDER BY `no` ASC";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, business.getUUID());
            ResultSet r = stmt.executeQuery();
            while(r.next())
                list.add(resultToCommodity(business, r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void update(BusinessCommodity businessCommodity) {
        String sql;
        PreparedStatement stmt = null;
        try (Connection con = dataSource.getConnection()) {
            if(businessCommodity.getBusiness() != null) {
                sql = "UPDATE businesses_commodities SET price = ?, `no` = ? WHERE business_id = ? AND commodity_id = ?";
                stmt = con.prepareStatement(sql);
                stmt.setInt(1, businessCommodity.getPrice());
                stmt.setInt(2, businessCommodity.getNumber());
                stmt.setInt(3, businessCommodity.getBusiness().getUUID());
                stmt.setInt(4, businessCommodity.getUUID());

            } else {
                sql = "UPDATE businesses_available_commodities SET `name` = ?, `item_type` = ?, health_addition = ?, special_action = ?, drunk_level_per_sip = ? WHERE id = ?";
                stmt = con.prepareStatement(sql);
                stmt.setString(1, businessCommodity.getName());
                if(businessCommodity instanceof BusinessCommodityItem)
                    stmt.setInt(2, ((BusinessCommodityItem) businessCommodity).getItemType().id);
                else
                    stmt.setNull(2, Types.INTEGER);
                if(businessCommodity instanceof BusinessCommodityDrink) {
                    stmt.setInt(3, ((BusinessCommodityDrink) businessCommodity).getAction().getValue());
                    stmt.setInt(4, ((BusinessCommodityDrink) businessCommodity).getDrunkLevelPerSip());
                } else {
                    stmt.setNull(3, Types.INTEGER);
                    stmt.setNull(4, Types.INTEGER);
                }
                if(businessCommodity instanceof BusinessCommodityFood)
                    stmt.setFloat(5, ((BusinessCommodityFood) businessCommodity).getHealth());
                else
                    stmt.setNull(5, Types.FLOAT);

                stmt.setInt(6,  businessCommodity.getUUID());
            }
            stmt.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void insert(BusinessCommodity businessCommodity) {
        String sql = "INSERT INTO businesses_commodities (business_id, commodity_id, `no`, price) VALUES (?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, businessCommodity.getBusiness().getUUID());
            stmt.setInt(2, businessCommodity.getUUID());
            stmt.setInt(3, businessCommodity.getNumber());
            stmt.setInt(4, businessCommodity.getPrice());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(BusinessCommodity businessCommodity) {
        String sql = "DELETE FROM businesses_commodities WHERE business_id = ? AND commodity_id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, businessCommodity.getBusiness().getUUID());
            stmt.setInt(2, businessCommodity.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(BusinessType businessType, BusinessCommodity businessCommodity) {
        if(businessCommodity instanceof BusinessCommodityItem)
            insert(businessType, (BusinessCommodityItem)businessCommodity);
        else if(businessCommodity instanceof BusinessCommodityDrink)
            insert(businessType, (BusinessCommodityDrink)businessCommodity);
        else if(businessCommodity instanceof BusinessCommodityFood)
            insert(businessType, (BusinessCommodityFood)businessCommodity);
        else {
            String sql = "INSERT INTO businesses_available_commodities (business_type, `name`) VALUES (?, ?)";
            try (
                    Connection con = dataSource.getConnection();
                    PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ) {
                stmt.setInt(1, businessType.getId());
                stmt.setString(2, businessCommodity.getName());
                stmt.execute();
                ResultSet r = stmt.getGeneratedKeys();
                if(r.next())
                    businessCommodity.setUUID(r.getInt(1));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void insert(BusinessType businessType, BusinessCommodityItem businessCommodity) {
        String sql = "INSERT INTO businesses_available_commodities (business_type, `name`, item_type) VALUES (?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, businessType.getId());
            stmt.setString(2, businessCommodity.getName());
            stmt.setInt(3, businessCommodity.getItemType().id);
            stmt.execute();
            ResultSet r = stmt.getGeneratedKeys();
            if(r.next())
                businessCommodity.setUUID(r.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insert(BusinessType businessType, BusinessCommodityDrink businessCommodity) {
        String sql = "INSERT INTO businesses_available_commodities (business_type, `name`, special_action, drunk_level_per_sip) VALUES (?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, businessType.getId());
            stmt.setString(2, businessCommodity.getName());
            stmt.setInt(3, businessCommodity.getAction().getValue());
            stmt.setInt(4, businessCommodity.getDrunkLevelPerSip());
            stmt.execute();
            ResultSet r = stmt.getGeneratedKeys();
            if(r.next())
                businessCommodity.setUUID(r.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insert(BusinessType businessType, BusinessCommodityFood businessCommodity) {
        String sql = "INSERT INTO businesses_available_commodities (business_type, `name`, health_addition) VALUES (?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, businessType.getId());
            stmt.setString(2, businessCommodity.getName());
            stmt.setFloat(3, businessCommodity.getHealth());
            stmt.execute();
            ResultSet r = stmt.getGeneratedKeys();
            if(r.next())
                businessCommodity.setUUID(r.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(BusinessType businessType, BusinessCommodity businessCommodity) {
        String sql = "DELETE FROM businesses_available_commodities WHERE id = ? AND business_type = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, businessCommodity.getUUID());
            stmt.setInt(2, businessType.getId());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BusinessCommodity> get(BusinessType businessType) {
        List<BusinessCommodity> list = new ArrayList<>();
        String sql = "SELECT * FROM businesses_available_commodities WHERE business_type = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, businessType.getId());
            ResultSet r = stmt.executeQuery();
            while(r.next())
                list.add(resultToCommodity(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Business resultToBusiness(ResultSet r) throws SQLException {
        Location exit = null;
        float x = r.getFloat("exit_x");
        if(!r.wasNull()) {
            exit = new Location(x, r.getFloat("exit_y"), r.getFloat("exit_z"), r.getInt("exit_interior"), r.getInt("exit_virtual"));
        }
        int owner = r.getInt("owner");
        if(r.wasNull())
            owner = LtrpPlayer.INVALID_USER_ID;
        return Business.create(r.getInt("id"),
                r.getString("name"),
                BusinessType.get(r.getInt("type")),
                owner,
                r.getInt("pickup_model"),
                r.getInt("price"),
                new Location(r.getFloat("entrance_x"), r.getFloat("entrance_y"), r.getFloat("entrance_z"), r.getInt("entrance_interior"), r.getInt("entrance_virtual")),
                exit,
                new Color(r.getInt("label_color")),
                r.getInt("money"),
                r.getInt("resources"),
                r.getInt("commodity_limit"),
                eventManager);
    }

    private BusinessCommodity resultToCommodity(ResultSet r) throws SQLException {
        BusinessCommodity commodity;
        int uuid = r.getInt("id");
        String name = r.getString("name");
        int type = r.getInt("item_type");
        if(!r.wasNull()) {
            commodity = new BusinessCommodityItem(uuid, name, ItemType.getById(type), eventManager);
        }
        float health = r.getFloat("health_addition");
        if(!r.wasNull()) {
            commodity = new BusinessCommodityFood(uuid, name, health);
        }
        int specialActionId = r.getInt("special_action");
        int drunkLevelPerSip = r.getInt("drunk_level_per_sip");
        if(!r.wasNull())
            commodity = new BusinessCommodityDrink(uuid, name, SpecialAction.get(specialActionId), drunkLevelPerSip, eventManager);
        else
            commodity = new BusinessCommodity(uuid, name);
        return commodity;
    }

    private BusinessCommodity resultToCommodity(Business business, ResultSet r) throws SQLException {
        BusinessCommodity commodity;
        int uuid = r.getInt("id");
        int price = r.getInt("price");
        int number = r.getInt("no");
        String name = r.getString("name");
        int type = r.getInt("item_type");
        if(!r.wasNull()) {
            commodity = new BusinessCommodityItem(uuid, business, name, price, number, ItemType.getById(type), eventManager);
        }
        float health = r.getFloat("health_addition");
        if(!r.wasNull()) {
            commodity = new BusinessCommodityFood(uuid, business, name, price, number, health);
        }
        int specialActionId = r.getInt("special_action");
        int drunkLevelPerSip = r.getInt("drunk_level_per_sip");
        if(!r.wasNull())
            commodity = new BusinessCommodityDrink(uuid, business, name, price, number, SpecialAction.get(specialActionId), drunkLevelPerSip, eventManager);
        else
            commodity = new BusinessCommodity(uuid, business, name, price, number);
        return commodity;
    }
}
