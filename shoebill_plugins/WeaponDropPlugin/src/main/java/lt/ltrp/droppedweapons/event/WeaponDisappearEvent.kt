package lt.ltrp.droppedweapons.event

import lt.ltrp.droppedweapons.`object`.DroppedWeaponData

/**
 * @author Bebras
 * 2017.01.08.
 * Dispatched whenever a [DroppedWeaponData] disappears
 */
class WeaponDisappearEvent(droppedWeaponData: DroppedWeaponData): DroppedWeaponEvent(droppedWeaponData) {}