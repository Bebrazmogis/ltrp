package lt.ltrp.event;

import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2015.12.19.
 */
public class TestEventImpl extends Event implements TestEvent {
    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public void interrupt() {

    }
}
