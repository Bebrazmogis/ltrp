package lt.ltrp.item;


import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;

/**
 * @author Bebras
 *         2015.12.04.
 */
public abstract class AbstractItem implements Item {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String name;
    /**
     * id which identifies the item amongst its kind
     */
    private int itemId;
    /**
     * A unique id
     */
    private int id;
    private boolean isDestroyed;

    private ItemType type;
    private boolean stackable;
    private int amount;

    public AbstractItem(String name, ItemType type, boolean stackable) {
        this.name = name;
        this.type = type;
        this.stackable = stackable;
        this.amount = 0;
    }



    @Override
    public void showOptions(LtrpPlayer player, Inventory inventory, AbstractDialog parentDialog) {
        logger.debug("showOptions called. player uid=" + player.getUserId() + " inventory name=" + inventory.getName());
        ListDialog listDialog = ListDialog.create(player, ItemController.getInstance().getEventManager()).build();
        listDialog.setCaption(getName() + " parinktys");
        listDialog.setButtonOk("Pasirinkti");
        listDialog.setButtonCancel("Atgal");
        listDialog.setClickCancelHandler((d) -> parentDialog.show());
        listDialog.setClickOkHandler((dialog, dialogitem) -> {
            logger.debug("showOptions dialog item selected. item="+ dialogitem.getItemText());
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
            }
        });

        for(Method method : this.getClass().getMethods()) {
            // If so, method is an option
            if(method.isAnnotationPresent(ItemUsageOption.class)) {
                logger.debug("showOptions adding method " + method.getName());
                ItemUsageOption itemUsageAnnotation = method.getAnnotation(ItemUsageOption.class);
                ListDialogItem listDialogItem = new ListDialogItem();
                listDialogItem.setData(method);
                listDialogItem.setItemText(itemUsageAnnotation.name());
                listDialog.addItem(listDialogItem);
            }
        }
        listDialog.show();
    }

    @ItemUsageOption(name = "test")
    public boolean lol(LtrpPlayer player) {
        player.sendMessage(Color.CORAL, "Test option. This is " + getName() + " type:" + getType().name() + " Class:" + getClass().getName());
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Item && ((Item) o).getGlobalId() == this.getGlobalId();
    }


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public int getGlobalId() {
        return id;
    }

    /**
     * Sets the items global ID. It should only be used when creating or loading the item
     * No changes at runtime should be made
     * @param id the new id
     */
    protected void setGlobalId(int id) {
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
        if(this.amount == 0) {
            this.destroy();
        }
    }

    /**
     * Updates the item data
     * @param con connection
     */
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

    /**
     * Inserts this items data
     * @param con connection which will be used to generate and execute statements
     * @return the items item id
     * @throws SQLException
     */
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

    @Override
    public void destroy() {
        isDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}
