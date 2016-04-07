package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.14.
 */
public class RadioItem extends BasicItem {

    private float frequency;

    public RadioItem(int id, String name, EventManager eventManager, float frequency) {
        super(id, name, eventManager, ItemType.Radio, false);
        this.frequency = frequency;
    }

    public RadioItem(EventManager eventManager) {
        this(0, "Racija", eventManager, 100.0f);
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void sendMessage(LtrpPlayer player, String msg) {
        String message = String.format("**[D:%.1f] %s: %s",
                getFrequency(),
                player.getCharName(),
                msg);
        LtrpPlayer.get().stream().
                filter(p -> p.getInventory().containsType(ItemType.Radio) && ((RadioItem)p.getInventory().getItem(ItemType.Radio)).getFrequency() == getFrequency())
                .forEach(p -> p.sendMessage(Color.RADIO, message));
        player.sendActionMessage("sako:[RACIJA] " + msg);
    }

}
