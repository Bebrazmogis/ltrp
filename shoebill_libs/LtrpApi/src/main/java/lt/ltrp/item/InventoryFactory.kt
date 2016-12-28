package lt.ltrp.item;

import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import lt.ltrp.`object`.Inventory
import lt.ltrp.`object`.InventoryEntity
import lt.maze.injector.InjectorPlugin
import net.gtaun.shoebill.resource.ResourceManager

/**
 * @author Bebras
 *         2016.04.14.
 */
interface InventoryFactory {

    fun create(owner: InventoryEntity, name: String): Inventory
    fun create(owner: InventoryEntity, name: String, size: Int): Inventory

    companion object {
        private val kodein by lazy {
            ResourceManager.get().getPlugin(InjectorPlugin::class.java)?.kodein
        }
        val INSTANCE: InventoryFactory by kodein?.lazy?.instance<InventoryFactory>()
    }


}
