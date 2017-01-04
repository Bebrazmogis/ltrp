package lt.ltrp.house.weed.event;


import lt.ltrp.event.property.PlayerHouseEvent;
import lt.ltrp.house.weed.object.HouseWeedSapling;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.house.object.House;

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
