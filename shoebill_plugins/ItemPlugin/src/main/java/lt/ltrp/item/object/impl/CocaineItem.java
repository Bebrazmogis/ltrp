package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.object.Inventory;
import lt.ltrp.object.LtrpPlayer;
import lt.maze.ysf.YSFPlugin;
import lt.maze.ysf.object.YSFPlayer;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.World;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class CocaineItem extends DrugItemImpl {

    private Timer drugTimer;

    public CocaineItem(int id, String name, EventManager eventManager, int dosesLeft) {
        super(id, name, eventManager, ItemType.Cocaine, dosesLeft);
    }

    public CocaineItem(EventManager eventManager, int dosesLeft) {
        this(0, "Kokainas", eventManager, dosesLeft);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(player.getDrugs().isOn(getClass())) {
            return false;
        }
        player.sendActionMessage("staigiai átraukia kokaino miltelius per nosá.");
        player.setWeather(-68);
        drugTimer = Timer.create(1300, 1, i -> {
            YSFPlayer ysfP = YSFPlugin.get(player);
            if(ysfP != null) {
                ysfP.setGravity(World.get().getGravity() - 0.0015f);
            }
            player.setHealth(player.getHealth() + 20f);
            player.setWeather(16);
        });
        drugTimer.start();
        super.setDrugEffectDuration(player, 5 * 60);
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
