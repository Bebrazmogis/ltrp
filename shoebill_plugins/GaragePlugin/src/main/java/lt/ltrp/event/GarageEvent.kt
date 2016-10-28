package lt.ltrp.event

import lt.ltrp.`object`.Garage
import lt.ltrp.event.property.PropertyEvent

/**
 * Created by Bebras on 2016-10-06.
 */
abstract class GarageEvent(var garage: Garage): PropertyEvent(garage) {
}