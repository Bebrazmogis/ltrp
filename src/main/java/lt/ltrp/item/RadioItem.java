package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * @author Bebras
 *         2015.12.14.
 */
public class RadioItem extends BasicItem {

    public static final Collection<Float> PRIVATE_FREQUENCIES = new ArrayList<>();

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
        this.sendMessage(player, msg, LtrpPlayer.DEFAULT_ACTION_MESSAGE_DISTANCE);
    }

    public void sendMessage(LtrpPlayer player, String text, float distance) {
        // If it's a decreased distance message, we add some interference
        if(distance <= LtrpPlayer.DEFAULT_ACTION_MESSAGE_DISTANCE) {
            int inf = text.length() / 9;
            int textLen = text.length();
            char[] chars = text.toCharArray();
            int index;
            Random random = new Random();
            while(inf > 0 && !Character.isWhitespace(chars[(index = random.nextInt(textLen))]) && chars[index] != '?') {
                chars[index] = '?';
                inf--;
            }
            text = new String(chars);
        }
        String message = String.format("**[D:%.1f] %s: %s",
                getFrequency(),
                player.getCharName(),
                text);
        LtrpPlayer.get().stream().
                filter(p -> p.getInventory().containsType(ItemType.Radio) && ((RadioItem)p.getInventory().getItem(ItemType.Radio)).getFrequency() == getFrequency())
                .forEach(p -> p.sendMessage(Color.RADIO, message));
        player.sendActionMessage("sako:[RACIJA] " + text, distance);
    }

}
