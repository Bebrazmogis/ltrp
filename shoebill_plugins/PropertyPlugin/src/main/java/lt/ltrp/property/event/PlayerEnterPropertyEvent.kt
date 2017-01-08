package lt.ltrp.property.event


import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.property.`object`.Property

/**
 * @author Bebras
 * *         2015.11.29.
 */
class PlayerEnterPropertyEvent(property: Property, val player: LtrpPlayer) : PropertyEvent(property)
