package lt.ltrp.item.event;

import lt.ltrp.item.object.Inventory;
import lt.ltrp.item.object.WeaponItem;
import lt.ltrp.player.event.PlayerEvent;
import lt.ltrp.player.object.LtrpPlayer;

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
