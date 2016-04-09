package lt.ltrp.item.drug.event;

import lt.ltrp.item.drug.DrugItem;
import lt.ltrp.player.object.LtrpPlayer;
import net.gtaun.shoebill.event.player.PlayerEvent;

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
