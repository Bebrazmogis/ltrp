package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class MaskItem extends ClothingItem {

    public MaskItem(String name, int id, ItemType type, int modelid, PlayerAttachBone bone) {
        super(name, id, type, modelid, bone);
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

}
