package lt.maze

import net.gtaun.shoebill.ShoebillMain
import net.gtaun.shoebill.resource.Plugin
import net.gtaun.util.event.EventManagerNode
import java.time.*
import java.util.*

/**
 * Created by Bebras on 2016-12-21.
 * The main class of the payday plugin
 */
@ShoebillMain("Pay day", "Bebras", "", "1.0", buildNumber = 1)
class PayDayPlugin() : Plugin() {

    /**
     * Interval of payday expressed in minutes
     */
    var interval = 60
        set(value) {
            updateTimer()
            field = value
        }

    private lateinit var eventManagerNode: EventManagerNode
    private val paydayTimer = Timer("PayDay timer")
    private lateinit var payDayTask: PayDayTask

    override fun onDisable() {
        paydayTimer.cancel()
        eventManagerNode.cancelAll()
    }

    override fun onEnable() {
        eventManagerNode = eventManager.createChildNode()
        payDayTask = PayDayTask(eventManagerNode)
        updateTimer()
    }

    private fun updateTimer() {
        paydayTimer.cancel()
        paydayTimer.scheduleAtFixedRate(payDayTask, getDelay(), interval * 60 * 1000L)
        logger.info("PayDay timer scheduled in " + Duration.ofMillis(getDelay()) + " MS")
    }

    /**
     * Returns the amount until next round hour in milliseconds
     */
    private fun getDelay(): Long {
        val now = LocalDateTime.now()
        val nextHour = LocalDateTime.of(now.toLocalDate(), LocalTime.of(now.hour, 0, 0, 0))
        return Duration.between(now, nextHour).toMillis()
    }
}