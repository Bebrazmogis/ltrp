package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.object.DrinkItem;
import lt.ltrp.util.ItemUsageOption;import lt.ltrp.object.Inventory;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerEntry;
import net.gtaun.util.event.HandlerPriority;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class DrinkItemImpl extends ConsumableItemImpl implements DrinkItem {

    private SpecialAction specialAction;
    private HandlerEntry keyStateEvent;

    public DrinkItemImpl(int id, String name, EventManager eventManager, ItemType type, int dosesLeft, SpecialAction action) {
        super(id, name, eventManager, type, dosesLeft, false);
        this.specialAction = action;
    }

    @Override
    @ItemUsageOption(name = "Gerti")
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(getDosesLeft() == 0) {
            destroy();
            inventory.remove(this);
        } else {
            if(player.getInventory() != inventory) {
                if(player.getInventory().isFull()) {
                    player.sendMessage(Color.LIGHTRED, "Negalite pasiimti ðio daikto, jûsø kuprinë pilna");
                    return false;
                } else {
                    player.getInventory().add(this);
                    inventory.remove(this);
                    inventory = player.getInventory();
                }
            }
            if(player.getSpecialAction() != SpecialAction.NONE) {
                player.sendMessage(Color.LIGHTRED, "Jûs jau kaþkà darote.");
            } else {
                final Inventory inv = inventory;
                player.setSpecialAction(specialAction);
                player.sendActionMessage("iðsitraukia butelá ir já atsidaro");
                keyStateEvent = getEventManager().registerHandler(PlayerKeyStateChangeEvent.class, HandlerPriority.LOW, e-> {
                   if(e.getOldState().isKeyPressed(PlayerKey.FIRE)) {
                       //setDosesLeft(getDosesLeft()-1);
                       super.use(player, inv);
                       if(getDosesLeft() == 0) {
                           keyStateEvent.cancel();
                           inv.remove(this);
                           player.setSpecialAction(SpecialAction.NONE);
                           player.sendActionMessage("iðgeria paskutiná laðà ir iðmeta butelá.");
                       }
                   }
                });
                return true;
            }
        }
        return false;
    }

    public SpecialAction getSpecialAction() {
        return specialAction;
    }

    public void setSpecialAction(SpecialAction specialAction) {
        this.specialAction = specialAction;
    }

    @Override
    public void destroy() {
        if(keyStateEvent != null) {
            keyStateEvent.cancel();
        }
        super.destroy();
    }
}
