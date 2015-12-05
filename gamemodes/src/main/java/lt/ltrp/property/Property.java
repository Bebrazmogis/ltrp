package lt.ltrp.property;

import lt.ltrp.item.Inventory;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Label;
import net.gtaun.shoebill.object.Pickup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.29.
 */
public abstract class Property implements Destroyable {

    private static List<Property> propertyList = new ArrayList<>();

    public static List<Property> get() {
        return propertyList;
    }


    public int uid;
    private int ownerUserId;
    private String name;
    private Location entrance, exit;
    private Label label;
    private Pickup pickup;
    private Inventory inventory;
    private boolean destroyed;

    public Property(int uniqueid, String name) {
        this.uid = uniqueid;
        this.name = name;
        propertyList.add(this);
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Location getExit() {
        return exit;
    }

    public void setExit(Location exit) {
        this.exit = exit;
    }

    public Location getEntrance() {
        return entrance;
    }

    public void setEntrance(Location entrance) {
        this.entrance = entrance;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void sendActionMessage(String s) {
        for(LtrpPlayer p : LtrpPlayer.get()) {
            if(p.getProperty() == this) {
                p.sendMessage(lt.ltrp.data.Color.ACTION, "* "+ this.getName() + " " + s);
            }
        }
    }

    public void sendStateMessage(String s) {
        for(LtrpPlayer p : LtrpPlayer.get()) {
            if(p.getProperty() == this) {
                p.sendMessage(lt.ltrp.data.Color.ACTION, "* " + s + " ((" + this.getName() + "))");
            }
        }
    }

    public boolean isOwner(LtrpPlayer player) {
        return player.getUserId() == getOwnerUserId();
    }


    @Override
    public void destroy() {

    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
