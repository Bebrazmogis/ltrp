package lt.ltrp.item;

import lt.ltrp.Util.PawnFunc;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.object.Timer;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class HeroinItem extends DrugItem {

    public HeroinItem(String name, int id, ItemType type, int dosesLeft) {
        super(name, id, type, dosesLeft);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(!inventory.containsType(ItemType.Syringe) && !player.getInventory().containsType(ItemType.Syringe)) {
            player.sendErrorMessage("Jûs neturite ðvirkðto");
            return false;
        } else {
            super.use(player, inventory);
            Item syringe = inventory.getItem(ItemType.Syringe);
            if(syringe == null) {
                syringe = player.getInventory().getItem(ItemType.Syringe);
            }
            player.setWeather(-64);
            syringe.setAmount(syringe.getAmount()-1);
            player.sendActionMessage("pasiemæs ðvirkstà ástato já á venà ant rankos ir susileidþia heroinà.");

            Timer.create(1300, 1, new Timer.TimerCallback() {
                @Override
                public void onTick(int i) {

                }

                @Override
                public void onStop() {
                    player.setVarInt("DrugHP", 5);
                    player.setVarInt("DrugHPLimit", 65);

                    AmxCallable func = PawnFunc.getNativeMethod("DrugEffects");
                    if (func != null) {
                        func.call(player.getId());
                    }
                }
            });
            return true;
        }
    }
}
