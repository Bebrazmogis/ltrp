package lt.ltrp.object;

import lt.ltrp.ActionMessenger;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Bebras
 *         2016.04.13.
 */
public interface RadioItem extends Item {

    static final Collection<Float> PRIVATE_FREQUENCIES = new ArrayList<>();



    float getFrequency();
    void setFrequency(float frequency);
    default void sendMessage(LtrpPlayer player, String msg) {
        sendMessage(player, msg, ActionMessenger.Companion.getDEFAULT_RADIUS());
    }
    void sendMessage(LtrpPlayer player, String message, float distance);


}
