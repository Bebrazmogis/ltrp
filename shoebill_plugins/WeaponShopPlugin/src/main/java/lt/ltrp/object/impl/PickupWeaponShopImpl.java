package lt.ltrp.object.impl;

import lt.ltrp.dialog.PickupWeaponShopManagementDialog;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.PickupWeaponShop;
import lt.maze.streamer.object.DynamicPickup;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class PickupWeaponShopImpl extends AbstractWeaponShop implements PickupWeaponShop {

    private String text;
    private int modelId;
    private DynamicPickup pickup;

    public PickupWeaponShopImpl(int UUID, String name, Location location, EventManager eventManager, String text, int modelId) {
        super(UUID, name, location, eventManager);
        this.text = text;
        this.modelId = modelId;
        updatePickup();
    }

    private void updatePickup() {
        if(pickup != null && !pickup.isDestroyed())
            pickup.destroy();
        else
            pickup = DynamicPickup.create(modelId, 1, getLocation());
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int getModelId() {
        return modelId;
    }

    @Override
    public void setModelId(int modelId) {
        this.modelId = modelId;
        updatePickup();
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        updatePickup();
    }

    @Override
    public DynamicPickup getPickup() {
        return pickup;
    }

    @Override
    public void showManagementDialog(LtrpPlayer player) {
        showManagementDialog(player, null);
    }

    @Override
    public void showManagementDialog(LtrpPlayer player, AbstractDialog parentDialog) {
        PickupWeaponShopManagementDialog.create(player, eventManager, parentDialog, this)
                .show();
    }
}
