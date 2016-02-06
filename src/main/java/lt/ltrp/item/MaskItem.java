package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class MaskItem extends ClothingItem {

    public MaskItem(String name, int modelid) {
        super(name, ItemType.Mask, modelid, PlayerAttachBone.HEAD);
    }


    @Override
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(super.equip(player, inventory)) {
            // hide nickame, alert admins perhaps
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(super.unequip(player, inventory)) {
            // show nickname
            return true;
        } else {
            return false;
        }
    }

    protected static MaskItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_clothing WHERE id = ?";
        MaskItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new MaskItem(result.getString("name"), result.getInt("model"));
                item.setItemId(itemid);
            }
        }
        return item;
    }

}
