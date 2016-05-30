package lt.ltrp.event;

import lt.ltrp.object.Graffiti;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.30.
 *
 *         This event is dispatched once a player finished painting a grafiti either by using /spray delete or by /spray save
 */
public class PlayerEndGraffitiPaintingEvent extends GraffitiPlayerEvent {

    public PlayerEndGraffitiPaintingEvent(Graffiti graffiti, LtrpPlayer player) {
        super(graffiti, player);
    }
}
