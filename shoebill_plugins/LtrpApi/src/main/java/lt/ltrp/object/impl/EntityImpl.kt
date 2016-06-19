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


}
