package lt.ltrp.`object`.impl

import lt.ltrp.`object`.Inventory
import lt.ltrp.`object`.InventoryEntity
import lt.ltrp.`object`.Item
import lt.ltrp.`object`.WeaponItem
import lt.ltrp.constant.ItemType
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.dialog.ListDialog
import net.gtaun.shoebill.common.dialog.ListDialogItem
import net.gtaun.shoebill.constant.WeaponModel
import net.gtaun.shoebill.data.Color
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-11-09.

 */
class InfiniteInventoryImpl(var invName: String, var owner: InventoryEntity, var eventManager: EventManager): Inventory {

    private val items = mutableListOf<Item>()

    override fun contains(p0: Item?): Boolean {
        return items.contains(p0)
    }

    override fun getItems(): Array<out Item> {
        return items.toTypedArray()
    }

    override fun getItems(p0: ItemType?): Array<out Item> {
        return items.filter { it.type == p0 }.toTypedArray()
    }

    override fun <T : Any?> getItems(p0: Class<T>): Array<out T> {
        val filtered = items.filterIsInstance(p0) as Collection<Any>
        return filtered.toTypedArray<Any>() as Array<T>
    }

    override fun clear() {
        items.clear()
    }

    override fun getName(): String {
        return invName
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun show(p0: Player?) {
        if(items.size > 0) {
            val dialogItems = mutableListOf<ListDialogItem>()
            items.forEach {
                dialogItems.add(ListDialogItem.create()
                        .data(it)
                        .itemText(it.name)
                        .build())
            }
            ListDialog.create(p0, eventManager)
                    .caption(name)
                    .buttonOk("Pasirinkti")
                    .buttonCancel("Iðeiti")
                    .items<Any>(dialogItems)
                    .onClickOk(ListDialog.ClickOkHandler { dialog, dialogItem ->
                        p0?.sendMessage(Color.AQUA, "Pasirinkai daiktà " + dialogItem.getData() + " jo tipas:" + dialogItem.getData().javaClass.name)
                        (dialogItem.data as Item).showOptions(p0, this, dialog)
                    })
                    .build()
                    .show()
        }
    }

    override fun remove(p0: Item?) {
        items.remove(p0)
    }

    override fun remove(p0: Int) {
        items.removeAt(p0)
    }

    override fun containsWeapon(p0: WeaponModel?): Boolean {
        return items.firstOrNull { it is WeaponItem && it.weaponData.model == p0 } != null
    }

    override fun getItem(p0: ItemType?): Item {
        return items.first { it.type == p0 }
    }

    override fun getEntity(): InventoryEntity {
        return owner
    }

    override fun containsType(p0: ItemType?): Boolean {
        return items.firstOrNull { it.type == p0 } != null
    }

    override fun add(p0: Item?) {
        if(p0 != null)
            items.add(p0)
    }

    override fun add(p0: Array<out Item>?) {
        if(p0 != null)
            items.addAll(p0)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun tryAdd(p0: Item?): Boolean {
        if(p0 != null) {
            items.add(p0)
            return true
        } else {
            return false
        }
    }

    override fun isFull(): Boolean {
        return false
    }

}