package lt.ltrp.event;


import lt.ltrp.event.player.PlayerEvent;
import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.AngledLocation;

/**
 * @author Bebras
 *         2016.03.16.
 */
public class PlayerBuyNewVehicleEvent extends PlayerEvent {

    private int modelId;
    private AngledLocation spawnLocation;
    private int color1, color2;
    private int price;


    public PlayerBuyNewVehicleEvent(LtrpPlayer player, int modelId, AngledLocation spawnLocation, int color1, int color2, int price) {
        super(player);
        this.modelId = modelId;
        this.spawnLocation = spawnLocation;
        this.color1 = color1;
        this.color2 = color2;
        this.price = price;
    }


    public int getModelId() {
        return modelId;
    }

    public AngledLocation getSpawnLocation() {
        return spawnLocation;
    }

    public int getColor1() {
        return color1;
    }

    public int getColor2() {
        return color2;
    }

    public int getPrice() {
        return price;
    }
}
