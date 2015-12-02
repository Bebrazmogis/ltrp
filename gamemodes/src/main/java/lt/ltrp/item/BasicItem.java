package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.event.item.ItemLocationChangeEvent;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.property.Property;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.VehicleRelated;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class BasicItem implements Item {

    private String name;
    private int id;
    private boolean isDestroyed;
    private ItemType type;
    private boolean stackable;
    private int amount;

    public BasicItem(String name, int id, ItemType type, boolean stackable) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.stackable = stackable;
        this.amount = 0;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ItemType getType() {
        return type;
    }

    @Override
    public boolean isStackable() {
        return stackable;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
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
    public void destroy() {
        isDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}
