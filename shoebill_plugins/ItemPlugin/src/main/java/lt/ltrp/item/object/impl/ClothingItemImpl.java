package lt.ltrp.object.impl;


import lt.ltrp.constant.ItemType;
import lt.ltrp.data.Color;
import lt.ltrp.object.ClothingItem;
import lt.ltrp.object.Inventory;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.ItemUsageEnabler;
import lt.ltrp.util.ItemUsageOption;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.util.event.EventManager;

import java.util.function.Supplier;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class ClothingItemImpl extends BasicItem implements ClothingItem {

    private static final String OPTION_WIELD = "U�sid�ti";
    private static final String OPTION_REMOVE = "Nusiimti";
    private static final String OPTION_EDIT = "Keisti pozicij�";

    private int modelId;
    private PlayerAttachBone bone;
    private boolean worn;

    public ClothingItemImpl(int id, String name, EventManager eventManager, ItemType type, int modelid, PlayerAttachBone bone) {
        super(id, name, eventManager, type, false);
        this.modelId = modelid;
        this.bone = bone;
        this.worn = false;
    }

    public ClothingItemImpl(String name, EventManager eventManager, ItemType type, int modelid, PlayerAttachBone bone) {
        this(0, name, eventManager, type, modelid, bone);
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
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

    @ItemUsageOption(name = OPTION_WIELD)
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            if(!worn) {
                if(player.getAttach().getSlotByBone(bone).isUsed()) {
                    player.sendMessage(Color.LIGHTRED, "J�s jau esate ka�k� apsireng�s ant �ios k�no dalies");
                } else {
                    player.getAttach().getSlotByBone(bone).set(bone, modelId, new Vector3D(), new Vector3D(), new Vector3D(), 1, 1);
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

    @ItemUsageOption(name = OPTION_REMOVE)
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            if(worn) {
                player.getAttach().getSlotByBone(bone).remove();
                player.sendActionMessage("nusiema daikt� kuris atrodo kaip " + getName());
                worn = false;
                return true;
            }
        }
        return false;
    }

    @ItemUsageOption(name = OPTION_EDIT)
    public boolean edit(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            if(worn) {
                player.getAttach().getSlotByBone(bone).edit();
                return true;
            }
        }
        return false;
    }

    @ItemUsageEnabler
    public Supplier<Boolean> isEnabled(String itemText, LtrpPlayer player, Inventory inventory) {
        System.out.println("isEnabled " + itemText + " is worn:" + isWorn());
        switch(itemText) {
            case OPTION_WIELD:
                return () -> !isWorn();
            case OPTION_EDIT:
                return this::isWorn;
            case OPTION_REMOVE:
                return this::isWorn;
            default:
                return super.isEnabled(itemText, player, inventory);
        }
    }

}
