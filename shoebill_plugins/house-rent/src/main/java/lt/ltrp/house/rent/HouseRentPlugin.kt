package lt.ltrp.house.rent

import lt.ltrp.BankPlugin
import lt.ltrp.DatabasePlugin
import lt.ltrp.HousePlugin
import lt.ltrp.SpawnPlugin
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.PlayerData
import lt.ltrp.constant.Currency
import lt.ltrp.house.rent.dao.impl.MySqlHouseTenantDaoImpl
import lt.ltrp.house.event.HouseLoadedEvent
import lt.ltrp.event.PaydayEvent
import lt.ltrp.house.`object`.House
import lt.ltrp.house.rent.`object`.HouseTenant
import lt.ltrp.house.rent.`object`.impl.HouseTenantImpl
import lt.ltrp.house.rent.command.LandlordCommands
import lt.ltrp.house.rent.command.TenantCommands
import lt.ltrp.house.rent.dao.HouseTenantDao
import lt.ltrp.resource.DependentPlugin
import lt.ltrp.spawn.data.SpawnData
import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.common.command.PlayerCommandManager
import net.gtaun.shoebill.data.Color
import net.gtaun.util.event.EventManagerNode
import net.gtaun.util.event.HandlerPriority

/**
 * Created by Bebras on 2016-10-12.
 * This plugin is responsible for managing house rent
 * It provides all the necessary user commands: both tenant and landlord
 * Provides method for creating and removing tenants from a specific house
 * Retrieves all tenants from the database on startup
 *
 * <b>This plugin supports being reloaded at runtime</b>
 */
class HouseRentPlugin: DependentPlugin() {

    private lateinit var eventManager: EventManagerNode
    private lateinit var houseTenantDao: HouseTenantDao
    private lateinit var commandManager: PlayerCommandManager

    init {
        addDependency(DatabasePlugin::class)
        addDependency(HousePlugin::class)
    }

    override fun onDependenciesLoaded() {
        eventManager = getEventManager().createChildNode()
        houseTenantDao = MySqlHouseTenantDaoImpl(DatabasePlugin.get(DatabasePlugin::class.java).dataSource, eventManager)

        registerEvents()
        commandManager = PlayerCommandManager(eventManager)
        commandManager.registerCommands(LandlordCommands(eventManager))
        commandManager.registerCommands(TenantCommands(this))
        commandManager.installCommandHandler(HandlerPriority.NORMAL)

        House.get().forEach { loadHouseTenants(it) }
    }

    override fun onDisable() {
        eventManager.destroy()
        House.get().forEach { it.tenants.clear() }
        commandManager.uninstallAllHandlers()
        commandManager.destroy()
    }

    /**
     * Inserts a new tenant for the specified house
     *
     * @param house the house to add the tenant to
     * @param playerData data containing information about the player
     *
     * @return returns new newly created instance of [HouseTenant]
     */
    fun insertTenant(house: House, playerData: PlayerData): HouseTenant {
        val tenant = HouseTenantImpl(playerData, house, eventManager)
        val id = houseTenantDao.insert(tenant)
        tenant.UUID = id
        return tenant
    }

    /**
     * Removes a tenant from the specified house
     * Does nothing if player is not renting the house
     *
     * @param house the house to remove the tenant from
     * @param playerData tenant data
     */
    fun removeTenant(house: House, playerData: PlayerData) {
        val tenant = house.tenants.firstOrNull { it.player == playerData }
        if(tenant != null)
            houseTenantDao.remove(tenant)
    }

    private fun registerEvents() {
        eventManager.registerHandler(PaydayEvent::class.java, { onPayDay() })
        eventManager.registerHandler(HouseLoadedEvent::class.java, { onHouseLoad(it.property) })
    }

    private fun onPayDay() {
        val bankPlugin = Shoebill.get().resourceManager.getPlugin(BankPlugin::class.java)
        val spawnPlugin = SpawnPlugin.get(SpawnPlugin::class.java)
        if (bankPlugin != null && spawnPlugin != null) {
            // Get a list of all existing tenants
            // Filter out the offline ones because they don't pay rent
            // And then make them pay rent
            House.get()
                    .map { it.tenants }
                    .flatten()
                    .filter { it.player.isOnline }
                    .forEach {
                        val house = it.house
                        val rent = house.rentPrice
                        val player = if(it.player is LtrpPlayer) it.player as LtrpPlayer else LtrpPlayer.get(it.player.UUID)
                        val account = bankPlugin.bankController.getAccount(player)
                        if (account.money >= rent) {
                            account.addMoney(-rent)
                            bankPlugin.bankController.update(account)
                            house.addMoney(rent)
                            player.sendMessage(net.gtaun.shoebill.data.Color.WHITE, String.format("| Mokestis uþ nuomà: %d%c |", rent, Currency.SYMBOL))
                            // If he can't afford next rent, inform him of that
                            if(account.money < rent) {
                                player.sendMessage(Color.WHITE, "| Sekanèiai nuomai jûsø banko sàskaitoje per maþai pinigø! |")
                            }
                        } else {
                            player.sendMessage("Jûsø banko sàskaitoje nëra pakankamai pinigø susimokëti uþ nuomà, todël buvote iðmestas.")
                            spawnPlugin.setSpawnData(player, SpawnData.DEFAULT)
                            removeTenant(house, it.player)
                        }
            }
        } else {
            logger.error("BankPlugin or SpawnPlugin is not loaded")
        }
    }

    private fun onHouseLoad(house: House) {
        loadHouseTenants(house)
    }

    /**
     * Loads all tenants for the specified house and adds them to the house tenant list
     * This method relies on the fact that house tenant collection is not null
     * Prints out information about how many tenants were loaded to the console
     *
     * @param house house for which to load tenants for
     */
    private fun loadHouseTenants(house: House) {
        val tenants = houseTenantDao.get(house)
        house.tenants.addAll(tenants)
        logger.info("Loaded " + tenants.size + " tenants for house " + house.UUID)
    }
}