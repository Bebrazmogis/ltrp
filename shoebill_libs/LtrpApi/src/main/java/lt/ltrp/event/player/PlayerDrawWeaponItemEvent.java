package lt.ltrp.event.player;

import lt.ltrp.object.Inventory;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeaponItem;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class PlayerDrawWeaponItemEvent extends PlayerEvent {

    private Inventory drawLocation;
    private WeaponItem item;


    public PlayerDrawWeaponItemEvent(LtrpPlayer player, Inventory drawLocation, WeaponItem item) {
        super(player);
        this.drawLocation = drawLocation;
        this.item = item;
    }

    public WeaponItem getItem() {
        return item;
    }

    public Inventory getDrawLocation() {
        return drawLocation;
    }

}
