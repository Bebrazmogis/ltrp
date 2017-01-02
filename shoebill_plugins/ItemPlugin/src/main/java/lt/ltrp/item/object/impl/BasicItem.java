package lt.ltrp.object.impl;


import lt.ltrp.data.Color;
import lt.ltrp.constant.ItemType;
import lt.ltrp.event.item.ItemLocationChangeEvent;
import lt.ltrp.util.ItemUsageEnabler;
import lt.ltrp.util.ItemUsageOption;import lt.ltrp.object.Inventory;
import lt.ltrp.object.InventoryEntity;
import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.object.Property;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.util.event.EventManager;

import java.util.function.Supplier;

/**
 * @author Bebras
 *         2015.11.14.
 *
 *         The mother of all items
 */

public class BasicItem extends AbstractItem {

    private static final String OPTION_DROP = "Iðmesti";
    private static final String OPTION_TAKE = "Paimti";
    private static final String OPTION_PLACE = "Padëti";
    private static final String OPTION_GIVE_TO_PLAYER = "Perduoti kitam þaidëjui";

    public BasicItem(int id, String name, EventManager eventManager, ItemType type, boolean stackable) {
        super(id, name, eventManager, type, stackable);
    }

    @ItemUsageOption(name = OPTION_DROP, order = 20)
    public boolean drop(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory().equals(inventory)) {
            player.getInventory().remove(this);
            player.sendActionMessage("iðmeta daiktà kuris atrodo kaip " + getName());
            //getEventManager().dispatchEvent(new PlayerDropItemEvent(player, this));
            return true;
        } else {
            return false;
        }
    }

    @ItemUsageOption(name = OPTION_TAKE)
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
            getEventManager().dispatchEvent(new ItemLocationChangeEvent(this, inventory, player.getInventory(), player));
        }
        return true;
    }

    @ItemUsageOption(name = OPTION_PLACE)
    public boolean place(LtrpPlayer player) {
        if(player.getInventory().contains(this)) {
            LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
            Inventory inventory = null;

            // Pirmiausia ieðkom nekilnojamam turte, nes kai kuriose jo ruðyse gali bûti viduje transporto priemonë.
            Property property = Property.get(player);
            if(property != null && property instanceof InventoryEntity) {
                inventory = ((InventoryEntity)property).getInventory();
            }

            if(vehicle != null) {
                inventory = vehicle.getInventory();
            }

            if(inventory != null) {
                if(!inventory.isFull()) {
                    inventory.add(this);
                    player.getInventory().remove(this);
                    player.sendActionMessage("padeda daiktà kuris atrodo kaip " +  getName());
                    getEventManager().dispatchEvent(new ItemLocationChangeEvent(this, player.getInventory(), inventory, player));
                } else
                    player.sendErrorMessage(inventory.getName() + " nebegali turëti daugiau daiktø.");
            }
            else
                player.sendErrorMessage("Aplink jus nëra nieko kur bûtø galima ádëti daiktà.");
        } else
            player.sendErrorMessage(getName() + " nëra jûsø kuprinëje.");
        return false;
    }

    @ItemUsageOption(name = OPTION_GIVE_TO_PLAYER, order = 5f)
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
            getEventManager().dispatchEvent(new ItemLocationChangeEvent(this, player.getInventory(), target.getInventory(), player));
            return true;
        }
        return false;
    }

    /**
     * Determines whether certain {@link lt.ltrp.util.ItemUsageOption} will be enabled
     * Child classes should always call super.isEnabled unless they want to disable parenet options
     * @param itemText - item text of the option
     * @param player - player for whom it should be enabled/disabled
     * @param inventory - relevant inventory
     * @return returns true if the option should be displayed, false otherwise
     */
    @ItemUsageEnabler
    public Supplier<Boolean> isEnabled(String itemText, LtrpPlayer player, Inventory inventory) {
        switch(itemText) {
            case OPTION_PLACE:
                return () -> {
                    Inventory inv = null;

                    // Pirmiausia ieðkom nekilnojamam turte, nes kai kuriose jo ruðyse gali bûti viduje transporto priemonë.
                    Property property = Property.get(player);
                    if(property != null && property instanceof InventoryEntity) {
                        inv = ((InventoryEntity)property).getInventory();
                    }
                    LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
                    if (vehicle != null) {
                        inv = vehicle.getInventory();
                    }
                    return inv != null;
                };
            case OPTION_TAKE:
                return () -> player.getInventory() != inventory;
            case OPTION_GIVE_TO_PLAYER:
                return () -> player.getClosestPlayer(3.0f) != null;
        }
        return null;
    }
/*
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
    */

}
