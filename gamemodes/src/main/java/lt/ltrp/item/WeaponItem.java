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
                player.sendActionMessage("iðmeta ginklà kuris atrodo kaip " + getName());
                return true;
            } else
                player.sendErrorMessage("Jûs esate komos bûsenoje!");
        } else
            player.sendErrorMessage("Negalite iðmesti darbinio ginklo.");
        return false;
    }

    @ItemUsageOption(name = "Iðsitraukti")
    public boolean drawWeapon(LtrpPlayer player, Inventory inventory) {
        if(beingDrawn) {
            return false;
        }
        if(player.getInventory() == inventory) {
            player.sendActionMessage("bando kaþkà iðsitraukti");
            beingDrawn = true;
            Timer.create(1500 + player.getDrunkLevel(), 1, e -> {
                onItemDrawn(player, weaponData, inventory);
            });
        } else {
            player.sendActionMessage("bando kaþkà iðsitraukti");
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
            player.sendActionMessage("iðsitraukia ginklà kuris atrodo kaip " + getName());
        } else {
            player.sendActionMessage("iðsitraukia ginklà kuris atrodo kaip " + getName() + " ið " + location.getName());
        }
        beingDrawn = false;
        location.remove(this);
        ItemController.getEventManager().dispatchEvent(new PlayerDrawWeaponEvent(player, weapondata, location));
    }
}
