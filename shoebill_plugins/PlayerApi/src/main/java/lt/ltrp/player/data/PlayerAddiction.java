package lt.ltrp.player.data;



import lt.ltrp.item.object.drug.DrugItem;
import lt.ltrp.player.object.LtrpPlayer;

import java.sql.Timestamp;

/**
 * @author Bebras
 *         2016.04.05.
 */
public class PlayerAddiction {

    private Class<? extends DrugItem> type;
    private int level;
    private Timestamp lastDose;
    private LtrpPlayer player;

    public PlayerAddiction(LtrpPlayer player, Class<? extends DrugItem> type, int level, Timestamp lastDose) {
        this.player = player;
        this.type = type;
        this.level = level;
        this.lastDose = lastDose;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public Class<? extends DrugItem> getType() {
        return type;
    }

    public void setType(Class<? extends DrugItem> type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Timestamp getLastDose() {
        return lastDose;
    }

    public void setLastDose(Timestamp lastDose) {
        this.lastDose = lastDose;
    }
}
