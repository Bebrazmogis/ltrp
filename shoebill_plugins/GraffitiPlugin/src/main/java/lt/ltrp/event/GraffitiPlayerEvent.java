package lt.ltrp.event;

import lt.ltrp.object.Graffiti;
import lt.ltrp.player.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiPlayerEvent extends GraffitiEvent {

    private LtrpPlayer player;

    public GraffitiPlayerEvent(Graffiti graffiti, LtrpPlayer player) {
        super(graffiti);
        this.player = player;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }
}
