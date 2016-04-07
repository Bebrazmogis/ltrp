package lt.ltrp.property.event;

import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.House;
import lt.ltrp.property.HouseWeedSapling;

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
