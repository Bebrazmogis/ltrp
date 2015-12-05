package lt.ltrp.item;

import java.sql.*;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class ContainerItem extends BasicItem {

    private int itemCount, size;

    public ContainerItem(String name, ItemType type, boolean stackable, int items, int maxsize) {
        super(name, type, stackable);
        this.itemCount = items;
        this.size = maxsize;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    @Override
    protected PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_container SET `name` = ?, stackable = ?, items = ?, size = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getItemCount());
        stmt.setInt(4, getSize());
        stmt.setInt(5, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_container (`name`, stackable, items, size) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getItemCount());
        stmt.setInt(4, getSize());
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_container WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }

    protected static ContainerItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_container WHERE id = ?";
        ContainerItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new ContainerItem(result.getString("name"), type, result.getBoolean("stackable"), result.getInt("items"), result.getInt("size"));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}
