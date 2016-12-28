package lt.ltrp.property.event

import lt.ltrp.property.`object`.Property
import net.gtaun.util.event.Event

/**
 * @author Bebras
 * *         2016.04.19.
 */
abstract class PropertyEvent(val property: Property) : Event()
