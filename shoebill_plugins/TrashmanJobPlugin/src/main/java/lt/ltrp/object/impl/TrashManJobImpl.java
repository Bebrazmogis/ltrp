package lt.ltrp.object.impl;

import lt.ltrp.AbstractContractJob;
import lt.ltrp.JobProperty;
import lt.ltrp.data.TrashMissions;
import lt.ltrp.object.TrashManJob;import net.gtaun.shoebill.data.Location;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.03.01.
 */
public class TrashManJobImpl extends AbstractContractJob implements TrashManJob {


    @JobProperty("trashmaster_capacity")
    public int trashMasterCapacity;

    @JobProperty("trash_route_bonus")
    public int trashRouteBonus;

    @JobProperty("trash_pickup_bonus")
    public int trashPickupBonus;

    @JobProperty("trash_drop_off")
    public Location trashDropoff;

    private TrashMissions trashMissions;

    public TrashManJobImpl(int id, String name, Location location, int basePaycheck, EventManager eventManager, int contractLength, int maxPaycheck, int minPaycheck, int trashMasterCapacity, int trashRouteBonus) {
        super(id, name, location, basePaycheck, eventManager, contractLength, maxPaycheck, minPaycheck);
        this.trashMasterCapacity = trashMasterCapacity;
        this.trashRouteBonus = trashRouteBonus;
    }

    public TrashManJobImpl(int id, EventManager eventManager) {
        this(id, null, null, 0, eventManager, 0, 0, 0, 0, 0);
    }

    public int getTrashMasterCapacity() {
        return trashMasterCapacity;
    }

    public int getTrashRouteBonus() {
        return trashRouteBonus;
    }

    @Override
    public int getTrashPickupBonus() {
        return trashPickupBonus;
    }

    @Override
    public TrashMissions getMissions() {
        return trashMissions;
    }

    @Override
    public void setMissions(TrashMissions trashMissions) {
        this.trashMissions = trashMissions;
    }

    @Override
    public Location getDropOffLocation() {
        return trashDropoff;
    }
}
