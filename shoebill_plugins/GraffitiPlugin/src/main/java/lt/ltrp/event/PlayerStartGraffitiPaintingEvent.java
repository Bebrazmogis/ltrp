package lt.ltrp.event;

import lt.ltrp.object.Graffiti;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class PlayerStartGraffitiPaintingEvent extends GraffitiPlayerEvent {

    public PlayerStartGraffitiPaintingEvent(Graffiti graffiti, LtrpPlayer player) {
        super(graffiti, player);
    }

}
