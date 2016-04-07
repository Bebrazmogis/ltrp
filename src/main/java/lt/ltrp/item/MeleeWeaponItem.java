package lt.ltrp.item;

import lt.ltrp.data.Animation;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.event.player.PlayerGiveDamageEvent;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerEntry;
import net.gtaun.util.event.HandlerPriority;

import java.sql.*;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class MeleeWeaponItem extends ClothingItem {

    private static final Animation DEFAULT_ANIM = new Animation("BASEBALL", "BAT_1", 4.1f, false, true, true, false, false, 0);
    /*
    18644 - screwdriver
    18633 - wrench
    18634 - crowbar
    18890 - rake
    19590 - woozie sword
    19631 - sledge hammer
     */
    private Animation animation;
    private HandlerEntry keyStateEntry;

    public MeleeWeaponItem(int id, String name, EventManager eventManager, int modelId) {
        super(id, name, eventManager, ItemType.MeleeWeapon, modelId, PlayerAttachBone.HAND_RIGHT);
        switch(modelId) {
            case 18644:
                this.animation = new Animation("KNIFE", "KNIFE_3", 4.1f, false, true, true, false, false, 0);
                break;
            case 18633:
            case 18634:
                this.animation = new Animation("BASEBALL", "BAT_1", 4.1f, false, true, true, false, false, 0);
                break;
            case 18890:
            case 19590:
            case 19631:
                this.animation = new Animation("BASEBALL", "BAT_4", 4.1f, false, true, true, false, false, 0);
                break;
            default:
                this.animation = DEFAULT_ANIM;
                break;
        }
    }

    public MeleeWeaponItem(String name, EventManager eventManager, int modelId, Animation animation) {
        this(0, name, eventManager, modelId);
        this.animation = animation;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    @Override
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(player.isInAnyVehicle()) {
            player.sendErrorMessage("Negalite iðsitraukti ðio ginklo bûdamas transporto priemonëje!");
        } else {
            if(super.equip(player, inventory)) {
                keyStateEntry = getEventManager().registerHandler(PlayerKeyStateChangeEvent.class, e-> {
                    if(!e.getOldState().isKeyPressed(PlayerKey.FIRE) && player.getKeyState().isKeyPressed(PlayerKey.FIRE)) {
                        player.applyAnimation(animation);
                    }
                });
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(super.unequip(player, inventory)) {
            if(keyStateEntry != null) {
                keyStateEntry.cancel();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void destroy() {
        if(keyStateEntry != null) {
            keyStateEntry.cancel();
        }
        super.destroy();
    }

}
