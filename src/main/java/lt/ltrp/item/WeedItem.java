package lt.ltrp.item;

import lt.ltrp.item.constant.ItemType;
import lt.ltrp.item.drug.DrugItemImpl;
import lt.ltrp.item.object.Inventory;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import lt.maze.ysf.YSFPlugin;
import lt.maze.ysf.object.YSFPlayer;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.World;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.05.
 */
public class WeedItem extends DrugItemImpl {

    private Timer drugTimer;

    public WeedItem(int id, String name, EventManager eventManager, int dosesLeft) {
        super(id, name, eventManager, ItemType.Weed, dosesLeft);
    }

    public WeedItem(EventManager eventManager, int doeses) {
        this(0, "Marihuana", eventManager, doeses);
    }


    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        drugTimer = Timer.create(500, 1, i -> {
            player.sendActionMessage("laikydamas suktinæ pridega jà þiebtuveliu.");
            player.setSpecialAction(SpecialAction.SMOKE_CIGGY);
            YSFPlayer ysfP = YSFPlugin.get(player);
            if(ysfP != null) {
                ysfP.setGravity(World.get().getGravity() + 0.0015f);
            }
            player.setHealth(player.getHealth() + 10f);
            player.setWeather(17);

            drugTimer = Timer.create(400, ii -> {
                if(player.isInAnyVehicle()) {
                    LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
                    if(vehicle != null) {
                        Velocity velocity = vehicle.getVelocity();
                        velocity.x -= velocity.x / 100 * 5;
                        velocity.y -= velocity.x / 100 * 5;
                        vehicle.setVelocity(velocity);
                    }
                }
            });
            drugTimer.start();
        });
        drugTimer.start();
        super.use(player, inventory);
        super.setDrugEffectDuration(player, 7 * 60);
        return true;
    }

    @Override
    protected void onDrugEffectEnd(LtrpPlayer p) {
        drugTimer.stop();
        YSFPlayer ysfP = YSFPlugin.get(p);
        if(ysfP != null) {
            ysfP.setGravity(World.get().getGravity());
        }
        p.setWeather(World.get().getWeather());
        if(p.getHealth() > 100f) {
            p.setHealth(100f);
        }
        p.setSpecialAction(SpecialAction.NONE);
    }

    @Override
    public void destroy() {
        if(drugTimer != null)
            drugTimer.destroy();
        super.destroy();
    }


}
