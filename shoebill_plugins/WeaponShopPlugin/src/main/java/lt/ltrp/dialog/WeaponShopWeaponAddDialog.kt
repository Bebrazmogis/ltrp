package lt.ltrp.dialog

import lt.ltrp.WeaponShopPlugin
import lt.ltrp.`object`.Entity
import lt.ltrp.player.`object`.LtrpPlayer
import lt.ltrp.`object`.WeaponShop
import lt.ltrp.data.WeaponShopWeapon
import lt.maze.dialog.AbstractDialog
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 *         2016.06.14.
 */
object WeaponShopWeaponAddDialog {

    fun create(player: LtrpPlayer, eventManager: EventManager, parentDialog: AbstractDialog, weaponShop: WeaponShop):
            WeaponModelSelectDialog {
        return WeaponModelSelectDialog.create(player, eventManager, {
            caption { "Pasirinkite ginklà kurá norite pridët kaip prekæ" }
            buttonOk { "Pridëti" }
            buttonCancel { "Atgal" }
            parent { parentDialog }
            onClickCancel { it.showParent() }
            onSelectWeapon { dialog, weaponModel ->
                val weapon = WeaponShopWeapon(Entity.INVALID_ID, weaponShop, null, weaponModel, 0, 0)
                WeaponShopPlugin.get(WeaponShopPlugin::class.java).shopWeaponDao.insert(weapon)
            }
        })
    }
}
