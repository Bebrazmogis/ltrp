package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.object.Timer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 * @author Bebras
 *         2015.11.14.
 */


public class DiceItem extends BasicItem {

    private boolean thrown = false;

    public DiceItem() {
        super("Loðimo kauliukai", ItemType.Dice, false);
    }

    @ItemUsageOption(name = "Mesti")
    public boolean dice(LtrpPlayer player) {
        if(thrown) {
            return false;
        }
        player.sendActionMessage("meta loðimo kauliukus...");
        thrown = true;
        Timer.create(600, 1, e -> {
            player.sendStateMessage("... kauliukai iðsiridena skaièiumi " + (new Random().nextInt(6)+1));
            thrown = false;
            // I could fire off an event, but who cares? :/
        }).start();
        return true;
    }


    protected static DiceItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_basic WHERE id = ?";
        DiceItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new DiceItem();
                item.setItemId(itemid);
            }
        }
        return item;
    }


}
