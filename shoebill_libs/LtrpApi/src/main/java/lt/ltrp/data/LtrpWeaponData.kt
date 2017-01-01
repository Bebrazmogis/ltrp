package lt.ltrp.data

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.impl.EntityImpl
import net.gtaun.shoebill.constant.WeaponModel
import net.gtaun.shoebill.data.Vector2D
import net.gtaun.shoebill.data.WeaponData

/**
 * @author Bebras
 * *         2015.12.02.
 */
open class LtrpWeaponData(weaponData: WeaponData, job: Boolean, uuid: Int) : EntityImpl(uuid) {

    var weaponData: WeaponData = weaponData;
    var isJob: Boolean = false
    var isDropped: Boolean = false
        private set


    constructor(type: WeaponModel, ammo: Int, job: Boolean) : this(WeaponData(type, ammo), job, Entity.INVALID_ID) {

    }

    constructor(uuid: Int, type: WeaponModel, ammo: Int, job: Boolean) : this(WeaponData(type, ammo), job, uuid) {

    }


    fun clone(): LtrpWeaponData {
        return LtrpWeaponData(this.weaponData.model, this.weaponData.ammo, this.isJob)
    }

    fun setDropped(loc: Vector2D) {
        this.isDropped = true
        DroppedWeaponData(this, loc)
    }


    companion object {
        val UNARMED = LtrpWeaponData(WeaponModel.UNKNOWN, 1, false)
    }
}
