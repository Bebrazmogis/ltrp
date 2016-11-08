package lt.ltrp.`object`.impl;

import lt.ltrp.`object`.Entity;

/**
 * @author Bebras
 *         2015.11.29.
 */
abstract class EntityImpl(uuid: Int):  Entity {

    override var UUID: Int = uuid

    constructor(): this(Entity.INVALID_ID) {

    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is Entity && other.UUID == UUID
    }

    override fun hashCode(): Int {
        return UUID
    }
}
