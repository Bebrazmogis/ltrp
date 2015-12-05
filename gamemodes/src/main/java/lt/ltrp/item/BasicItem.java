package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.event.item.ItemLocationChangeEvent;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Property;
import lt.ltrp.vehicle.LtrpVehicle;
import java.sql.*;

/**
 * @author Bebras
 *         2015.11.14.
 */

public class BasicItem extends AbstractItem {



    public BasicItem(String name, ItemType type, boolean stackable) {
        super(name, type, stackable);
    }

    @ItemUsageOption(name = "I�mestii")
    public boolean drop(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            player.getInventory().remove(this);
            player.sendActionMessage("i�meta daikt� kuris atrodo kaip " + getName());
            return true;
        } else {
            return false;
        }
    }

    @ItemUsageOption(name = "Paimti")
    public boolean take(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory() == inventory) {
            return false;
        }
        if(player.getInventory().isFull()) {
            player.sendMessage(Color.LIGHTRED, "J�s� inventorius pilnas, tod�l negalite paimti �io daikto.");
        } else {
            player.getInventory().add(this);
            inventory.remove(this);
            player.sendActionMessage("pa�me " + getName() + " i� " + inventory.getName());
        }
        return true;
    }

    @ItemUsageOption(name = "Pad�ti")
    public boolean place(LtrpPlayer player) {
        if(player.getInventory().contains(this)) {
            LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 2.0f);
            Inventory inventory = null;

            // Pirmiausia ie�kom nekilnojamam turte, nes kai kuriose jo ru�yse gali b�ti viduje transporto priemon�.
            Property property = player.getProperty();
            if(property != null) {
                inventory = property.getInventory();
            }

            if(vehicle != null) {
                inventory = vehicle.getInventory();
            }

            if(inventory != null) {
                if(!inventory.isFull()) {
                    inventory.add(this);
                    player.getInventory().remove(this);
                    player.sendActionMessage("padeda daikt� kuris atrodo kaip " +  getName());
                    ItemController.getEventManager().dispatchEvent(new ItemLocationChangeEvent(this, player.getInventory(), inventory, player));
                } else
                    player.sendErrorMessage(inventory.getName() + " nebegali tur�ti daugiau daikt�.");
            }
            else
                player.sendErrorMessage("Aplink jus n�ra nieko kur b�t� galima �d�ti daikt�.");
        } else
            player.sendErrorMessage(getName() + " n�ra j�s� kuprin�je.");
        return false;
    }

    @ItemUsageOption(name = "Perduoti kitam �aid�jui")
    public boolean giveToPlayer(LtrpPlayer player, Inventory inventory) {
        LtrpPlayer target = player.getClosestPlayer(3.0f);
        if(target == null) {
            player.sendErrorMessage("�alia j�s� n�ra jokio �aid�jo.");
        } else if(player.getInventory() != inventory) {
            player.sendErrorMessage("Negalite perduoti daikt� ne i� savo inventoriaus.");
        } else if(target.getInventory().isFull()) {
            player.sendActionMessage("bando perduodi daikt� kuris atrodo kaip " + getName() + " bet " + target + " neturi kur jo �sid�ti");
        } else {
            player.sendActionMessage("perduoda �alia stovin�iam " + target.getName() + " daikt� " + getName());
            target.getInventory().add(this);
            player.getInventory().remove(this);
            player.applyAnimation("DEALER", "shop_pay", 4.0f, 0, 1, 1, 1, 0, 0);
            return true;
        }
        return false;
    }

    @Override
    protected PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_basic SET `name` = ?, stackable = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_basic (`name`, stackable) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_basic WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }

    protected static BasicItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_basic WHERE id = ?";
        BasicItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new BasicItem(result.getString("name"), type, result.getBoolean("stackable"));
                item.setItemId(itemid);
            }
        }
        return item;
    }

}
