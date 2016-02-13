package lt.ltrp.item;

import lt.ltrp.Util.PawnFunc;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.object.Timer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bebras
 *         2015.12.03.
 */
public class CocaineItem extends DrugItem {

    public CocaineItem(String name, int dosesLeft) {
        super(name, ItemType.Cocaine, dosesLeft);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        player.sendActionMessage("staigiai átraukia kokaino miltelius per nosá.");
        player.setWeather(-68);
        Timer.create(1300, 1, new Timer.TimerCallback() {
            @Override
            public void onTick(int i) {

            }

            @Override
            public void onStop() {
                player.setVarInt("DrugHP", 7);
                player.setVarInt("DrugHPLimit", 70);

                AmxCallable func = PawnFunc.getPublicMethod("DrugEffects");
                if (func != null) {
                    func.call(player.getId());
                }
            }
        });
        return true;
    }


    protected static CocaineItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_consumable WHERE id = ?";
        CocaineItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new CocaineItem(result.getString("name"), result.getInt("doses"));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}
