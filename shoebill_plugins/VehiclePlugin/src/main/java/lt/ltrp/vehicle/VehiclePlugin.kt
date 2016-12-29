package lt.ltrp.vehicle


import lt.ltrp.resource.DependentPlugin
import lt.ltrp.vehicle.`object`.LtrpVehicle
import lt.ltrp.vehicle.dao.VehicleFuelConsumptionDao
import lt.ltrp.vehicle.dao.impl.ConstantVehicleFuelConstumptionDao
import lt.ltrp.vehicle.event.handlers.*
import lt.ltrp.vehicle.tasks.FuelDecrementTask
import lt.maze.DatabasePlugin
import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.entities.Player
import net.gtaun.shoebill.entities.Timer
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent
import net.gtaun.shoebill.event.vehicle.VehicleUpdateDamageEvent
import net.gtaun.shoebill.resource.ResourceManager
import net.gtaun.util.event.EventManagerNode


/**
 * @author Bebras
 *         2015.12.06.
 */
@ShoebillMain("Vehicle plugin", "Bebras")
class VehiclePlugin: DependentPlugin() {

    private lateinit var eventManagerNode: EventManagerNode
    private val fuelConsumptionDao = ConstantVehicleFuelConstumptionDao()
    private var timer: java.util.Timer = java.util.Timer(this.description.name + " timer")
    private val playerLastUsedVehicles = mutableMapOf<Player, LtrpVehicle>()
    // Not used actually
    private val vehicleEngineTimers = mutableMapOf<LtrpVehicle, Timer>()

    init {

        addDependency(DatabasePlugin::class)
    }

    override fun onEnable() {
        super.onEnable()
        eventManagerNode = eventManager.createChildNode()
    }

    override fun onDisable() {
        super.onDisable()
        eventManagerNode.cancelAll()
        timer.cancel()
        LtrpVehicle.vehicles.forEach { it.destroy() }
        playerLastUsedVehicles.clear()
        vehicleEngineTimers.values.forEach(Timer::destroy)
        vehicleEngineTimers.clear()
    }

    override fun onDependenciesLoaded() {
        val databasePlugin = ResourceManager.get().getPlugin(DatabasePlugin::class.java)
        if(databasePlugin != null) {
            try {
                databasePlugin.query(this.javaClass.getResourceAsStream("vehicles.sql"))
            } catch (e: Exception) {
                logger.error("Can not insert vehicle table", e)
                super.disable()
            }
        }

        startTimers()
        addEventHandlers()
    }


    private fun startTimers() {
        timer.scheduleAtFixedRate(FuelDecrementTask(fuelConsumptionDao, eventManagerNode), 0L, FUEL_TIMER_INTERVAL)
    }

    private fun addEventHandlers() {
        eventManagerNode.registerHandler(VehicleUpdateDamageEvent::class.java, VehicleUpdateDamageEventHandler())
        eventManagerNode.registerHandler(PlayerStateChangeEvent::class.java, PlayerStateChangeEventHandler(playerLastUsedVehicles))
        eventManagerNode.registerHandler(PlayerKeyStateChangeEvent::class.java, PlayerKeyStateChangeEventHandler())

        eventManagerNode.registerHandler(PlayerDisconnectEvent::class.java, PlayerDisconnectEventHandler(playerLastUsedVehicles))
        eventManagerNode.registerHandler(VehicleDeathEvent::class.java, VehicleDeathEventHandler(playerLastUsedVehicles, vehicleEngineTimers))
        //eventManagerNode.registerHandler(VehicleDestroyEvent::class.java, VehicleDestroyEventHandler(vehicleEngineTimers))
    }

    companion object {
        /**
         * Frequency at which fuel is decreased
         */
        val FUEL_TIMER_INTERVAL = 60 * 1000L
    }

    fun getFuelConsumptionDao(): VehicleFuelConsumptionDao {
        return fuelConsumptionDao
    }

}
    /*

        // TODO new module/item module
        eventManager.registerHandler(VehicleDeathEvent.class, e -> {
            if(vehicle != null) {
                // Once a vehicle dies, car audio is removed. Sort of item sink
                if(vehicle.getInventory().containsType(ItemType.CarAudio)) {
                    Item item = vehicle.getInventory().getItem(ItemType.CarAudio);
                    vehicle.getInventory().remove(item);
                    item.destroy();
                }
            }
        });

        // TODO move to speedometer module
        eventManager.registerHandler(PlayerStateChangeEvent.class, HandlerPriority.HIGHEST, e -> {

                if(newState == PlayerState.DRIVER) {
                    player.getInfoBox().showSpeedometer(LtrpVehicle.getByVehicle(player.getVehicle()));
                }

                if(oldState == PlayerState.DRIVER) {
                    player.getInfoBox().hideSpeedometer();
                }

        });

        timer = Timer.create(160, ticks -> {
            for(LtrpVehicle vehicle : LtrpVehicle.get()) {
                LtrpPlayer player = vehicle.getDriver();
                if(player != null) {
                    float speed = vehicle.getSpeed();
                    Velocity velocity = vehicle.getVelocity();
                    eventManager.dispatchEvent(new SpeedometerTickEvent(player, vehicle, speed));
                    player.getInfoBox().update();

                    if(maxSpeeds.containsKey(player)) {
                        float maxSpeed = maxSpeeds.get(player);
                        if(speed > maxSpeed) {
                            float cap = maxSpeeds.get(player);
                            double change = (cap-7) / speed;
                            velocity.setX((float)(velocity.getX() * change));
                            velocity.setY((float)(velocity.getY() * change));
                            vehicle.setVelocity(velocity);
                        }
                    }

                }
            }
        });
        timer.start();


        // TODO once I find a way to pass calculation of engine start time/success rate
        // To other modules, then change the code below to it
        eventManager.registerHandler(PlayerKeyStateChangeEvent.class, e -> {


                        if(!vehicleEngineTimers.containsKey(vehicle)) {
                            int time;
                            int percentage;
                            float dmg = 1000 - vehicle.getHealth();
                            /*if(vehicle instanceof PlayerVehicle) {
                                int deaths = ((PlayerVehicle)vehicle).getDeaths();
                                time = 1500 + deaths * 150;
                                percentage = (int) (100 - Factorial.get((int) (deaths * 1.5)) - dmg / 40f);
                                logger.debug("Vehicle start percentage:" + percentage);
                            }// else {
                                time = 1500;
                                percentage = 100 - (int)dmg / 40;
                            //}

        });
    }

}
*/