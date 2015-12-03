package lt.ltrp.item;

import lt.ltrp.Util.PawnFunc;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.object.Timer;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class MetaamphetamineItem extends DrugItem {


    public MetaamphetamineItem(String name, int id, int dosesLeft) {
        super(name, id, ItemType.MetaAmphetamine, dosesLeft);
    }


    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        player.setWeather(141);


        Timer.create(1300, 1, new Timer.TimerCallback() {
            @Override
            public void onTick(int i) {

            }
            @Override
            public void onStop() {
                player.setVarInt("DrugHP", 10);
                player.setVarInt("DrugHPLimit", 50);

                AmxCallable func = PawnFunc.getNativeMethod("DrugEffects");
                if(func != null) {
                    func.call(player.getId());
                }
            }
        });
        return true;
    }


}
