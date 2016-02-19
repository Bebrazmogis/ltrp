package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.constant.WeaponModel;
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

    public FixedSizeInventory(String name, Item[] items, int size) {
        this.size = size;
        this.name = name;
        this.items = items;
        this.itemCount = items.length;
    }

    public FixedSizeInventory(String name) {
        this.name = name;
        if(this.name == null || this.name.equals("")) {
            this.name = " ";
        }
        this.items = new Item[DEFAULT_SIZE];
        this.size = DEFAULT_SIZE;
        this.itemCount = 0;
    }


    @Override
    public void add(Item item) {
        tryAdd(item);
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
                break;
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
        for(int i = 0; i < itemCount; i++)
            if(items[i] == item) {
                for(int j = i; j < itemCount-1; j++) {
                    items[j] = items[j+1];
                    break;
                }
                itemCount--;
                break;
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
        return (Item[]) items.toArray();
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
    public void show(LtrpPlayer player) {
        logger.debug("showing for " + player.getUserId() + " item count:" + itemCount);
        List<ListDialogItem> dialogItems = new ArrayList<>();
        if(itemCount == 0) {
            ListDialog dialog = ListDialog.create(player, ItemController.getInstance().getEventManager())
                    .caption(getName())
                    .item("{FF0000}Daiktø nëra")
                    .buttonOk("Gerai")
                    .buttonCancel("Iðeiti")
                    .build();
            dialog.show();
        } else {
            for(int i = 0; i < itemCount; i++) {
                if(items[i].isDestroyed()) {
                    remove(i);
                    i--;
                    continue;
                }

                ListDialogItem item = new ListDialogItem();
                item.setItemText(items[i].getName());
                item.setData(items[i]);
                dialogItems.add(item);
            }

            ListDialog.create(player, ItemController.getInstance().getEventManager())
                    .caption(getName())
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Iðeiti")
                    .items(dialogItems)
                    .onClickOk((dialog, dialogItem) -> {
                        Item item = (Item)dialogItem.getData();
                        player.sendMessage(Color.AQUA, "Pasirinkai daiktà " + item + " jo tipas:" + item.getClass().getName());
                        item.showOptions(player, this, dialog);
                    })
                    .build()
                    .show();
        }
    }
}
