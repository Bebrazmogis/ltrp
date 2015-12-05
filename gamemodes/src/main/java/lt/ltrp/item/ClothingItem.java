package lt.ltrp.item;

import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.data.Vector3D;

import java.sql.*;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class ClothingItem extends BasicItem {

    private int modelid;
    private PlayerAttachBone bone;
    private boolean worn;

    public ClothingItem(String name, ItemType type, int modelid, PlayerAttachBone bone) {
        super(name, type, false);
        this.modelid = modelid;
        this.bone = bone;
        this.worn = false;
    }

    public int getModelid() {
        return modelid;
    }

    public void setModelid(int modelid) {
        this.modelid = modelid;
    }

    public PlayerAttachBone getBone() {
        return bone;
    }

    public void setBone(PlayerAttachBone bone) {
        this.bone = bone;
    }

    public boolean isWorn() {
        return worn;
    }

    public void setWorn(boolean worn) {
        this.worn = worn;
    }

    @ItemUsageOption(name = "Uþsidëti")
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory() == inventory) {
            if(!worn) {
                if(player.getAttach().getSlotByBone(bone).isUsed()) {
                    player.sendMessage(Color.LIGHTRED, "Jûs jau esate kaþkà apsirengæs ant ðios kûno dalies");
                } else {
                    player.getAttach().getSlotByBone(bone).set(bone, modelid, new Vector3D(), new Vector3D(), new Vector3D(), 1, 1);
                    player.sendActionMessage("Uþsideda daiktà kuris atrodo kaip " + getName());
                    worn = true;
                    return true;
                }
            } else {
                player.sendMessage(Color.LIGHTRED, "Jûs jau esate uþsidëjæs ðá daiktà");
            }
        }
        return false;
    }

    @ItemUsageOption(name = "Nusiimti")
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory() == inventory) {
            if(worn) {
                player.getAttach().getSlotByBone(bone).remove();
                player.sendActionMessage("nusiema daiktà kuris atrodo kaip " + getName());
                worn = false;
                return true;
            }
        }
        return false;
    }

    @ItemUsageOption(name = "Redaguoti")
    public boolean edit(LtrpPlayer player, Inventory inventory) {
        if(player.getInventory() == inventory) {
            if(worn) {
                player.getAttach().getSlotByBone(bone).edit();
                return true;
            }
        }
        return false;
    }


    @Override
    protected PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_clothing SET `name` = ?, stackable = ?, model = ?, bone = ?, worn = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getModelid());
        stmt.setInt(4, getBone().getValue());
        stmt.setBoolean(5, isWorn());
        stmt.setInt(6, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_clothing (`name`, stackable, model, bone, worn) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getModelid());
        stmt.setInt(4, getBone().getValue());
        stmt.setBoolean(5, isWorn());
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_clothing WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }

    protected static ClothingItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_clothing WHERE id = ?";
        ClothingItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new ClothingItem(result.getString("name"), type, result.getInt("model"), PlayerAttachBone.get(result.getInt("bone")));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}
