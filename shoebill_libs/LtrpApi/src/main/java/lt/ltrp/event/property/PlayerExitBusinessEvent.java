package lt.ltrp.event.property;


import lt.ltrp.object.Business;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class PlayerExitBusinessEvent extends PlayerExitPropertyEvent {



    public PlayerExitBusinessEvent(Business property, LtrpPlayer player) {
        super(property, player);
    }

    @Override
    public Business getProperty() {
        return (Business)super.getProperty();
    }
}
