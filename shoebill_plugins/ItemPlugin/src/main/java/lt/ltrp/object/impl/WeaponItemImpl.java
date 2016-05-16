package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.event.player.PlayerDrawWeaponItemEvent;
import lt.ltrp.object.Inventory;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.WeaponItem;
import lt.ltrp.util.ItemUsageOption;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class WeaponItemImpl extends BasicItem implements WeaponItem {

    private LtrpWeaponData weaponData;
    private boolean beingDrawn = false;
    private Timer drawTimer;

    public WeaponItemImpl(int id, String name, EventManager eventManager, LtrpWeaponData weaponData) {
        super(id, name, eventManager, ItemType.Weapon, false);
        this.weaponData = weaponData;
}

    public WeaponItemImpl(EventManager eventManager, LtrpWeaponData weaponData) {
        this(0, weaponData.getModel().getName(), eventManager, weaponData);
    }

    public LtrpWeaponData getWeaponData() {
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
        if(player.getInventory().equals(inventory)) {
            player.sendActionMessage("bando kaþkà iðsitraukti");
            beingDrawn = true;
            drawTimer = Timer.create(1500 + player.getDrunkLevel()/2, 1, e -> {
                onItemDrawn(player, weaponData, inventory);
            });
        } else {
            player.sendActionMessage("bando kaþkà iðsitraukti");
            beingDrawn = true;
            Timer.create(2300 + player.getDrunkLevel()/2, 1, e -> {
                onItemDrawn(player, weaponData, inventory);
            });
        }
        drawTimer.start();
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
        getEventManager().dispatchEvent(new PlayerDrawWeaponItemEvent(player, location, this));
    }

    @Override
    public void destroy() {
        if(drawTimer != null)
            drawTimer.destroy();
        super.destroy();
    }


}
