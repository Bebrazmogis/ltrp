package lt.ltrp.object;

import lt.maze.streamer.object.DynamicPickup;

/**
 * @author Bebras
 *         2016.06.14.
 */
public interface PickupWeaponShop extends WeaponShop {

    String getText();
    void setText(String text);

    int getModelId();
    void setModelId(int modelId);

    DynamicPickup getPickup();
}
