package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Vector3D;

/**
 * @author Bebras
 *         2015.11.29.
 */

public class FishingRodItem extends ClothingItem {

    private static final int FISHING_ROD_MODEL = 18632;

    public FishingRodItem(String name, int id) {
        super(name, id, ItemType.FishingRod, false, FISHING_ROD_MODEL, PlayerAttachBone.HAND_LEFT);
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


}
