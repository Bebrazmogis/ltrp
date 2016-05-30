package lt.ltrp.event;

import lt.ltrp.object.Graffiti;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class GraffitiDestroyEvent extends GraffitiEvent {

    public GraffitiDestroyEvent(Graffiti graffiti) {
        super(graffiti);
    }
}
