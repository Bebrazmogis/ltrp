package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;

import java.sql.*;

/**
 * @author Bebras
 *         2015.12.14.
 */
public class RadioItem extends BasicItem {

    private float frequency;

    public RadioItem(String name, float frequency) {
        super(name, ItemType.Radio, false);
        this.frequency = frequency;
    }

    public RadioItem() {
        this("Racija", 100.0f);
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void sendMessage(LtrpPlayer player, String msg) {
        String message = String.format("**[D:%.1f] %s: %s",
                getFrequency(),
                player.getCharName(),
                msg);
        LtrpPlayer.get().stream().
                filter(p -> p.getInventory().containsType(ItemType.Radio) && ((RadioItem)p.getInventory().getItem(ItemType.Radio)).getFrequency() == getFrequency())
                .forEach(p -> p.sendMessage(Color.RADIO, message));
        player.sendActionMessage("sako:[RACIJA] " + msg);
    }

    @Override
    protected PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_radio SET `name` = ?, stackable = ?, frequency = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setFloat(3, frequency);
        stmt.setInt(4, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_radio (`name`, stackable, frequency) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setFloat(3, frequency);
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_radio WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }

    protected static RadioItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_radio WHERE id = ?";
        RadioItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new RadioItem(result.getString("name"), result.getFloat("frequency"));
                item.setItemId(itemid);
            }
        }
        return item;
    }


}
