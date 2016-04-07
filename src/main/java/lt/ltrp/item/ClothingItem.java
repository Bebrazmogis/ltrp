package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.util.event.EventManager;

import java.util.function.Supplier;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class ClothingItem extends BasicItem {

    private static final String OPTION_WIELD = "Uþsidëti";
    private static final String OPTION_REMOVE = "Nusiimti";
    private static final String OPTION_EDIT = "Keisti pozicijà";

    private int modelId;
    private PlayerAttachBone bone;
    private boolean worn;

    public ClothingItem(int id, String name, EventManager eventManager, ItemType type, int modelid, PlayerAttachBone bone) {
        super(id, name, eventManager, type, false);
        this.modelId = modelid;
        this.bone = bone;
        this.worn = false;
    }

    public ClothingItem(String name, EventManager eventManager, ItemType type, int modelid, PlayerAttachBone bone) {
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

    @ItemUsageOption(name = "Uþsidëti")
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            if(!worn) {
                if(player.getAttach().getSlotByBone(bone).isUsed()) {
                    player.sendMessage(Color.LIGHTRED, "Jûs jau esate kaþkà apsirengæs ant ðios kûno dalies");
                } else {
                    player.getAttach().getSlotByBone(bone).set(bone, modelId, new Vector3D(), new Vector3D(), new Vector3D(), 1, 1);
                    player.sendActionMessage("Uþsideda daiktà kuris atrodo kaip " + getName());
                    worn = true;
                    return true;
                }
            } else {
                player.sendMessage(Color.LIGHTRED, "Jûs jau esate uþsidëjæs ðá daiktà");
            }
        }
        return false;
    }

    @ItemUsageOption(name = "Nusiimti")
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            if(worn) {
                player.getAttach().getSlotByBone(bone).remove();
                player.sendActionMessage("nusiema daiktà kuris atrodo kaip " + getName());
                worn = false;
                return true;
            }
        }
        return false;
    }

    @ItemUsageOption(name = "Redaguoti")
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
        switch(itemText) {
            case OPTION_WIELD:
                return () -> !isWorn();
            case OPTION_EDIT:
            case OPTION_REMOVE:
                return this::isWorn;
            default:
                return () -> false;
        }
    }

}
