package lt.ltrp.api;

import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.12.30.
 */
public interface Initializable {

    void initialize(EventManager eventManager);
    boolean isInitialized();
    default void uninitialize() {};
}
