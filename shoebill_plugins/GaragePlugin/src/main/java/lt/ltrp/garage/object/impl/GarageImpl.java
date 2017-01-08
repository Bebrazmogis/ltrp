package lt.ltrp.garage.object.impl;

import lt.ltrp.event.property.garage.GarageDestroyEvent;
import lt.ltrp.object.*;
import lt.ltrp.property.object.impl.InventoryPropertyImpl;
import lt.maze.streamer.StreamerPlugin;
import lt.maze.streamer.constant.StreamerType;
import lt.maze.streamer.object.DynamicLabel;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2015.11.29.
 */
public class GarageImpl extends InventoryPropertyImpl implements Garage{

    private LtrpVehicle vehicle;
    private AngledLocation vehicleEntrance;
    private AngledLocation vehicleExit;

    public GarageImpl(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit,
                      AngledLocation vehicleEntrance, AngledLocation vehicleExit, Color labelColor, EventManager eventManager) {
        super(id, name, ownerUserId, pickupModelId, price, entrance, exit, labelColor, eventManager);
        this.vehicleEntrance = vehicleEntrance;
        this.vehicleExit = vehicleExit;
        setInventory(Inventory.create(eventManager, this, "Garaþo daiktai", 20));
    }


    public GarageImpl(int id, Location entrance, Location exit, AngledLocation vehicleEntrance, AngledLocation vehicleExit, int price, EventManager eventManager1) {
        this(id, "", LtrpPlayer.INVALID_USER_ID, DEFAULT_PICKUP_MODEL, price, entrance, exit, vehicleEntrance, vehicleExit, Garage.DEFAULT_GARAGE_LABEL_COLOR, eventManager1);
    }


    @Override
    protected void update() {
        if(entranceLabel != null) {
            entranceLabel.destroy();
            entranceLabel = null;
        }
        String text;
        if(!isOwned())  {
            text = String.format("{FFFFFF}Ðis garaþas yra parduodamas\nPardavimo kainà: {FFBB00}%d\n{FFFFFF}Norëdami pirkti raðykite {FFBB00}/buygarage", getPrice());
        } else {
            text = String.format("{FFBB00}Garaþas\nNorëdami áeiti raðykite /enter");
        }
        entranceLabel = DynamicLabel.create(text, getLabelColor(), getEntrance());
        LtrpPlayer.Companion.get().stream().filter(entranceLabel::isVisible).forEach(p -> StreamerPlugin.getInstance().update(p, getEntrance(), StreamerType.Label));
    }


    @Override
    public void setVehicle(LtrpVehicle ltrpVehicle) {
        vehicle = ltrpVehicle;
    }

    @Override
    public LtrpVehicle getVehicle() {
        return vehicle;
    }

    @Override
    public AngledLocation getVehicleEntrance() {
        return vehicleEntrance;
    }

    @Override
    public AngledLocation getVehicleExit() {
        return vehicleExit;
    }

    @Override
    public void setVehicleEntrance(AngledLocation angledLocation) {
        this.vehicleEntrance = angledLocation;
    }

    @Override
    public void setVehicleExit(AngledLocation angledLocation) {
        this.vehicleExit = angledLocation;
    }

    @Override
    public void destroy() {
        super.destroy();
        eventManager.dispatchEvent(new GarageDestroyEvent(this));
    }
}
