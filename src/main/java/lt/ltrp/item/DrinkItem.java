package lt.ltrp.item;

import lt.ltrp.item.constant.ItemType;
import lt.ltrp.item.object.Inventory;
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
public class DrinkItem extends ConsumableItemImpl {

    private SpecialAction specialAction;
    private HandlerEntry keyStateEvent;

    public DrinkItem(int id, String name, EventManager eventManager, ItemType type, int dosesLeft, SpecialAction action) {
        super(id, name, eventManager, type, dosesLeft, false);
        this.specialAction = action;
    }

    @Override
    @ItemUsageOption(name = "Gerti")
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(getDosesLeft() == 0) {
            inventory.remove(this);
        } else {
            if(player.getInventory() != inventory) {
                if(player.getInventory().isFull()) {
                    player.sendMessage(Color.LIGHTRED, "Negalite pasiimti �io daikto, j�s� kuprin� pilna");
                    return false;
                } else {
                    player.getInventory().add(this);
                    inventory.remove(this);
                    inventory = player.getInventory();
                }
            }
            if(player.getSpecialAction() != SpecialAction.NONE) {
                player.sendMessage(Color.LIGHTRED, "J�s jau ka�k� darote.");
            } else {
                final Inventory inv = inventory;
                player.setSpecialAction(SpecialAction.DRINK_BEER);
                player.sendActionMessage("i�sitraukia butel� ir j� atsidaro");
                keyStateEvent = getEventManager().registerHandler(PlayerKeyStateChangeEvent.class, HandlerPriority.LOW, e-> {
                   if(e.getOldState().isKeyPressed(PlayerKey.FIRE)) {
                       setDosesLeft(getDosesLeft()-1);
                       if(getDosesLeft() == 0) {
                           keyStateEvent.cancel();
                           inv.remove(this);
                           player.setSpecialAction(SpecialAction.NONE);
                           player.sendActionMessage("i�geria paskutin� la�� ir i�meta butel�.");
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
