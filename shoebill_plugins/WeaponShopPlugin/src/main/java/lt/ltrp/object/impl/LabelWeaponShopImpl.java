package lt.ltrp.object.impl;

import lt.ltrp.dialog.LabelWeaponShopManagementDialog;
import lt.ltrp.object.LabelWeaponShop;
import lt.ltrp.object.LtrpPlayer;
import lt.maze.streamer.object.DynamicLabel;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.06.14.
 */
public class LabelWeaponShopImpl extends AbstractWeaponShop implements LabelWeaponShop {

    private String text;
    private Color color;
    private DynamicLabel label;

    public LabelWeaponShopImpl(int UUID, String name, Location location, EventManager eventManager, String text, Color color) {
        super(UUID, name, location, eventManager);
        this.text = text;
        this.color = color;
    }

    private void updateLabel() {
        if(color == null || text == null)
            return;

        if(this.label != null && !label.isDestroyed())
            label.update(color, text);
        else
            this.label = DynamicLabel.create(text, color, getLocation(), 35f);
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        updateLabel();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        updateLabel();
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        this.label.destroy();
        updateLabel();
    }

    @Override
    public void showManagementDialog(LtrpPlayer player) {
        LabelWeaponShopManagementDialog.create(player, eventManager, null, this)
                .show();
    }

    @Override
    public void showManagementDialog(LtrpPlayer player, AbstractDialog parentDialog) {
        LabelWeaponShopManagementDialog.create(player, eventManager, parentDialog, this)
                .show();
    }

    @Override
    public void destroy() {

        super.destroy();
    }

}
