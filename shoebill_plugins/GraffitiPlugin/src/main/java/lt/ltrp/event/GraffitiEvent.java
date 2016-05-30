package lt.ltrp.event;

import lt.ltrp.object.Graffiti;
import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiEvent extends Event {

    private Graffiti graffiti;

    public GraffitiEvent(Graffiti graffiti) {
        this.graffiti = graffiti;
    }

    public Graffiti getGraffiti() {
        return graffiti;
    }
}
