package lt.ltrp.item;

import lt.ltrp.data.LtrpWeaponData;
import lt.ltrp.item.event.PlayerDrawWeaponItemEvent;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.WeaponData;
import net.gtaun.shoebill.object.Timer;

import java.sql.*;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class WeaponItem extends BasicItem {

    private LtrpWeaponData weaponData;
    private boolean beingDrawn = false;
    private Timer drawTimer;

    public WeaponItem(String name, LtrpWeaponData weaponData) {
        super(name, ItemType.Weapon, false);
        this.weaponData = weaponData;
    }

    public WeaponItem(LtrpWeaponData weaponData) {
        this(weaponData.getModel().getName(), weaponData);
    }

    public LtrpWeaponData getWeaponData() {
        return weaponData;
    }

    public void setWeaponData(LtrpWeaponData weaponData) {
        this.weaponData = weaponData;
    }

    @Override
    public boolean drop(LtrpPlayer player, Inventory inventory) {
        if(!weaponData.isJob()) {
            if(!player.isInComa()) {
                weaponData.setDropped(player.getLocation());
                player.getInventory().remove(this);
                player.sendActionMessage("iðmeta ginklà kuris atrodo kaip " + getName());
                return true;
            } else
                player.sendErrorMessage("Jûs esate komos bûsenoje!");
        } else
            player.sendErrorMessage("Negalite iðmesti darbinio ginklo.");
        return false;
    }

    @ItemUsageOption(name = "Iðsitraukti")
    public boolean drawWeapon(LtrpPlayer player, Inventory inventory) {
        if(beingDrawn) {
            return false;
        }
        if(player.getInventory() == inventory) {
            player.sendActionMessage("bando kaþkà iðsitraukti");
            beingDrawn = true;
            drawTimer = Timer.create(1500 + player.getDrunkLevel()/2, 1, e -> {
                onItemDrawn(player, weaponData, inventory);
            });
        } else {
            player.sendActionMessage("bando kaþkà iðsitraukti");
            beingDrawn = true;
            Timer.create(2300 + player.getDrunkLevel()/2, 1, e -> {
                onItemDrawn(player, weaponData, inventory);
            });
        }
        drawTimer.start();
        return true;
    }

    private void onItemDrawn(LtrpPlayer player, WeaponData weapondata, Inventory location) {
        player.giveWeapon(getWeaponData());
        if(location == player.getInventory()) {
            player.sendActionMessage("iðsitraukia ginklà kuris atrodo kaip " + getName());
        } else {
            player.sendActionMessage("iðsitraukia ginklà kuris atrodo kaip " + getName() + " ið " + location.getName());
        }
        beingDrawn = false;
        location.remove(this);
        ItemController.getInstance().getEventManager().dispatchEvent(new PlayerDrawWeaponItemEvent(player, location, this));
    }



    @Override
    protected PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_weapon SET `name` = ?, stackable = ?, weapon_id = ?, ammo = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getWeaponData().getModel().getId());
        stmt.setInt(4, getWeaponData().getAmmo());
        stmt.setInt(5, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_weapon (`name`, stackable, weapon_id, ammo) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getWeaponData().getModel().getId());
        stmt.setInt(4, getWeaponData().getAmmo());
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_weapon WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }

    protected static WeaponItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_weapon WHERE id = ?";
        WeaponItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new WeaponItem(result.getString("name"), new LtrpWeaponData(WeaponModel.get(result.getInt("weapon_id")), result.getInt("ammo"), false));
                item.setItemId(itemid);
            }
        }
        return item;
    }
}
