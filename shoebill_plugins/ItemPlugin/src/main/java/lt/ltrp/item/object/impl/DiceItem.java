package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.util.ItemUsageOption;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;

import java.util.Random;

/**
 * @author Bebras
 *         2015.11.14.
 */


public class DiceItem extends BasicItem {

    private boolean thrown = false;

    public DiceItem(int id, String name, EventManager eventManager) {
        super(id, name, eventManager, ItemType.Dice, false);
    }

    public DiceItem(EventManager eventManager) {
        this(0, "Loğimo kauliukai", eventManager);
    }

    @ItemUsageOption(name = "Mesti")
    public boolean dice(LtrpPlayer player) {
        if(thrown) {
            return false;
        }
        player.sendActionMessage("meta loğimo kauliukus...");
        thrown = true;
        Timer.create(600, 1, e -> {
            player.sendStateMessage("... kauliukai iğsiridena skaièiumi " + (new Random().nextInt(6)+1));
            thrown = false;
            // I could fire off an event, but who cares? :/
        }).start();
        return true;
    }

}
