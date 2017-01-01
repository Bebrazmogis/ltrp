package lt.ltrp.player.event;


import lt.ltrp.object.drug.DrugItem;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2016.04.06.
 */
public class PlayerUseDrugsEvent extends PlayerEvent {

    private Class<? extends DrugItem> drugType;

    public PlayerUseDrugsEvent(LtrpPlayer player, Class<? extends DrugItem> drugType) {
        super(player);
        this.drugType = drugType;
    }

    public Class<? extends DrugItem> getDrugType() {
        return drugType;
    }

    @Override
    public LtrpPlayer getPlayer() {
        return (LtrpPlayer)super.getPlayer();
    }
}
