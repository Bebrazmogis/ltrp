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
public class HeroinItem extends DrugItem {

    public HeroinItem(String name, int dosesLeft) {
        super(name, ItemType.Heroin, dosesLeft);
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(!inventory.containsType(ItemType.Syringe) && !player.getInventory().containsType(ItemType.Syringe)) {
            player.sendErrorMessage("Jûs neturite ðvirkðto");
            return false;
        } else {
            super.use(player, inventory);
            Item syringe = inventory.getItem(ItemType.Syringe);
            if(syringe == null) {
                syringe = player.getInventory().getItem(ItemType.Syringe);
            }
            player.setWeather(-64);
            syringe.setAmount(syringe.getAmount()-1);
            player.sendActionMessage("pasiemæs ðvirkstà ástato já á venà ant rankos ir susileidþia heroinà.");

            Timer.create(1300, 1, new Timer.TimerCallback() {
                @Override
                public void onTick(int i) {

                }

                @Override
                public void onStop() {
                    player.setVarInt("DrugHP", 5);
                    player.setVarInt("DrugHPLimit", 65);

                    AmxCallable func = PawnFunc.getNativeMethod("DrugEffects");
                    if (func != null) {
                        func.call(player.getId());
                    }
                }
            });
            return true;
        }
    }

    protected static HeroinItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_consumable WHERE id = ?";
        HeroinItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new HeroinItem(result.getString("name"), result.getInt("doses"));
                item.setItemId(itemid);
            }
        }
        return item;
    }

}
