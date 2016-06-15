package lt.ltrp.dao.impl

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.LabelWeaponShop
import lt.ltrp.`object`.WeaponShop
import lt.ltrp.`object`.PickupWeaponShop
import lt.ltrp.`object`.impl.LabelWeaponShopImpl
import lt.ltrp.`object`.impl.PickupWeaponShopImpl
import lt.ltrp.dao.WeaponShopDao
import lt.ltrp.data.Color
import net.gtaun.shoebill.data.Location
import net.gtaun.util.event.EventManager
import java.sql.*
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.14.
 */
class MySqlWeaponShopDaoImpl (ds: DataSource, event: EventManager): WeaponShopDao {

    val dataSource: DataSource = ds
    val eventManager: EventManager = event;

    private fun toShop(r: ResultSet): WeaponShop {
        val color: Color = Color(r.getInt("label_color"))
        if(!r.wasNull()) {
            return LabelWeaponShopImpl(
                    r.getInt("id"),
                    r.getString("name"),
                    Location(r.getFloat("x"), r.getFloat("y"), r.getFloat("z"), r.getInt("interior_id"), r.getInt("world_id")),
                    eventManager,
                    r.getString("label_text"),
                    color
            )
        } else {
            return PickupWeaponShopImpl(
                    r.getInt("id"),
                    r.getString("name"),
                    Location(r.getFloat("x"), r.getFloat("y"), r.getFloat("z"), r.getInt("interior_id"), r.getInt("world_id")),
                    eventManager,
                    r.getString("pickup_text"),
                    r.getInt("pickup_model")
            )
        }
    }

    override fun get(): MutableCollection<WeaponShop> {
        val shops = arrayListOf<WeaponShop>()
        val sql: String = "SELET * FROM weapon_shops"
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql)
        try {
            val r: ResultSet = stmt.executeQuery()
            while(r.next())
                shops.add(toShop(r))
            r.close()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
        return shops
    }

    override fun getWithWeapons(): MutableCollection<WeaponShop>? {
        val shops = arrayListOf<WeaponShop>()
        val sql: String = "SELET weapon_shops.*, weapon_shop_weapons.* FROM weapon_shops LEFT JOIN weapon_shop_weapons ON weapon_shops.id = weapon_shop_weapons.shop_id"
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql)
        try {
            val r: ResultSet = stmt.executeQuery()
            var lastId: Int = Entity.INVALID_ID
            var shop: WeaponShop? = null
            while(r.next()) {
                val id: Int = r.getInt("id");
                // If the current ID changed or shop is null, let's create a new one
                if(shop == null || id != lastId) {
                    if(shop != null)
                        shops.add(shop)
                    shop = toShop(r)
                    lastId = id
                }
                // If so, we're still loading the same shop, so just parse the weapon
                else {
                    val tmpShop = shop
                    if(tmpShop != null)
                        tmpShop.addSoldWeapon(MySqlWeaponShopWeaponDaoImpl.toWeapon(r, tmpShop))
                }
            }
            r.close()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
        return shops
    }

    override fun get(uuid: Int): WeaponShop? {
        val sql: String = "SELET * FROM weapon_shops WHERE id = ?"
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql)
        try {
            stmt.setInt(1, uuid)
            val r: ResultSet = stmt.executeQuery()
            while(r.next())
                return toShop(r)
            r.close()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
        return null
    }

    override fun update(shop: WeaponShop) {
        val sql: String = "UPDATE wepon_shop SET name = ?, x = ?, y = ?, z = ?, world_id = ?, interior_id = ?, label_color = ?, label_text = ?, pickup_model = ?, pickup_text = ? WHERE id = ?"
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql)
        try {
            stmt.setString(1, shop.getName())
            stmt.setFloat(2, shop.getLocation().x)
            stmt.setFloat(3, shop.getLocation().y)
            stmt.setFloat(4, shop.getLocation().z)
            stmt.setInt(5, shop.getLocation().worldId)
            stmt.setInt(6, shop.getLocation().interiorId)
            // extra fields for label stuff or null
            if(shop is LabelWeaponShop) {
                val labelShop: LabelWeaponShop = shop
                stmt.setInt(7, labelShop.getColor().getValue())
                stmt.setString(8, labelShop.getText())
            } else {
                stmt.setNull(7, Types.INTEGER)
                stmt.setNull(8, Types.VARCHAR)
            }
            // Extra fields for pickup shops or null
            if(shop is PickupWeaponShop) {
                val pickupShop: PickupWeaponShop = shop
                stmt.setInt(9, pickupShop.getModelId())
                stmt.setString(10, pickupShop.getText())
            } else {
                stmt.setNull(9, Types.INTEGER)
                stmt.setNull(10, Types.VARCHAR)
            }
            stmt.setInt(11, shop.getUUID())

            stmt.execute()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
    }

    override fun remove(shop: WeaponShop) {
        val sql: String = "DELETE FROM weapon_shops WHERE id = ?"
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql)
        try {
            stmt.setInt(1, shop.getUUID())
            stmt.execute()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
    }

    override fun insert(shop: WeaponShop) {
        val sql: String = "INSERT INTO weapon_shops (name, x, y, z, world_id, interior_id, label_color, label_text, pickup_model, pickup_text) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        try {
            stmt.setString(1, shop.getName())
            stmt.setFloat(2, shop.getLocation().x)
            stmt.setFloat(3, shop.getLocation().y)
            stmt.setFloat(4, shop.getLocation().z)
            stmt.setInt(5, shop.getLocation().worldId)
            stmt.setInt(6, shop.getLocation().interiorId)
            // extra fields for label stuff or null
            if(shop is LabelWeaponShop) {
                val labelShop: LabelWeaponShop = shop
                stmt.setInt(7, labelShop.getColor().getValue())
                stmt.setString(8, labelShop.getText())
            } else {
                stmt.setNull(7, Types.INTEGER)
                stmt.setNull(8, Types.VARCHAR)
            }
            // Extra fields for pickup shops or null
            if(shop is PickupWeaponShop) {
                val pickupShop: PickupWeaponShop = shop
                stmt.setInt(9, pickupShop.getModelId())
                stmt.setString(10, pickupShop.getText())
            } else {
                stmt.setNull(9, Types.INTEGER)
                stmt.setNull(10, Types.VARCHAR)
            }
            stmt.execute()
            val r = stmt.getGeneratedKeys()
            if(r.next())
                shop.setUUID(r.getInt(1))
            r.close()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
    }



}