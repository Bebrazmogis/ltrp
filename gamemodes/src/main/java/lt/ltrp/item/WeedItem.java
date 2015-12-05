package lt.ltrp.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class WeedItem extends DrugItem {

    public WeedItem(String name, int dosesLeft) {
        super(name, ItemType.Weed, dosesLeft);
    }

    protected static WeedItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_consumable WHERE id = ?";
        WeedItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new WeedItem(result.getString("name"), result.getInt("doses"));
                item.setItemId(itemid);
            }
        }
        return item;
    }

}
