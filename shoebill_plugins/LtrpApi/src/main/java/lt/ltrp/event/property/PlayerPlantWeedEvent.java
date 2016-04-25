package lt.ltrp.event.property;


import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.data.HouseWeedSapling;
import lt.ltrp.object.House;

/**
 * @author Bebras
 *         2016.03.30.
 */
public class PlayerPlantWeedEvent extends PlayerHouseEvent {

    private HouseWeedSapling weedSapling;

    public PlayerPlantWeedEvent(LtrpPlayer player, House house, HouseWeedSapling sapling) {
        super(player, house);
        this.weedSapling = sapling;
    }

    public HouseWeedSapling getWeedSapling() {
        return weedSapling;
    }
}
