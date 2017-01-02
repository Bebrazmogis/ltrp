package lt.ltrp

import lt.ltrp.`object`.*
import lt.ltrp.`object`.impl.*
import lt.ltrp.constant.ItemType
import lt.ltrp.dao.ItemDao
import lt.ltrp.dao.PhoneDao
import lt.ltrp.data.LtrpWeaponData
import lt.ltrp.event.item.ItemCreateEvent
import lt.ltrp.item.ItemFactory
import net.gtaun.shoebill.constant.SpecialAction
import net.gtaun.shoebill.constant.WeaponModel
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-28.
 */
class ItemFactoryImpl(private val eventManager: EventManager,
                      private val itemDao: ItemDao,
                      private val phoneDao: PhoneDao): ItemFactory {

    override fun create(type: ItemType, name: String, entity: InventoryEntity, eventManager: EventManager): Item {
        val item = create(type, entity, eventManager)
        item.name = name
        return item
    }

    override fun create(type: ItemType, name: String, specialAction: SpecialAction, owner: InventoryEntity, eventManager: EventManager): Item {
        var item: Item? = null
        when(type) {
            ItemType.Drink -> {
                item = DrinkItemImpl (0, name, eventManager, type, 1, specialAction)
                item.setAmount(1)
            }
            else ->
                item = create(type, name, owner, eventManager)
        }
        return item as Item
    }

    override fun create(type: ItemType, owner: InventoryEntity, eventManager: EventManager): Item {
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

    override fun createWeed(eventManager: EventManager, doses: Int): WeedItem {
        val item = WeedItemImpl(eventManager, doses)
        onNewItem(item, null)
        return item
    }

    override fun createWeedSeed(eventManager: EventManager): WeedSeedItem {
        val item = WeedSeedItemImpl(eventManager)
        onNewItem(item, null)
        return item
    }

    override fun createWeapon(model: WeaponModel, ammo: Int, owner: InventoryEntity): WeaponItem {
        val item = WeaponItemImpl(eventManager, LtrpWeaponData (model, ammo, false))
        eventManager.dispatchEvent(ItemCreateEvent (item, owner))
        itemDao.insert(item)
        return item
    }

    private fun onNewItem(item: Item, owner: InventoryEntity?) {
        eventManager.dispatchEvent(ItemCreateEvent(item, owner))
        itemDao.insert(item, owner)
    }

}