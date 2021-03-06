package lt.ltrp.object.impl;

import lt.ltrp.constant.ItemType;
import lt.ltrp.object.*;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class FixedSizeInventory implements Inventory {

    private static final Logger logger = LoggerFactory.getLogger(FixedSizeInventory.class);

    private static final int DEFAULT_SIZE = 10;

    private int size;
    private String name;
    private Item[] items;
    private int itemCount;
    private InventoryEntity entity;
    private EventManager eventManager;

    public FixedSizeInventory(EventManager eventManager, String name, int size, InventoryEntity entity) {
        this.eventManager = eventManager;
        this.size = size;
        this.name = name;
        this.items = new Item[size];
        this.itemCount = 0;
        this.entity = entity;
    }

    public FixedSizeInventory(EventManager eventManager, String name, InventoryEntity entity) {
        this.eventManager = eventManager;
        this.name = name;
        if(this.name == null || this.name.equals("")) {
            this.name = " ";
        }
        this.items = new Item[DEFAULT_SIZE];
        this.size = DEFAULT_SIZE;
        this.itemCount = 0;
        this.entity = entity;
    }


    @Override
    public void add(Item item) {
        boolean success = tryAdd(item);
        if(entity instanceof LtrpPlayer)
            ((LtrpPlayer) entity).sendDebug(success);
    }

    @Override
    public boolean tryAdd(Item item) {
        logger.debug("FixedSizeInventory :: tryAdd. ItemCount:" + itemCount);
        if(item.isStackable()) {
            for(int i = 0; i < itemCount; i++) {
                if(items[i].getType() == item.getType()) {
                    items[i].setAmount(items[i].getAmount() + item.getAmount());
                    System.out.println("It was stackable, adding");
                    return true;
                }
            }
        }
        // Maybe we can replace a destroyed item
        for(int i = 0; i < items.length; i++) {
            if(items[i] != null && items[i].isDestroyed()) {
                items[i] = item;
                return true;
            }
        }
        if(itemCount != size) {
            items[itemCount++] = item;
            return true;
        }
        return false;
    }

    @Override
    public void add(Item[] items) {
        for(Item item : items) {
            tryAdd(item);
        }
    }

    @Override
    public void remove(Item item) {
        for(int i = 0; i < itemCount; i++) {
            if(items[i].equals(item)) {
                for(int j = i; j < itemCount-1; j++) {
                    System.out.println("Moving from " + (j+1) + " to " + j);
                    items[j] = items[j+1];
                }
                itemCount--;
                break;
            }
        }
    }

    @Override
    public void remove(int index) {
        if(index > 0 && index < itemCount) {
            for(int i = index; i < itemCount-1; i++) {
                items[i] = items[i+1];
            }
            itemCount--;

        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void clear() {
        itemCount = 0;
    }

    @Override
    public boolean contains(Item item) {
        for(int i = 0; i < itemCount; i++)
            if(item.equals(items[i]))
                return true;
        return false;
    }

    @Override
    public boolean containsType(ItemType type) {
        for(int i = 0; i < itemCount; i++)
            if(type == items[i].getType())
                return true;
        return false;
    }

    @Override
    public boolean containsWeapon(WeaponModel model) {
        for(int i = 0; i < itemCount; i++) {
            if(items[i].getType().equals(ItemType.Weapon) && ((WeaponItem)items[i]).getWeaponData().getModel().equals(model)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean isFull() {
        return size == itemCount;
    }

    @Override
    public boolean isEmpty() {
        return itemCount == 0;
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    @Override
    public Item[] getItems() {
        Item[] items = new Item[itemCount];
        System.arraycopy(this.items, 0, items, 0, itemCount);
        return items;
    }

    @Override
    public Item[] getItems(ItemType type) {
        List<Item> items = new ArrayList<>();
        for(int i = 0; i < itemCount; i++) {
            if(!this.items[i].isDestroyed() && this.items[i].getType() == type)  {
                items.add(this.items[i]);
            }
        }
        return items.toArray(new Item[0]);
    }

    @Override
    public <T> T[] getItems(Class<T> t) {
        List<T> items = new ArrayList<>();
        for(int i = 0; i < itemCount; i++) {
            if(!this.items[i].isDestroyed() && this.items[i].getClass() == t)  {
                    items.add((T)this.items[i]);
            }
        }
        return (T[])items.toArray();
    }

    @Override
    public Item getItem(ItemType type) {
        for(int i = 0; i < itemCount; i++) {
            if(items[i].getType() == type) {
                return items[i];
            }
        }
        return null;
    }

    @Override
    public void show(Player player) {
        logger.debug("showing for " + ((LtrpPlayer)player).getUUID() + " item count:" + itemCount);
        List<ListDialogItem> dialogItems = new ArrayList<>();
        if(itemCount == 0) {
            ListDialog dialog = ListDialog.create(player, eventManager)
                    .caption(getName())
                    .item("{FF0000}Daikt� n�ra")
                    .buttonOk("Gerai")
                    .buttonCancel("I�eiti")
                    .build();
            dialog.show();
        } else {
            for(int i = 0; i < itemCount; i++) {
                if(items[i].isDestroyed()) {
                    remove(i);
                    i--;
                    continue;
                }
                logger.debug("adding item: "+ items[i].getName());
                ListDialogItem item = new ListDialogItem();
                item.setItemText(items[i].getName());
                item.setData(items[i]);
                dialogItems.add(item);
            }

            ListDialog.create(player, eventManager)
                    .caption(getName())
                    .buttonOk("Pasirinkti")
                    .buttonCancel("I�eiti")
                    .items(dialogItems)
                    .onClickOk((dialog, dialogItem) -> {
                        Item item = (Item)dialogItem.getData();
                        player.sendMessage(Color.AQUA, "Pasirinkai daikt� " + item + " jo tipas:" + item.getClass().getName());
                        item.showOptions(player, this, dialog);
                    })
                    .build()
                    .show();
        }
    }

    @Override
    public InventoryEntity getEntity() {
        return entity;
    }
}
