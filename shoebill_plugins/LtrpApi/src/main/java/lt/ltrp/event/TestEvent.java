package lt.ltrp.event;

import net.gtaun.util.event.Interruptable;

/**
 * @author Bebras
 *         2015.12.19.
 */
public interface TestEvent extends Interruptable {

    boolean isInterrupted();

}
