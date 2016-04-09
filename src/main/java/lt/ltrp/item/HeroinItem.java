package lt.ltrp.item;

import lt.ltrp.item.drug.DrugItem;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.vehicle.event.SpeedometerTickEvent;
import lt.maze.ysf.YSFPlugin;
import lt.maze.ysf.object.YSFPlayer;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.World;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class HeroinItem extends DrugItem {

    private Timer drugTimer;
    private EventManagerNode node;

    public HeroinItem(int id, String name, EventManager eventManager, int dosesLeft) {
        super(id, name, eventManager, ItemType.Heroin, dosesLeft);
    }

    public HeroinItem(EventManager eventManager, int doses) {
        this(0, "Heroinas", eventManager, doses);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(!inventory.containsType(ItemType.Syringe) && !player.getInventory().containsType(ItemType.Syringe)) {
            player.sendErrorMessage("Jûs neturite ðvirkðto");
            return false;
        } else {
            Item syringe = inventory.getItem(ItemType.Syringe);
            if(syringe == null) {
                syringe = player.getInventory().getItem(ItemType.Syringe);
                player.getInventory().remove(syringe);
            } else {
                inventory.remove(syringe);
            }
            player.sendActionMessage("pasiemæs ðvirkstà ástato já á venà ant rankos ir susileidþia heroinà.");

            drugTimer = Timer.create(3000, 1, i -> {
                YSFPlayer ysfP = YSFPlugin.get(player);
                if(ysfP != null) {
                    ysfP.setGravity(World.get().getGravity() - 0.003f);
                }
                player.setWeather(-64);
                node = getEventManager().createChildNode();
                node.registerHandler(SpeedometerTickEvent.class, e -> {
                    if(e.getPlayer().equals(player)) {
                        e.getVehicle().setVelocity(new Velocity(0f, 0f, 0f));
                    }
                });

                node.registerHandler(PlayerStateChangeEvent.class, e -> {
                    LtrpPlayer p = LtrpPlayer.get(e.getPlayer());
                    if(p.equals(player)) {
                        if(p.getState().equals(PlayerState.DRIVER)) {
                            p.applyAnimation("PED", "CAR_DEAD_LHS", 4.1f, true, false, false, false);
                        }
                    }
                });
            });
            drugTimer.start();
            super.setDrugEffectDuration(player, 2 * 60);
            super.use(player, inventory);
            return true;
        }
    }

    @Override
    protected void onDrugEffectEnd(LtrpPlayer p) {
        if(node != null)
            node.cancelAll();
        YSFPlayer ysfP = YSFPlugin.get(p);
        if(ysfP != null) {
            ysfP.setGravity(World.get().getGravity());
        }
        p.setWeather(World.get().getWeather());
        if(p.getHealth() > 50f) {
            p.setHealth(50f);
        }
    }

    @Override
    public void destroy() {
        drugTimer.destroy();
        if(node != null)
            node.cancelAll();
        super.destroy();
    }


}
