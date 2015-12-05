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
public class MetaamphetamineItem extends DrugItem {


    public MetaamphetamineItem(String name, int dosesLeft) {
        super(name, ItemType.MetaAmphetamine, dosesLeft);
    }


    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        player.setWeather(141);


        Timer.create(1300, 1, new Timer.TimerCallback() {
            @Override
            public void onTick(int i) {

            }
            @Override
            public void onStop() {
                player.setVarInt("DrugHP", 10);
                player.setVarInt("DrugHPLimit", 50);

                AmxCallable func = PawnFunc.getNativeMethod("DrugEffects");
                if(func != null) {
                    func.call(player.getId());
                }
            }
        });
        return true;
    }


    protected static MetaamphetamineItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_consumable WHERE id = ?";
        MetaamphetamineItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new MetaamphetamineItem(result.getString("name"), result.getInt("doses"));
                item.setItemId(itemid);
            }
        }
        return item;
    }

}
