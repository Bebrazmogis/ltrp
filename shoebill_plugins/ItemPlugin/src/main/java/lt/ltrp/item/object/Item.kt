package lt.ltrp.`object`

import lt.ltrp.item.ItemController
import lt.ltrp.constant.ItemType
import net.gtaun.shoebill.common.dialog.AbstractDialog
import net.gtaun.shoebill.constant.SpecialAction
import net.gtaun.shoebill.entities.Destroyable
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 * *         2015.11.14.
 */
interface Item : NamedEntity, Destroyable {

    val type: ItemType
    val isStackable: Boolean
    var amount: Int
    fun showOptions(player: Player, inventory: Inventory, parentDialog: AbstractDialog)
}
