package lt.ltrp.object.impl;


import lt.ltrp.constant.ItemType;
import lt.ltrp.data.Color;
import lt.ltrp.event.PlayerSelectItemOptionEvent;
import lt.ltrp.event.item.ItemDestroyEvent;
import lt.ltrp.object.Inventory;
import lt.ltrp.object.Item;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.util.ItemUsageEnabler;
import lt.ltrp.util.ItemUsageOption;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * @author Bebras
 *         2015.12.04.
 */
public abstract class AbstractItem extends NamedEntityImpl implements Item {

    protected static Logger logger = null;

    private boolean isDestroyed;
    private ItemType type;
    private boolean stackable;
    private int amount;
    private EventManager eventManager;

    public AbstractItem(int id, String name, EventManager eventManager, ItemType type, boolean stackable) {
        super(id, name);
        if(logger == null) {
            logger =  LoggerFactory.getLogger(this.getClass());
        }
        this.type = type;
        this.stackable = stackable;
        this.amount = 0;
        this.eventManager = eventManager;
    }

    @Override
    public void showOptions(Player player, Inventory inventory, AbstractDialog parentDialog) {
        logger.debug("showOptions called. player uid=" + ((LtrpPlayer)player).getUUID() + " inventory name=" + inventory.getName());
        ListDialog listDialog = ListDialog.create(player, getEventManager()).build();
        listDialog.setCaption(getName() + " parinktys");
        listDialog.setButtonOk("Pasirinkti");
        listDialog.setButtonCancel("Atgal");
        listDialog.setClickCancelHandler((d) -> parentDialog.show());
        listDialog.setClickOkHandler((dialog, dialogitem) -> {
            logger.debug("showOptions player-stats item selected. item="+ dialogitem.getItemText());
            Method m = (Method)dialogitem.getData();
            logger.debug("showOptions selected item method=" + m.getName());
            try {
                if(m.getParameterCount() == 1) {
                    m.invoke(this, player);
                } else {
                    m.invoke(this, player, inventory);
                }
            } catch(IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                // This might happen whenever item action is implemented outside this package
            } catch(NotImplementedException e) {
                logger.info("Method " + m.getName() + " not implemented for " + getClass().getName() + "(player=" + ((LtrpPlayer) player).getUUID());
            }
            LtrpPlayer p = LtrpPlayer.get(player);
            // Should not be a problem, but Kotlin
            if(p != null)
                eventManager.dispatchEvent(new PlayerSelectItemOptionEvent(p, this, dialogitem.getItemText(), m.getName()));
        });

        Method enableSupplier = null;
        for(Method m : getClass().getMethods()) {
            if(m.isAnnotationPresent(ItemUsageEnabler.class)) {
                enableSupplier = m;
                break;
            }
        }
        TreeMap<Float, List<ListDialogItem>> listDialogItems = new TreeMap<>();
        for(Method method : this.getClass().getMethods()) {
            // If so, method is an option
            System.out.println(method.getName() + " annotation? "+ method.isAnnotationPresent(ItemUsageOption.class));
            if(method.isAnnotationPresent(ItemUsageOption.class)) {
                ItemUsageOption itemUsageAnnotation = method.getAnnotation(ItemUsageOption.class);
                // if the class contains an enable supplier, we need to check if it allows the option to be enabled(visible)
                if(enableSupplier != null && enableSupplier.getReturnType() == Supplier.class) {
                    try {
                        Supplier<Boolean> supplier = null;
                        // It may actually contain both, i think
                        if(enableSupplier.getParameterCount() == 1) {
                            supplier = (Supplier<Boolean>)enableSupplier.invoke(this, itemUsageAnnotation.name());
                        }
                        if(enableSupplier.getParameterCount() == 3) {
                            supplier = (Supplier<Boolean>)enableSupplier.invoke(this, itemUsageAnnotation.name(), player, inventory);
                        }
                        System.out.println(method.getName() + " supplier?" + (supplier != null ? supplier.get() : "null"));
                        // If it is null, we consider it enabled
                        if(supplier != null && !supplier.get())
                            continue;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        logger.error("Could not call ItemUsageEnabler method " + enableSupplier.getName());
                    }
                }
                ListDialogItem listDialogItem = new ListDialogItem();
                listDialogItem.setData(method);
                listDialogItem.setItemText(String.format("{%s}%s{%s}",
                        new Color(itemUsageAnnotation.color()).toRgbHexString(), itemUsageAnnotation.name(), Color.DIALOG.toRgbHexString()));
                float order = itemUsageAnnotation.order();
                if(!listDialogItems.containsKey(order))
                    listDialogItems.put(order, new ArrayList<>());

                listDialogItems.get(order).add(listDialogItem);
            }
        }
        listDialogItems.forEach((k,v) -> {
            v.forEach(i -> {
                logger.debug("showOptions adding option " + i.getItemText() + " method " + ((Method)i.getData()).getName() + " order " + k);
                listDialog.addItem(i);
            });
        });
        listDialog.show();
    }

    @ItemUsageOption(name = "Test", color = 0xFFEE00, order = 0.1f)
    public boolean lol(LtrpPlayer player) {
        player.sendMessage(String.format("Test Option. Item name: %s type:%s class:%s", getName(), getType().name(), getClass().getName()));
        player.sendMessage(String.format("Test Option. Item UUID: %d amount:%d", getUUID(), getAmount()));
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Item && ((Item) o).getUUID() == this.getUUID();
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
        if(this.amount == 0) {
            this.destroy();
        }
    }

    @Override
    public void destroy() {
        isDestroyed = true;
        getEventManager().dispatchEvent(new ItemDestroyEvent(this), this);
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    protected EventManager getEventManager() {
        return eventManager;
    }


    /**
     * Updates the item data
     * @param con connection
     */
    /*
    protected void update(Connection con) {
        try {
            getUpdateStatement(con).execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    protected void delete(Connection con) {
        try {
            getDeleteStatement(con).execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
*/
    /**
     * Inserts this items data
     * @param con connection which will be used to generate and execute statements
     * @return the items item id
     * @throws SQLException
     */
    /*
    protected int insert(Connection con) throws SQLException {
        try (
                PreparedStatement stmt = getInsertStatement(con);
                ) {
            System.out.println("QUERY:"+ stmt.toString());
            stmt.executeUpdate();
            ResultSet result = stmt.getGeneratedKeys();
            if(result.next()) {
                return result.getInt(1);
            } else
                return 0;
        }
    }

    protected abstract PreparedStatement getUpdateStatement(Connection connection) throws SQLException;

    protected abstract PreparedStatement getInsertStatement(Connection connection) throws SQLException;

    protected abstract PreparedStatement getDeleteStatement(Connection connection) throws SQLException;
    */
}
