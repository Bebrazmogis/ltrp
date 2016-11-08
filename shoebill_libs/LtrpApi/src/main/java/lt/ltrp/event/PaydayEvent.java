package lt.ltrp.event;

import net.gtaun.util.event.Event;

/**
 * @author Bebras
 *         2015.12.21.
 */
public class PaydayEvent extends Event {

    private int hour;

    public PaydayEvent(int hour) {
        this.hour = hour;
    }

    public int getHour() {
        return hour;
    }
}
