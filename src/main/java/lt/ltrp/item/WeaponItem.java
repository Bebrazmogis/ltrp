package lt.ltrp.item;

import lt.ltrp.item.event.PlayerDrawWeaponItemEvent;
import lt.ltrp.player.data.LtrpWeaponData;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class WeaponItem extends BasicItem {

    private LtrpWeaponData weaponData;
    private boolean beingDrawn = false;
    private Timer drawTimer;

    public WeaponItem(int id, String name, EventManager eventManager, LtrpWeaponData weaponData) {
        super(id, name, eventManager, ItemType.Weapon, false);
        this.weaponData = weaponData;
}

    public WeaponItem(EventManager eventManager, LtrpWeaponData weaponData) {
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
        if(player.getInventory().equals(inventory)) {
            player.sendActionMessage("bando ka�k� i�sitraukti");
            beingDrawn = true;
            drawTimer = Timer.create(1500 + player.getDrunkLevel()/2, 1, e -> {
                onItemDrawn(player, weaponData, inventory);
            });
        } else {
            player.sendActionMessage("bando ka�k� i�sitraukti");
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
            player.sendActionMessage("i�sitraukia ginkl� kuris atrodo kaip " + getName());
        } else {
            player.sendActionMessage("i�sitraukia ginkl� kuris atrodo kaip " + getName() + " i� " + location.getName());
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
