package lt.ltrp.data

import lt.ltrp.EntityImpl
import lt.ltrp.`object`.WeaponShop
import net.gtaun.shoebill.constant.WeaponModel

/**
 * @author Bebras
* 2016.06.14.
 */

class WeaponShopWeapon (uuid: Int, weaponShop: WeaponShop, name: String?, weaponModel: WeaponModel, ammo: Int, price: Int) : EntityImpl(uuid) {

    var weaponShop: WeaponShop = weaponShop
        set
        get

    var name: String? = name
        get
        set

    var weaponModel: WeaponModel = weaponModel
        set
        get

    var ammo: Int = ammo
        set
        get

    var price: Int = price
        set
        get

}