package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.data.Color;
import lt.ltrp.object.Inventory;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.ItemUsageEnabler;
import lt.ltrp.util.ItemUsageOption;
import lt.ltrp.player.util.PlayerUtils;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.PlayerAttach;
import net.gtaun.util.event.EventManager;

import java.util.function.Supplier;

/**
 * @author Bebras
 *         2015.11.29.
 */

public class FishingRodItem extends ClothingItemImpl {

    private static final int FISHING_ROD_MODEL = 18632;
    private static final String OPTION_EQUIP = "Iðlankstyti";
    private static final String OPTION_DEEQUIP = "Sulankstyti";

    public FishingRodItem(EventManager eventManager) {
        this(0, "Meðkerë", eventManager);
    }

    public FishingRodItem(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.FishingRod, FISHING_ROD_MODEL, PlayerAttachBone.HAND_LEFT);
    }



    @Override
    @ItemUsageOption(name = OPTION_EQUIP)
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            if(!isWorn()) {
                PlayerAttach.PlayerAttachSlot slot = PlayerUtils.getSlotByBone(player, getBone());
                player.sendDebug(slot);
                if(slot.isUsed()) {
                    player.sendMessage(Color.LIGHTRED, "Jûs jau kaþkà laikote rankoje.");
                } else {
                    slot.set(getBone(), getModelId(), new Vector3D(), new Vector3D(), new Vector3D(1f, 1f, 1f), 0, 0);
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
    @ItemUsageOption(name = OPTION_DEEQUIP)
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            if(isWorn()) {
                PlayerAttach.PlayerAttachSlot slot = PlayerUtils.getSlotByBone(player, getBone());
                slot.remove();
                player.sendActionMessage("sulanksto meðkeræ");
                setWorn(false);
                return true;
            }
        }
        return false;
    }

    @Override
    @ItemUsageEnabler
    public Supplier<Boolean> isEnabled(String itemText, LtrpPlayer player, Inventory inventory) {
        switch(itemText) {
            case OPTION_DEEQUIP: return this::isWorn;
            case OPTION_EQUIP: return () -> !isWorn();
            default:
                return super.isEnabled(itemText, player, inventory);
        }
    }

}
