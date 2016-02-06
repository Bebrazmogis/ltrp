package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;

import java.sql.*;

/**
 * @author Bebras
 *         2015.11.14.
 */
public abstract class ConsumableItem extends BasicItem {

    private int dosesLeft;

    public ConsumableItem(String name, ItemType type, int dosesLeft, boolean stackable) {
        super(name, type, stackable);
        this.dosesLeft = dosesLeft;
    }

    public int getDosesLeft() {
        return dosesLeft;
    }

    public void setDosesLeft(int dosesLeft) {
        this.dosesLeft = dosesLeft;
    }

    @ItemUsageOption(name = "Vartoti")
    public boolean use(LtrpPlayer player , Inventory inventory) {
        dosesLeft--;
        return true;
    }


    @Override
    protected PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_consumable SET `name` = ?, stackable = ?, doses = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getDosesLeft());
        stmt.setInt(4, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_consumable (`name`, stackable, doses) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getDosesLeft());
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_consumable WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }

}
