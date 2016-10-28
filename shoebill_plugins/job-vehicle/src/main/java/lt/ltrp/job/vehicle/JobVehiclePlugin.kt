package lt.ltrp.job.vehicle

import lt.ltrp.DatabasePlugin
import lt.ltrp.VehiclePlugin
import lt.ltrp.job.JobController
import lt.ltrp.job.`object`.Job
import lt.ltrp.job.dao.JobVehicleDao
import lt.ltrp.job.event.JobLoadedEvent
import lt.ltrp.job.vehicle.dao.impl.MySqlJobVehicleDao
import lt.ltrp.resource.DependentPlugin
import net.gtaun.shoebill.`object`.Vehicle
import net.gtaun.shoebill.event.vehicle.VehicleDeathEvent
import net.gtaun.util.event.EventManagerNode

/**
 * Created by Bebras on 2016-10-25.
 * This plugin manages job vehicles
 * It handles loading them, updating when needed
 * It also provides implementation for [JobVehicleController]
 */
class JobVehiclePlugin: DependentPlugin() {

    private lateinit var eventManager: EventManagerNode
    private lateinit var vehicleDao: JobVehicleDao
    private lateinit var controller: JobVehicleControllerImpl

    init {
        addDependency(DatabasePlugin::class)
        addDependency(VehiclePlugin::class)
    }

    override fun onEnable() {
        super.onEnable()
        eventManager = getEventManager().createChildNode()
        addEvents()
    }

    override fun onDependenciesLoaded() {
        vehicleDao = MySqlJobVehicleDao(DatabasePlugin.get(DatabasePlugin::class.java).dataSource, eventManager)
        controller = JobVehicleControllerImpl(vehicleDao, eventManager)

        JobController.instance.get().forEach { loadVehicles(it) }
    }

    private fun addEvents() {
        eventManager.registerHandler(JobLoadedEvent::class.java, {  loadVehicles(it.job) })
        eventManager.registerHandler(VehicleDeathEvent::class.java, { onVehicleDeath(it.vehicle) })
    }

    override fun onDisable() {
        super.onDisable()
        JobVehicleContainer.get().forEach { it.destroy() }
    }

    private fun loadVehicles(job: Job) {
        val vehicles = vehicleDao.get(job)
        job.vehicles.addAll(vehicles)
        logger.info("Loaded " + vehicles.size + " job vehicles for job id " + job.UUID)
    }

    private fun onVehicleDeath(vehicle: Vehicle) {
        val jobVehicle = controller.get(vehicle)
        if(jobVehicle != null) {
            vehicleDao.update(jobVehicle)
        }
    }


}