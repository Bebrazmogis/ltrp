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

    @ItemUsageOption(name = "Iðmestii")
    public boolean drop(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            player.getInventory().remove(this);
            player.sendActionMessage("iðmeta daiktà kuris atrodo kaip " + getName());
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
            player.sendMessage(Color.LIGHTRED, "Jûsø inventorius pilnas, todël negalite paimti ðio daikto.");
        } else {
            player.getInventory().add(this);
            inventory.remove(this);
            player.sendActionMessage("paëme " + getName() + " ið " + inventory.getName());
        }
        return true;
    }

    @ItemUsageOption(name = "Padëti")
    public boolean place(LtrpPlayer player) {
        if(player.getInventory().contains(this)) {
            LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 2.0f);
            Inventory inventory = null;

            // Pirmiausia ieðkom nekilnojamam turte, nes kai kuriose jo ruðyse gali bûti viduje transporto priemonë.
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
                    player.sendActionMessage("padeda daiktà kuris atrodo kaip " +  getName());
                    ItemController.getEventManager().dispatchEvent(new ItemLocationChangeEvent(this, player.getInventory(), inventory, player));
                } else
                    player.sendErrorMessage(inventory.getName() + " nebegali turëti daugiau daiktø.");
            }
            else
                player.sendErrorMessage("Aplink jus nëra nieko kur bûtø galima ádëti daiktà.");
        } else
            player.sendErrorMessage(getName() + " nëra jûsø kuprinëje.");
        return false;
    }

    @ItemUsageOption(name = "Perduoti kitam þaidëjui")
    public boolean giveToPlayer(LtrpPlayer player, Inventory inventory) {
        LtrpPlayer target = player.getClosestPlayer(3.0f);
        if(target == null) {
            player.sendErrorMessage("Ðalia jûsø nëra jokio þaidëjo.");
        } else if(player.getInventory() != inventory) {
            player.sendErrorMessage("Negalite perduoti daiktø ne ið savo inventoriaus.");
        } else if(target.getInventory().isFull()) {
            player.sendActionMessage("bando perduodi daiktà kuris atrodo kaip " + getName() + " bet " + target + " neturi kur jo ásidëti");
        } else {
            player.sendActionMessage("perduoda ðalia stovinèiam " + target.getName() + " daiktà " + getName());
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
