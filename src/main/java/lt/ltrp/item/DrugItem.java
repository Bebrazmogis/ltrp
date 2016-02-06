package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class DrugItem extends ConsumableItem {

    public DrugItem(String name, ItemType type, int dosesLeft) {
        super(name, type, dosesLeft, true);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        boolean success = super.use(player, inventory);
        if(getDosesLeft() == 0) {
            this.destroy();
        }
        return success;
    }


    protected static DrugItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_consumable WHERE id = ?";
        DrugItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new DrugItem(result.getString("name"), type, result.getInt("doses"));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}
