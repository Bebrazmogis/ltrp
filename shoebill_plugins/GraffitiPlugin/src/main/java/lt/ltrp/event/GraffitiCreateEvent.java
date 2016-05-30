package lt.ltrp.event;

import lt.ltrp.object.Graffiti;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiCreateEvent  extends GraffitiEvent {

    public GraffitiCreateEvent(Graffiti graffiti) {
        super(graffiti);
    }
}
