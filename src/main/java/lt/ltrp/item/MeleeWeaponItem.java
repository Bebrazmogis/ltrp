package lt.ltrp.item;

import lt.ltrp.data.Animation;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.constant.PlayerAttachBone;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.event.player.PlayerGiveDamageEvent;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.util.event.HandlerEntry;
import net.gtaun.util.event.HandlerPriority;

import java.sql.*;

/**
 * @author Bebras
 *         2015.11.14.
 */
public class MeleeWeaponItem extends ClothingItem {

    private static final Animation DEFAULT_ANIM = new Animation("BSKTBALL", "BBALL_def_jump_shot", false, 1000);

    private Animation animation;
    private HandlerEntry keyStateEntry;
    private HandlerEntry giveDamageEntry;
    private float damageIncrease;

    public MeleeWeaponItem(String name, ItemType type, int modelid, PlayerAttachBone bone, Animation animation, float extradmgproc) {
        super(name, type, modelid, bone);
        this.animation = animation;
        this.damageIncrease = extradmgproc;
    }

    public MeleeWeaponItem(String name, ItemType type, int modelid, PlayerAttachBone bone, float extradmproc) {
        this(name, type, modelid, bone, DEFAULT_ANIM, extradmproc);
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public float getDamageIncrease() {
        return damageIncrease;
    }

    public void setDamageIncrease(float damageIncrease) {
        this.damageIncrease = damageIncrease;
    }

    @Override
    public boolean equip(LtrpPlayer player, Inventory inventory) {
        if(super.equip(player, inventory)) {
            keyStateEntry = ItemController.getEventManager().registerHandler(PlayerKeyStateChangeEvent.class, e-> {
                if(e.getOldState().isKeyPressed(PlayerKey.FIRE)) {
                    player.applyAnimation(animation);
                }
            });

            giveDamageEntry = ItemController.getEventManager().registerHandler(PlayerGiveDamageEvent.class, HandlerPriority.LOW, e -> {
                if(e.getVictim() != null) {
                    e.getVictim().setHealth(e.getVictim().getHealth() - e.getAmount() * damageIncrease);
                }
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unequip(LtrpPlayer player, Inventory inventory) {
        if(super.unequip(player, inventory)) {
            if(keyStateEntry != null) {
                keyStateEntry.cancel();
            }
            if(giveDamageEntry != null) {
                giveDamageEntry.cancel();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void destroy() {
        if(keyStateEntry != null) {
            keyStateEntry.cancel();
        }
        if(giveDamageEntry != null) {
            giveDamageEntry.cancel();
        }
        super.destroy();
    }


    @Override
    protected PreparedStatement getUpdateStatement(Connection connection) throws SQLException {
        String sql = "UPDATE items_melee_weapon SET `name` = ?, stackable = ?, model = ?, bone = ?, worn = ?, anim_lib = ?, anim_name = ?, dmg_increase = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getModelid());
        stmt.setInt(4, getBone().getValue());
        stmt.setBoolean(5, isWorn());
        stmt.setString(6, getAnimation().getAnimLib());
        stmt.setString(7, getAnimation().getAnimName());
        stmt.setFloat(8, getDamageIncrease());
        stmt.setInt(9, getItemId());
        return stmt;
    }

    @Override
    protected PreparedStatement getInsertStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO items_melee_weapon (`name`, stackable, model, bone, worn, anim_lib, anim_name, dmg_increase) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, getName());
        stmt.setBoolean(2, isStackable());
        stmt.setInt(3, getModelid());
        stmt.setInt(4, getBone().getValue());
        stmt.setString(5, getAnimation().getAnimLib());
        stmt.setString(6, getAnimation().getAnimName());
        stmt.setFloat(7, getDamageIncrease());
        stmt.setBoolean(8, isWorn());
        return stmt;
    }

    @Override
    protected PreparedStatement getDeleteStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM items_melee_weapon WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getItemId());
        return stmt;
    }

    protected static MeleeWeaponItem getById(int itemid, ItemType type, Connection connection) throws SQLException {
        String sql = "SELECT * FROM items_clothing WHERE id = ?";
        MeleeWeaponItem item = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, itemid);

            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                item = new MeleeWeaponItem(result.getString("name"), type, result.getInt("model"), PlayerAttachBone.get(result.getInt("bone")), new Animation(result.getString("anim_lib"), result.getString("anim_name"), false, 1000), result.getFloat("dmg_increase"));
                item.setItemId(itemid);
            }
        }
        return item;
    }


}
