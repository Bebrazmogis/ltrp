package lt.ltrp.item.object;

import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface ClothingItem extends Item {

    int getModelId();
    void setModelId(int modelId);
    PlayerAttachBone getBone();
    void setBone(PlayerAttachBone bone);
    boolean isWorn();
    void setWorn(boolean worn);
    boolean equip(LtrpPlayer player, Inventory inventory);
    boolean unequip(LtrpPlayer player, Inventory inventory);
    boolean edit(LtrpPlayer player, Inventory inventory);

}
