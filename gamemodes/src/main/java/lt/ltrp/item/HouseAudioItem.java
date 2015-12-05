package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.House;
import lt.ltrp.property.HouseUpgradeType;
import lt.ltrp.property.Property;
import net.gtaun.shoebill.common.dialog.MsgboxDialog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class HouseAudioItem extends BasicItem {

    public HouseAudioItem(String name) {
        super(name, ItemType.HouseAudio, false);
    }

    public HouseAudioItem() {
        this("Nam� audio sistema");
    }


    @ItemUsageOption(name = "�diegti � namus")
    public boolean installToHouse(LtrpPlayer player) {
        Property property = player.getProperty();
        if(property != null && property instanceof House) {
            House house = (House)property;
            if(!house.isUpgradeInstalled(HouseUpgradeType.Radio)) {
                MsgboxDialog.create(player, ItemController.getEventManager())
                        .caption("Patvirtinimas")
                        .buttonOk("Taip")
                        .buttonCancel("Ne")
                        .message("�io veiksmo atstatyti ne�manoma."
                                + "\nAr tikrai norite t�sti?"
                                + (house.getOwnerUserId() != player.getUserId() ? "\n\n{FF0000}Pastaba. �is namas jums nepriklauso." : ""))
                        .onClickOk(dialog -> {
                            house.addUpgrade(HouseUpgradeType.Radio);
                            player.sendMessage(Color.NEWS, "Sveikiname s�kmingai instaliavus garso sistem�. Gero klausymosi!");
                        })
                        .build()
                        .show();
            } else
                player.sendErrorMessage("�iame name jau yra audio aparat�ra.");
        } else
            player.sendErrorMessage("J�s neesate namuose");
        return false;
    }

    protected static HouseAudioItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_basic WHERE id = ?";
        HouseAudioItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new HouseAudioItem(result.getString("name"));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}
