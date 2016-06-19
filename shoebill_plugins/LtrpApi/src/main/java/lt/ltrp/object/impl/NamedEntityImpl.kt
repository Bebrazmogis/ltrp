package lt.ltrp.`object`.impl;

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.NamedEntity

/**
 * @author Bebras
 *         2015.11.29.
 */
abstract  class NamedEntityImpl(uuid: Int, override var name: String): NamedEntity, EntityImpl(uuid) {

    constructor(): this(Entity.INVALID_ID, "invalid entity") {

    }

}
