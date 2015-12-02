package lt.ltrp.item;

import lt.ltrp.data.Animation;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.event.player.PlayerGiveDamageEvent;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.util.event.HandlerEntry;
import net.gtaun.util.event.HandlerPriority;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class MeleeWeaponItem extends ClothingItem {

    private Animation animation;
    private HandlerEntry keyStateEntry;
    private HandlerEntry giveDamageEntry;
    private float damageIncrease;

    public MeleeWeaponItem(String name, int id, ItemType type, int modelid, PlayerAttachBone bone, Animation animation, float extradmgproc) {
        super(name, id, type, modelid, bone);
        this.animation = animation;
        this.damageIncrease = extradmgproc;
    }



    @Override
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(super.equip(player, inventory)) {
            keyStateEntry = ItemController.getEventManager().registerHandler(PlayerKeyStateChangeEvent.class, e-> {
                if(e.getOldState().isKeyPressed(PlayerKey.FIRE)) {
                    player.applyAnimation(animation);
                }
            });

            giveDamageEntry = ItemController.getEventManager().registerHandler(PlayerGiveDamageEvent.class, HandlerPriority.LOW, e -> {
                if(e.getVictim() != null) {
                    e.getVictim().setHealth(e.getVictim().getHealth() - e.getAmount() * damageIncrease);
                }
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(super.unequip(player, inventory)) {
            if(keyStateEntry != null) {
                keyStateEntry.cancel();
            }
            if(giveDamageEntry != null) {
                giveDamageEntry.cancel();
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
        if(giveDamageEntry != null) {
            giveDamageEntry.cancel();
        }
        super.destroy();
    }

}
