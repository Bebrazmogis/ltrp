package lt.ltrp.property.`object`.impl

import lt.ltrp.ActionMessenger
import lt.ltrp.StateMessenger
import lt.ltrp.`object`.Entity
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.property.`object`.Property
import lt.ltrp.`object`.impl.NamedEntityImpl
import lt.ltrp.constant.ACTION
import lt.ltrp.property.event.PropertyDestroyEvent

import net.gtaun.shoebill.data.Location
import net.gtaun.util.event.EventManager


/**
 * @author Bebras
 *         2015.11.29.
 */


abstract class AbstractProperty(uuid: Int, name: String,
                                override var entrance: Location,
                                protected var eventManager: EventManager) :
        NamedEntityImpl(uuid, name), Property {

    override var exit: Location? = null
    override var price: Int = 0
    override var isLocked = false
    override var ownerUUID = Entity.INVALID_ID

    init {
        Property.propertyList.add(this)
    }



    override fun isOwner(player: LtrpPlayer): Boolean {
        return isOwned && ownerUUID == player.UUID
    }

    override fun isInside(player: LtrpPlayer): Boolean {
        return exit != null
                && exit!!.interiorId == player.location.interiorId
                && exit!!.worldId == player.location.worldId
    }

    /**
     * Sends the message to everyone inside the property and everyone closer than #radius from entrance
     * For sending messages inside it ignores the radius
     */
    override fun sendActionMessage(action: String, radius: Float) {
        val text = "* $name $action"
        LtrpPlayer.Companion.get()
                .filter { isInside(it) }
                .forEach { it.sendMessage(ActionMessenger.DEFAULT_COLOR, text) }
        LtrpPlayer.Companion.get()
                .filter { it.location.distance(entrance) < radius }
                .forEach { it.sendFadeMessage(ActionMessenger.DEFAULT_COLOR, text, entrance) }
    }


    override fun sendStateMessage(state: String, radius: Float) {
        val text = "* $state (( $name ))"
        LtrpPlayer.Companion.get()
                .filter { isInside(it) }
                .forEach { it.sendMessage(StateMessenger.COLOR, text) }
        LtrpPlayer.Companion.get()
                .filter { it.location.distance(entrance) < radius }
                .forEach { it.sendFadeMessage(StateMessenger.COLOR, text, entrance) }
    }

    protected fun finalize() {
        if(!isDestroyed)
            destroy()
        eventManager.dispatchEvent(PropertyDestroyEvent(this))
    }
}
/*
public abstract class AbstractProperty extends NamedEntityImpl implements Property {

    private int ownerUserId;
    private int pickupModelId;
    private int price;
    private Location entrance, exit;
    protected DynamicLabel entranceLabel;
    protected DynamicPickup pickup;
    private Color labelColor;
    private boolean destroyed;
    private boolean locked;
    protected EventManager eventManager;


    public AbstractProperty(int id, String name, int ownerUserId, int pickupModelId, int price, Location entrance, Location exit, Color labelColor, EventManager eventManager) {
        super(id, name);
        this.ownerUserId = ownerUserId;
        this.pickupModelId = pickupModelId;
        this.price = price;
        this.entrance = entrance;
        this.exit = exit;
        if(labelColor != null)
            this.labelColor = labelColor;
        else
            this.labelColor = new Color();
        this.eventManager = eventManager;
        update();
    }

    protected abstract void update();

    @Override
    public void setLocked(boolean b) {
        locked = b;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public Color getLabelColor() {
        return labelColor;
    }

    @Override
    public void setLabelColor(Color color) {
        this.labelColor = color;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
        update();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        update();
    }

    public int getPickupModelId() {
        return pickupModelId;
    }

    public void setPickupModelId(int pickupModelId) {
        this.pickupModelId = pickupModelId;
        update();
    }

    @Override
    public DynamicPickup getPickup() {
        return pickup;
    }

    public void setColor(Color color) {
        labelColor = color;
        update();
    }

    public int getOwner() {
        return ownerUserId;
    }

    public void setOwner(int ownerUserId) {
        this.ownerUserId = ownerUserId;
        update();
    }

    public Location getExit() {
        return exit;
    }

    public void setExit(Location exit) {
        this.exit = exit;
        update();
    }

    public Location getEntrance() {
        return entrance;
    }

    public void setEntrance(Location entrance) {
        this.entrance = entrance;
        update();
    }

    public void sendActionMessage(String s) {
        LtrpPlayer.Companion.get().stream().filter(p -> Property.get(p).equals(this)).forEach(p -> {
            p.sendMessage(lt.ltrp.data.Color.ACTION, "* " + this.getName() + " " + s);
        });
    }

    public void sendStateMessage(String s) {
        LtrpPlayer.Companion.get().stream().filter(p -> Property.get(p).equals(this)).forEach(p -> {
            p.sendMessage(lt.ltrp.data.Color.ACTION, "* " + s + " ((" + this.getName() + "))");
        });
    }

    public boolean isOwner(LtrpPlayer player) {
        return player.getUUID() == getOwner();
    }

    @Override
    public boolean isOwned() {
        return getOwner() != LtrpPlayer.INVALID_USER_ID;
    }

    @Override
    public void destroy() {
        destroyed = true;
        eventManager.dispatchEvent(new PropertyDestroyEvent(this));
        if(entranceLabel != null) entranceLabel.destroy();
        if(pickup != null) pickup.destroy();
    }

    @Override
    protected void finalize() throws Throwable {
        if(!isDestroyed()) destroy();
        super.finalize();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}

*/
