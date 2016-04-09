package lt.ltrp.item.event;

import lt.ltrp.item.Inventory;
import lt.ltrp.item.WeaponItem;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

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

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }
}
