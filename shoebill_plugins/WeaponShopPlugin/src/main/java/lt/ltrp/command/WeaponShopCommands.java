package lt.ltrp.command;

import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeaponShop;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.06.15.
 */
public class WeaponShopCommands extends Commands {


    public boolean buy(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        Location loc = player.getLocation();
        Optional<WeaponShop> closestShopOptional = WeaponShop.get()
                .stream()
                .filter(s -> s.getLocation().distance(loc) < 5f)
                .min((s1, s2) -> Float.compare(s1.getLocation().distance(loc), s2.getLocation().distance(loc)));
        if(closestShopOptional.isPresent()) {
            WeaponShop shop = closestShopOptional.get();
            shop.showSoldWeaponDialog(player);
        }
        return false;
    }

}
