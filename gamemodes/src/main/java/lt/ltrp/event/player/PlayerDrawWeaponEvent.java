package lt.ltrp.event.player;

import lt.ltrp.item.Inventory;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.event.player.PlayerEvent;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class PlayerDrawWeaponEvent extends PlayerEvent {

    private WeaponData weaponData;
    private Inventory drawLocation;



    public PlayerDrawWeaponEvent(LtrpPlayer player, WeaponData data, Inventory drawLocation) {
        super(player);
    }

    public WeaponData getWeaponData() {
        return weaponData;
    }

    public Inventory getDrawLocation() {
        return drawLocation;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }
}
