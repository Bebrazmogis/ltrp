package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.SpecialAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class CigarettesItem extends DurableItem {

    private static final int MAX_CIGARETTES = 20;

    public CigarettesItem(String name, int durabilityy) {
        super(name, ItemType.Cigarettes, durabilityy, MAX_CIGARETTES, false);
    }


    @ItemUsageOption(name = "Uþsirûkyti")
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().containsType((ItemType.Lighter)) || inventory.containsType(ItemType.Lighter)) {
            player.setSpecialAction(SpecialAction.SMOKE_CIGGY);
            this.use();
            player.sendActionMessage("iðástraukia cigaretæ, ja prisidega ir pradeda rûkyti");
        }
        return false;
    }


    protected static CigarettesItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        CigarettesItem item = null;
        String sql = "SELECT * FROM items_durable WHERE id = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new CigarettesItem(result.getString("name"), result.getInt("durability"));
                item.setItemId(itemid);
            }
        }
        return item;
    }

}
