package lt.ltrp.property.event;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.property.data.HouseWeedSapling;
import lt.ltrp.property.object.House;

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
