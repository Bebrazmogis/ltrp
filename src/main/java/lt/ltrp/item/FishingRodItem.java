package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.util.event.EventManager;

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

    public FishingRodItem(EventManager eventManager) {
        this(0, "Meðkerë", eventManager);
    }

    public FishingRodItem(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.FishingRod, FISHING_ROD_MODEL, PlayerAttachBone.HAND_LEFT);
    }



    @Override
    @ItemUsageOption(name = "Iðlankstyti")
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            if(!isWorn()) {
                if(player.getAttach().getSlotByBone(getBone()).isUsed()) {
                    player.sendMessage(Color.LIGHTRED, "Jûs jau kaþkà laikote rankoje.");
                } else {
                    player.getAttach().getSlotByBone(getBone()).set(getBone(), getModelId(), new Vector3D(), new Vector3D(), new Vector3D(), 1, 1);
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
        if(player.getInventory().equals(inventory)) {
            if(isWorn()) {
                player.getAttach().getSlotByBone(getBone()).remove();
                player.sendActionMessage("sulanksto meðkeræ");
                setWorn(false);
                return true;
            }
        }
        return false;
    }


}
