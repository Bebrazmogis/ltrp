package lt.ltrp.item

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import lt.ltrp.`object`.InventoryEntity
import lt.ltrp.`object`.WeaponItem
import lt.maze.injector.InjectorPlugin
import net.gtaun.shoebill.constant.WeaponModel
import net.gtaun.shoebill.resource.ResourceManager

/**
 * Created by Bebras on 2016-10-28.
 */
interface ItemFactory {

    fun createWeapon(model: WeaponModel, ammo: Int, owner: InventoryEntity): WeaponItem

    companion object {
        private val kodein: Kodein? by lazy {
            ResourceManager.get().getPlugin(InjectorPlugin::class.java).kodein;
        }
        val instance by kodein?.lazy?.instance<ItemFactory>()
    }

}
