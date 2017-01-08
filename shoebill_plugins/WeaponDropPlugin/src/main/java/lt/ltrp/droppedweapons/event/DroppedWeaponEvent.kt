package lt.ltrp.droppedweapons.event

import lt.ltrp.droppedweapons.`object`.DroppedWeaponData
import net.gtaun.util.event.Event

/**
 * @author Bebras
 * 2017.01.08.
 * A base event for events related to [DroppedWeaponData]
 */
abstract class DroppedWeaponEvent(val droppedWeapon: DroppedWeaponData): Event() {}