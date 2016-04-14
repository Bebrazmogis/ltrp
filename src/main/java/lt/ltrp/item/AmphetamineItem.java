package lt.ltrp.item;


import lt.ltrp.item.constant.ItemType;
import lt.ltrp.item.drug.DrugItemImpl;
import lt.ltrp.item.object.Inventory;
import lt.ltrp.player.object.LtrpPlayer;
import lt.maze.ysf.YSFPlugin;
import lt.maze.ysf.object.YSFPlayer;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.World;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class AmphetamineItem extends DrugItemImpl {

    private Timer drugTimer;

    public AmphetamineItem(int id, String name, EventManager eventManager, int dosesLeft) {
        super(id, name, eventManager, ItemType.Amphetamine, dosesLeft);
    }

    public AmphetamineItem(EventManager eventManager, int doses) {
        this(0, "Amfetaminas", eventManager, doses);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(player.getDrugs().isOn(getClass()))
            return false;

        player.sendActionMessage("staigiai átraukia amfetamino dozæ per nosá.");
        drugTimer = Timer.create(2000, 1, i -> {
            player.setWeather(-68);
            YSFPlayer ysfP = YSFPlugin.get(player);
            if(ysfP != null) {
                ysfP.setGravity(World.get().getGravity() - 0.002f);
            }
            player.setHealth(player.getHealth() + 10f);
        });
        drugTimer.start();
        super.use(player, inventory);
        super.setDrugEffectDuration(player, 4 * 60);
        return true;
    }

    @Override
    protected void onDrugEffectEnd(LtrpPlayer p) {
        YSFPlayer ysfP = YSFPlugin.get(p);
        if(ysfP != null) {
            ysfP.setGravity(World.get().getGravity());
        }
        p.setWeather(World.get().getWeather());
        if(p.getHealth() > 90f) {
            p.setHealth(90f);
        }
        p.setDrunkLevel(p.getDrunkLevel() + 5000);
    }

    @Override
    public void destroy() {
        drugTimer.destroy();
        super.destroy();
    }
}
