package lt.ltrp.item;

import net.gtaun.shoebill.constant.PlayerAttachBone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class SuitcaseItem extends ClothingItem {

    public SuitcaseItem(String name, int modelid) {
        super(name, ItemType.Suitcase, modelid, PlayerAttachBone.HAND_RIGHT);
    }


    protected static SuitcaseItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_clothing WHERE id = ?";
        SuitcaseItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new SuitcaseItem(result.getString("name"), result.getInt("model"));
                item.setItemId(itemid);
            }
        }
        return item;
    }


}
