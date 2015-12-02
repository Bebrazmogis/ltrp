package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.object.Timer;

import java.util.Random;

/**
 * @author Bebras
 *         2015.11.14.
 */


public class DiceItem extends BasicItem {

    private boolean thrown = false;

    public DiceItem(int id) {
        super("Lo�imo kauliukai", id, ItemType.Dice);
    }

    @ItemUsageOption(name = "Mesti")
    public boolean dice(LtrpPlayer player) {
        player.sendActionMessage("meta lo�imo kauliukus...");
        thrown = true;
        Timer.create(600, 1, e -> {
            player.sendStateMessage("... kauliukai i�siridena skai�iumi " + new Random().nextInt(5)+1);
            thrown = false;
            // I could fire off an event, but who cares? :/
        });
        return true;
    }

}
