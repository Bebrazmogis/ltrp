package lt.ltrp.item.drug;

import lt.ltrp.item.ConsumableItem;
import lt.ltrp.item.Inventory;
import lt.ltrp.item.ItemType;
import lt.ltrp.item.drug.event.PlayerUseDrugsEvent;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.PlayerDrugs;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.03.
 */
public abstract class DrugItem extends ConsumableItem {

    private Timer drugEffectTimer;

    public DrugItem(int id, String name, EventManager eventManager, ItemType type, int dosesLeft) {
        super(id, name, eventManager, type, dosesLeft, true);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        boolean success = super.use(player, inventory);
        if(getDosesLeft() == 0) {
            this.destroy();
        }
        PlayerDrugs drugs = player.getDrugs();
        int level = drugs.getAddictionLevel(getClass());
        drugs.setAddictionLevel(getClass(), level+1);
        drugs.setOnDrugs(this.getClass(), true);
        getEventManager().dispatchEvent(new PlayerUseDrugsEvent(player, getClass()));
        return success;
    }

    protected final void setDrugEffectDuration(LtrpPlayer player, int seconds) {
        drugEffectTimer = Timer.create(seconds * 1000, i -> {
            player.getDrugs().setOnDrugs(getClass(), false);
            onDrugEffectEnd(player);
        });
        drugEffectTimer.start();
    }

    protected abstract void onDrugEffectEnd(LtrpPlayer p);

    @Override
    public void destroy() {
        if(drugEffectTimer != null) {
            drugEffectTimer.stop();
            drugEffectTimer.destroy();
        }
        super.destroy();
    }

}
