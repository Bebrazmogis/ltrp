package lt.ltrp.item;

import java.sql.*;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class DurableItem extends BasicItem {


    private int maxDurability, durability;

    public DurableItem(String name, ItemType type, int durability, int maxdurability, boolean stackable) {
        super(name, type, stackable);
        this.durability = durability;
        this.maxDurability = maxdurability;
    }

    public void use() {
        durability--;
        if(durability == 0) {
            this.destroy();
        }
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }


    public int getMaxDurability() {
        return maxDurability;
    }

    public int getDurability() {
        return durability;
    }

    @Override
    protected  PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_durable SET durability = ?, max_durability = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getDurability());
        stmt.setInt(2, getMaxDurability());
        stmt.setInt(3, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_durable (`name`, stackable, durability, max_durability) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getDurability());
        stmt.setInt(4, getMaxDurability());
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_durable WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }


    protected static DurableItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        DurableItem item = null;
        String sql = "SELECT * FROM items_durable WHERE id = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, itemid);
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new DurableItem(result.getString("name"), type, result.getInt("durability"), result.getInt("max_durability"), result.getBoolean("stackable"));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}

