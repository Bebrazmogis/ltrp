package lt.ltrp.item;

import lt.ltrp.Util.PawnFunc;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.object.Timer;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class CocaineItem extends DrugItem {

    public CocaineItem(String name, int id, ItemType type, int dosesLeft) {
        super(name, id, type, dosesLeft);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        player.sendActionMessage("staigiai átraukia kokaino miltelius per nosá.");
        player.setWeather(-68);
        Timer.create(1300, 1, new Timer.TimerCallback() {
            @Override
            public void onTick(int i) {

            }

            @Override
            public void onStop() {
                player.setVarInt("DrugHP", 7);
                player.setVarInt("DrugHPLimit", 70);

                AmxCallable func = PawnFunc.getNativeMethod("DrugEffects");
                if (func != null) {
                    func.call(player.getId());
                }
            }
        });
        return true;
    }
}
