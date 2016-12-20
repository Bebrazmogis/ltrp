package lt.ltrp;

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import lt.ltrp.`object`.Inventory
import lt.ltrp.`object`.impl.ItemCommands
import lt.ltrp.dao.ItemDao
import lt.ltrp.dao.PhoneDao
import lt.ltrp.dao.impl.MySqlDrugAddictionDaoImpl
import lt.ltrp.dao.impl.SqlItemDao
import lt.ltrp.dao.impl.SqlPhoneDaoImpl
import lt.ltrp.event.GarageLoadedEvent
import lt.ltrp.event.item.ItemCreateEvent
import lt.ltrp.event.item.ItemDestroyEvent
import lt.ltrp.event.item.ItemLocationChangeEvent
import lt.ltrp.event.item.PlayerDropItemEvent
import lt.ltrp.event.player.PlayerDrawWeaponItemEvent
import lt.ltrp.item.InventoryFactory
import lt.ltrp.item.ItemController
import lt.ltrp.item.ItemFactory
import lt.ltrp.resource.DependentPlugin
import lt.maze.injector.resource.BindingPlugin
import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority

/**
 * @author Bebras
 *         2015.11.14.
 */
class ItemPlugin: DependentPlugin(), BindingPlugin {

    private lateinit var eventManager: EventManagerNode
    private lateinit var itemDao: ItemDao
    private lateinit var drugController: DrugController
    private lateinit var phoneDao: PhoneDao
    private lateinit var phoneController: PhoneController
    private lateinit var commandManager: PlayerCommandManager
    private lateinit var inventoryFactory: InventoryFactoryImpl
    private lateinit var itemFactory: ItemFactory

    init {
        addDependency(DatabasePlugin::class)
    }

    override fun getKodeinModule(): Kodein.Module {
        return Kodein.Module {
            bind<ItemFactory>() with singleton { itemFactory }
            bind<InventoryFactory>() with singleton { inventoryFactory }
        }
    }

    override fun onDependenciesLoaded() {
        this.eventManager = getEventManager().createChildNode()
        val ds = Shoebill.get().resourceManager.getPlugin(DatabasePlugin::class.java).dataSource
        this.phoneDao = SqlPhoneDaoImpl(ds)
        this.itemDao = SqlItemDao(ds, eventManager, phoneDao)
        val drugAddictionDao = MySqlDrugAddictionDaoImpl(ds)
        itemFactory = ItemFactoryImpl(eventManager, itemDao, phoneDao)
        inventoryFactory = InventoryFactoryImpl(eventManager)

        drugController = DrugController(eventManager, drugAddictionDao)
        phoneController = PhoneController(eventManager, phoneDao)

        registerCommands()
        registerEvents()
        logger.debug("Controller:" + inventoryFactory + " dao:" + itemDao);
    }

    override fun onDisable() {
        super.onDisable()
        drugController.destroy()
        phoneController.destroy()
        commandManager.destroy()
    }

    private fun registerCommands() {
        commandManager = PlayerCommandManager(eventManager)
        commandManager.registerCommands(ItemCommands (eventManager, itemDao))
        commandManager.installCommandHandler(HandlerPriority.NORMAL)
    }

    private fun registerEvents() {
        eventManager.registerHandler(PlayerDropItemEvent::class.java,  {
            itemDao.delete(it.item)
        })

        eventManager.registerHandler(PlayerDrawWeaponItemEvent::class.java,  {
            itemDao.delete(it.item)
        })

        eventManager.registerHandler(ItemDestroyEvent::class.java,  {
            itemDao.delete(it.item)
        })

        eventManager.registerHandler(ItemLocationChangeEvent::class.java, { e ->
            val newOwner = e.getNewInventory().getEntity()
            itemDao.update(e.getItem(), newOwner);
        });

        eventManager.registerHandler(ItemCreateEvent::class.java, { e ->
            if (e.getOwner() != null)
                itemDao.insert(e.getItem(), e.getOwner());
            else
                itemDao.insert(e.getItem());
        });

        eventManager.registerHandler(GarageLoadedEvent::class.java, {e ->
            val g = e.garage
            val items = itemDao.getItems(g)
            if (g.inventory != null)
                items.forEach { g.inventory?.tryAdd(it) }
            else {
                val inv = Inventory.create (eventManager, g, "Garaþas "+g.name, 15)
                inv.add(items)
                g.inventory = inv
            }
        });
    }

    fun getItemDao(): ItemDao {
        return itemDao
    }

    fun getPhoneDao(): PhoneDao {
        return phoneDao
    }


    /*


        FishingBait(10),
        FishingBag(11),
        Lighter(12),
        Clothing(16),
        MeleeWeapon(17),
        Suitcase(18),
        Mask(19),
        Weapon(20),
        Drink(21),
        Syringe(23),
        Newspaper(25),
        Amphetamine(26),
        Cocaine(27),
        Extazy(28),
        Heroin(29),
        MetaAmphetamine(30),
        Pcp(31),
        DmvTheory(34),
        Prescription(35),
        Materials(36),
        CarAudio(40)
     */
}

