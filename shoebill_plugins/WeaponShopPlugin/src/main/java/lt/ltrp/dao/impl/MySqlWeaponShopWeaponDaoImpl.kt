package lt.ltrp.dao.impl

import lt.ltrp.`object`.WeaponShop
import lt.ltrp.dao.WeaponShopWeaponDao
import lt.ltrp.data.WeaponShopWeapon
import net.gtaun.shoebill.constant.WeaponModel
import java.sql.*
import javax.sql.DataSource

/**
 * @author Bebras
* 2016.06.14.
 */
class MySqlWeaponShopWeaponDaoImpl (dataSource: DataSource) : WeaponShopWeaponDao {

    val dataSource: DataSource = dataSource

    companion object {
        public fun toWeapon(r: ResultSet, shop: WeaponShop): WeaponShopWeapon {
            return WeaponShopWeapon(r.getInt("id"),
                    shop,
                    r.getString("name"),
                    WeaponModel.get( r.getInt("model_id")),
                    r.getInt("ammo"),
                    r.getInt("price")
            );
        }
    }

    override fun get(shop: WeaponShop): MutableCollection<WeaponShopWeapon>? {
        var weps = arrayListOf<WeaponShopWeapon>()
        val sql: String = "SELECT * FROM weapon_shop_weapons WHERE shop_id = ?"
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql)
        try {
            stmt.setInt(1, shop.getUUID())
            val r: ResultSet = stmt.executeQuery()
            while(r.next())
                weps.add(MySqlWeaponShopWeaponDaoImpl.toWeapon(r, shop))
            r.close()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
        return weps;
    }

    override fun get(uuid: Int): WeaponShopWeapon? {
        throw UnsupportedOperationException()
    }

    override fun update(weapon: WeaponShopWeapon) {
        val sql: String = "UPDATE weapon_shop_weapons (name, model_id, ammo, price) VALUES (?, ?, ?, ?)"
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql)
        try {
            stmt.setString(1, weapon.name)
            stmt.setInt(2, weapon.weaponModel.getModelId())
            stmt.setInt(3, weapon.ammo)
            stmt.setInt(4, weapon.price)
            stmt.execute()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
    }

    override fun remove(weapon: WeaponShopWeapon) {
        val sql: String = "REMOVE FROM weapon_shop_weapons WHERE id = ?"
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql)
        try {
            stmt.setInt(1, weapon.getUUID())
            stmt.execute()
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
    }

    override fun insert(weapon: WeaponShopWeapon) {
        val sql: String = "INSERT INTO weapon_shop_weapons (shop_id, name, model_id, ammo, price) VALUES (?, ?, ?, ?, ?)"
        val connection: Connection = dataSource.getConnection()
        val stmt: PreparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        try {
            stmt.setInt(1, weapon.weaponShop.getUUID())
            stmt.setString(2, weapon.name)
            stmt.setInt(3, weapon.weaponModel.getModelId())
            stmt.setInt(4, weapon.ammo)
            stmt.setInt(5, weapon.price)
            stmt.execute()
            val keys: ResultSet = stmt.getGeneratedKeys()
            if(keys.next())
                weapon.setUUID(keys.getInt(1))
        } catch(e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
            stmt.close()
        }
    }
}