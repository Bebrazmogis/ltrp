package lt.ltrp.item;

import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.event.player.PlayerDrawWeaponEvent;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.object.Timer;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class WeaponItem extends BasicItem {

    private LtrpWeaponData weaponData;
    private boolean beingDrawn = false;

    public WeaponItem(LtrpWeaponData weaponData, String name, int id, ItemType type) {
        super(name, id, type, false);
        this.weaponData = weaponData;
    }

    public WeaponData getWeaponData() {
        return weaponData;
    }

    public void setWeaponData(LtrpWeaponData weaponData) {
        this.weaponData = weaponData;
    }

    @Override
    public boolean drop(LtrpPlayer player, Inventory inventory) {
        if(!weaponData.isJob()) {
            if(!player.isInComa()) {
                weaponData.setDropped(player.getLocation());
                player.getInventory().remove(this);
                player.sendActionMessage("i�meta ginkl� kuris atrodo kaip " + getName());
                return true;
            } else
                player.sendErrorMessage("J�s esate komos b�senoje!");
        } else
            player.sendErrorMessage("Negalite i�mesti darbinio ginklo.");
        return false;
    }

    @ItemUsageOption(name = "I�sitraukti")
    public boolean drawWeapon(LtrpPlayer player, Inventory inventory) {
        if(beingDrawn) {
            return false;
        }
        if(player.getInventory() == inventory) {
            player.sendActionMessage("bando ka�k� i�sitraukti");
            beingDrawn = true;
            Timer.create(1500 + player.getDrunkLevel(), 1, e -> {
                onItemDrawn(player, weaponData, inventory);
            });
        } else {
            player.sendActionMessage("bando ka�k� i�sitraukti");
            beingDrawn = true;
            Timer.create(2300 + player.getDrunkLevel(), 1, e -> {
                onItemDrawn(player, weaponData, inventory);
            });
        }
        return true;
    }

    private void onItemDrawn(LtrpPlayer player, WeaponData weapondata, Inventory location) {
        player.giveWeapon(getWeaponData());
        if(location == player.getInventory()) {
            player.sendActionMessage("i�sitraukia ginkl� kuris atrodo kaip " + getName());
        } else {
            player.sendActionMessage("i�sitraukia ginkl� kuris atrodo kaip " + getName() + " i� " + location.getName());
        }
        beingDrawn = false;
        location.remove(this);
        ItemController.getEventManager().dispatchEvent(new PlayerDrawWeaponEvent(player, weapondata, location));
    }
}
