package lt.ltrp.item.object;

import lt.ltrp.player.object.LtrpPlayer;

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
    void sendMessage(LtrpPlayer player, String msg);


}
