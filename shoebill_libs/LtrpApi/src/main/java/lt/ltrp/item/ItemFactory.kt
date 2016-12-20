package lt.ltrp.item

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import lt.ltrp.`object`.*
import lt.ltrp.constant.ItemType
import lt.maze.injector.InjectorPlugin
import net.gtaun.shoebill.constant.SpecialAction
import net.gtaun.shoebill.constant.WeaponModel
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManager
import kotlin.reflect.KClass

/**
 * Created by Bebras on 2016-10-28.
 */
interface ItemFactory {

    fun createWeapon(model: WeaponModel, ammo: Int, owner: InventoryEntity): WeaponItem
    fun createWeed(eventManager: EventManager, doses: Int): WeedItem
    fun createWeedSeed(eventManager: EventManager): WeedSeedItem

    fun create(type: ItemType, name: String, entity: InventoryEntity, eventManager: EventManager): Item
    fun create(type: ItemType, owner: InventoryEntity, eventManager: EventManager): Item
    fun create(type: ItemType, name: String, specialAction: SpecialAction, owner: InventoryEntity, eventManager: EventManager): Item


    companion object {
        private val kodein: Kodein? by lazy {
            ResourceManager.get().getPlugin(InjectorPlugin::class.java).kodein;
        }
        val instance by kodein?.lazy?.instance<ItemFactory>()
    }

}
