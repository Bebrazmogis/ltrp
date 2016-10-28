package lt.ltrp

import lt.ltrp.`object`.*
import lt.ltrp.`object`.impl.*
import lt.ltrp.constant.ItemType
import lt.ltrp.dao.ItemDao
import lt.ltrp.dao.PhoneDao
import lt.ltrp.event.item.ItemCreateEvent
import lt.ltrp.item.ItemController
import lt.ltrp.item.ItemFactory
import net.gtaun.shoebill.constant.SpecialAction
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-28.
 */
class ItemControllerImpl(private val itemFactory: ItemFactory,
                         private val eventManager: EventManager,
                         private val itemDao: ItemDao,
                         private val phoneDao: PhoneDao):
        ItemController {

    override fun createWeedSeed(eventManager: EventManager?): WeedSeedItem {
        val item = WeedSeedItemImpl(eventManager)
        onNewItem(item, null)
        return item
    }

    override fun createWeed(eventManager: EventManager?, doses: Int): WeedItem {
        val item = WeedItemImpl(eventManager, doses)
        onNewItem(item, null)
        return item
    }

    override fun createInventory(eventManager: EventManager?, owner: InventoryEntity?, name: String?, size: Int): Inventory {
        val inv = FixedSizeInventory(eventManager, name, size, owner)
        return inv
    }

    override fun createItem(type: ItemType?, owner: InventoryEntity?, eventManager: EventManager?): Item {
        var item: Item? = null;
        when(type) {
            ItemType.Radio -> item = RadioItemImpl(eventManager)
            ItemType.Dice -> item = DiceItem (eventManager)
            ItemType.FishingRod -> item = FishingRodItem (eventManager)
            ItemType.Fueltank -> item = FuelTankItem (eventManager)
            ItemType.Cigarettes -> item = CigarettesItem (eventManager)
            ItemType.Phone -> item = ItemPhoneImpl (eventManager, phoneDao.generateNumber())
            ItemType.WeedSeed -> item = WeedSeedItemImpl (eventManager)
            ItemType.Molotov -> item = MolotovItem (eventManager)
            ItemType.HouseAudio -> item = HouseAudioItem (eventManager)
            ItemType.Weed -> item = WeedItemImpl (eventManager, 1)
            ItemType.Toolbox -> item = ToolboxItem (eventManager)
            ItemType.Mp3Player -> item = Mp3Item (eventManager)
            ItemType.BoomBox -> item = BoomBoxItem (eventManager)
        }
        if (item != null)
            item.amount = 1
        onNewItem(item as Item, owner)
        return item as Item
    }

    override fun createItem(type: ItemType?, name: String, entity: InventoryEntity?, eventManager: EventManager?): Item {
        val item = createItem (type, entity, eventManager)
        item.name = name
        return item
    }

    override fun createItem(type: ItemType?, name: String, specialAction: SpecialAction?, entity: InventoryEntity?, eventManager: EventManager?): Item {
        var item: Item? = null
        when(type) {
            ItemType.Drink -> {
                item = DrinkItemImpl (0, name, eventManager, type, 1, specialAction)
                item.setAmount(1)
            }
            else ->
                item = createItem(type, name, entity, eventManager)
        }
        return item as Item
    }

    private fun onNewItem(item: Item, owner: InventoryEntity?) {
        eventManager.dispatchEvent(ItemCreateEvent(item, owner))
        itemDao.insert(item, owner)
    }



}