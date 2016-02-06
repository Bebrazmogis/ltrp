package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Vector3D;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.11.29.
 */

public class FishingRodItem extends ClothingItem {

    private static final int FISHING_ROD_MODEL = 18632;

    public FishingRodItem(String name) {
        super(name, ItemType.FishingRod, FISHING_ROD_MODEL, PlayerAttachBone.HAND_LEFT);
    }

    public FishingRodItem() {
        this("Meðkerë");
    }


    @Override
    @ItemUsageOption(name = "Iðlankstyti")
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory() == inventory) {
            if(!isWorn()) {
                if(player.getAttach().getSlotByBone(getBone()).isUsed()) {
                    player.sendMessage(Color.LIGHTRED, "Jûs jau kaþkà laikote rankoje.");
                } else {
                    player.getAttach().getSlotByBone(getBone()).set(getBone(), getModelid(), new Vector3D(), new Vector3D(), new Vector3D(), 1, 1);
                    player.sendActionMessage("iðlanksto meðkeræ");
                    setWorn(true);
                    return true;
                }
            } else {
                player.sendMessage(Color.LIGHTRED, "Jûs jau esate iðlankstæs meðkeræ.");
            }
        }
        return false;
    }

    @Override
    @ItemUsageOption(name = "Sulankstyti")
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory() == inventory) {
            if(isWorn()) {
                player.getAttach().getSlotByBone(getBone()).remove();
                player.sendActionMessage("sulanksto meðkeræ");
                setWorn(false);
                return true;
            }
        }
        return false;
    }

    protected static FishingRodItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_clothing WHERE id = ?";
        FishingRodItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new FishingRodItem(result.getString("name"));
                item.setItemId(itemid);
            }
        }
        return item;
    }

}
