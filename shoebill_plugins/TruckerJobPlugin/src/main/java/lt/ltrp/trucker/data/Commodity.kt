package lt.ltrp.trucker.data

import lt.ltrp.`object`.AbstractEntity
/**
 * @author Bebras
* 2016.06.19.
*
* This class represents a simple commodity that has an UUID and a name, should not be instanciated
 */
open class Commodity(uuid: Int, name: String): AbstractEntity(uuid) {
    var name: String = name
        get
        set

    override fun equals(other: Any?): Boolean {
        return other is Commodity && other.getUUID() == getUUID()
    }
}