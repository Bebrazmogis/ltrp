package lt.ltrp.item;

import lt.ltrp.item.drug.DrugItem;
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
public class EctazyItem extends DrugItem {

    private Timer drugTimer;

    public EctazyItem(int id, String name, EventManager eventManager, int dosesLeft) {
        super(id, name, eventManager, ItemType.Extazy, dosesLeft);
    }

    public EctazyItem(EventManager eventManager, int doses) {
        this(0, "Ekstazi", eventManager, doses);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(player.getDrugs().isOn(getClass()))
            return false;

        player.sendActionMessage("ásideda saujoje laikomas tabletes á burnà ir jas nuryjà.");
        player.setDrunkLevel(player.getDrunkLevel() + 5 * 4000);
        drugTimer = Timer.create(1300, 1, i -> {
            YSFPlayer ysfP = YSFPlugin.get(player);
            if(ysfP != null) {
                ysfP.setGravity(World.get().getGravity() - 0.001f);
            }
            player.setHealth(player.getHealth() + 10f);
            player.setWeather(0);
        });
        drugTimer.start();
        super.setDrugEffectDuration(player, 5*60);
        super.use(player, inventory);
        return true;
    }

    @Override
    protected void onDrugEffectEnd(LtrpPlayer p) {
        YSFPlayer ysfP = YSFPlugin.get(p);
        if(ysfP != null) {
            ysfP.setGravity(World.get().getGravity());
        }
        p.setWeather(World.get().getWeather());
        if(p.getHealth() > 100f) {
            p.setHealth(100f);
        }
    }

    @Override
    public void destroy() {
        drugTimer.destroy();
        super.destroy();
    }

}
