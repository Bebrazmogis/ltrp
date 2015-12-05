package lt.ltrp.item;

import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.constant.SpecialAction;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.util.event.HandlerEntry;
import net.gtaun.util.event.HandlerPriority;

import java.sql.*;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class DrinkItem extends ConsumableItem {

    private SpecialAction specialAction;
    private HandlerEntry keyStateEvent;

    public DrinkItem(String name, ItemType type, int dosesLeft, SpecialAction action) {
        super(name, type, dosesLeft, false);
        this.specialAction = action;
    }

    @Override
    public boolean use(LtrpPlayer player, Inventory inventory) {
        if(getDosesLeft() == 0) {
            inventory.remove(this);
        } else {
            if(player.getInventory() != inventory) {
                if(player.getInventory().isFull()) {
                    player.sendMessage(Color.LIGHTRED, "Negalite pasiimti ðio daikto, jûsø kuprinë pilna");
                    return false;
                } else {
                    player.getInventory().add(this);
                    inventory.remove(this);
                    inventory = player.getInventory();
                }
            }
            if(player.getSpecialAction() != SpecialAction.NONE) {
                player.sendMessage(Color.LIGHTRED, "Jûs jau kaþkà darote.");
            } else {
                final Inventory inv = inventory;
                player.setSpecialAction(SpecialAction.DRINK_BEER);
                keyStateEvent = ItemController.getEventManager().registerHandler(PlayerKeyStateChangeEvent.class, HandlerPriority.LOW, e-> {
                   if(e.getOldState().isKeyPressed(PlayerKey.FIRE)) {
                       setDosesLeft(getDosesLeft()-1);
                       if(getDosesLeft() == 0) {
                           keyStateEvent.cancel();
                           inv.remove(this);
                           player.setSpecialAction(SpecialAction.NONE);
                       }
                   }
                });
                return true;
            }
        }
        return false;
    }

    public SpecialAction getSpecialAction() {
        return specialAction;
    }

    public void setSpecialAction(SpecialAction specialAction) {
        this.specialAction = specialAction;
    }

    @Override
    public void destroy() {
        if(keyStateEvent != null) {
            keyStateEvent.cancel();
        }
        super.destroy();
    }


    @Override
    protected PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_drink SET `name` = ?, stackable = ?, doses = ?, special_action = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getDosesLeft());
        stmt.setInt(4, getSpecialAction().getValue());
        stmt.setInt(5, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_drink (`name`, stackable, doses, special_action) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getDosesLeft());
        stmt.setInt(4, getSpecialAction().getValue());
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_drink WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }


    protected static DrinkItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_drink WHERE id = ?";
        DrinkItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new DrinkItem(result.getString("name"), type, result.getInt("doses"), SpecialAction.get(result.getInt("speical_action")));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}
