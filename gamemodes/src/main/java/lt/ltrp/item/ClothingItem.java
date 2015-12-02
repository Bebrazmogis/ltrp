package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Vector3D;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class ClothingItem extends BasicItem {

    private int modelid;
    private PlayerAttachBone bone;
    private boolean worn;

    public ClothingItem(String name, int id, ItemType type, int modelid, PlayerAttachBone bone) {
        super(name, id, type, false);
        this.modelid = modelid;
        this.bone = bone;
        this.worn = false;
    }

    public int getModelid() {
        return modelid;
    }

    public void setModelid(int modelid) {
        this.modelid = modelid;
    }

    public PlayerAttachBone getBone() {
        return bone;
    }

    public void setBone(PlayerAttachBone bone) {
        this.bone = bone;
    }

    public boolean isWorn() {
        return worn;
    }

    public void setWorn(boolean worn) {
        this.worn = worn;
    }

    @ItemUsageOption(name = "U�sid�ti")
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory() == inventory) {
            if(!worn) {
                if(player.getAttach().getSlotByBone(bone).isUsed()) {
                    player.sendMessage(Color.LIGHTRED, "J�s jau esate ka�k� apsireng�s ant �ios k�no dalies");
                } else {
                    player.getAttach().getSlotByBone(bone).set(bone, modelid, new Vector3D(), new Vector3D(), new Vector3D(), 1, 1);
                    player.sendActionMessage("U�sideda daikt� kuris atrodo kaip " + getName());
                    worn = true;
                    return true;
                }
            } else {
                player.sendMessage(Color.LIGHTRED, "J�s jau esate u�sid�j�s �� daikt�");
            }
        }
        return false;
    }

    @ItemUsageOption(name = "Nusiimti")
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory() == inventory) {
            if(worn) {
                player.getAttach().getSlotByBone(bone).remove();
                player.sendActionMessage("nusiema daikt� kuris atrodo kaip " + getName());
                worn = false;
                return true;
            }
        }
        return false;
    }

    @ItemUsageOption(name = "Redaguoti")
    public boolean edit(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory() == inventory) {
            if(worn) {
                player.getAttach().getSlotByBone(bone).edit();
                return true;
            }
        }
        return false;
    }
}
