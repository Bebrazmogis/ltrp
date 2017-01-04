package lt.ltrp.object.impl;

import lt.ltrp.data.WeaponShopWeapon;
import lt.ltrp.dialog.WeaponShopSoldWeaponListDialog;
import lt.ltrp.event.WeaponShopDestroyEvent;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.WeaponShop;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.06.14.
 */
public abstract class AbstractWeaponShop extends EntityImpl implements WeaponShop {


    private String name;
    private Location location;
    private Collection<WeaponShopWeapon> soldWeapons;
    private boolean destroyed;
    protected EventManager eventManager;


    public AbstractWeaponShop(int UUID, String name, Location location, EventManager eventManager) {
        super(UUID);
        this.name = name;
        this.location = location;
        this.soldWeapons = new ArrayList<>();
        this.eventManager = eventManager;
    }

    public AbstractWeaponShop(EventManager eventManager) {
        this.eventManager = eventManager;
        this.soldWeapons = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Collection<WeaponShopWeapon> getSoldWeapons() {
        return soldWeapons;
    }

    @Override
    public int getSoldWeaponCount() {
        return soldWeapons.size();
    }

    @Override
    public boolean isSellingWeapon(WeaponModel model) {
        Optional<WeaponShopWeapon> optionalWep = getSoldWeapons().stream().filter(w -> w.getWeaponModel().equals(model)).findFirst();
        return optionalWep.isPresent();
    }

    @Override
    public WeaponShopWeapon getSoldWeapon(WeaponModel model) {
        Optional<WeaponShopWeapon> optionalWep = getSoldWeapons().stream().filter(w -> w.getWeaponModel().equals(model)).findFirst();
        return optionalWep.isPresent() ? optionalWep.get() : null;
    }

    @Override
    public void addSoldWeapon(WeaponShopWeapon weapon) {
        getSoldWeapons().add(weapon);
    }

    @Override
    public void removeSoldWeapon(WeaponShopWeapon weapon) {
        getSoldWeapons().remove(weapon);
    }

    @Override
    public void showSoldWeaponDialog(LtrpPlayer player) {
        WeaponShopSoldWeaponListDialog.create(player, eventManager, this)
                .show();
    }

    @Override
    protected void finalize() throws Throwable {
        if(!isDestroyed()) destroy();
        super.finalize();
    }

    @Override
    public void destroy() {
        destroyed = true;
        eventManager.dispatchEvent(new WeaponShopDestroyEvent(this));
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
