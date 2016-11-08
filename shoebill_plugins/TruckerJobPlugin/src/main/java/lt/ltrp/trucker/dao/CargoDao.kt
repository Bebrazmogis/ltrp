package lt.ltrp.trucker.dao

import lt.ltrp.trucker.data.Cargo

/**
 * @author Bebras
* 2016.06.19.
 */
interface CargoDao {

    fun get(uuid: Int): Cargo?
    fun get(): List<Cargo>
    fun update(cargo: Cargo)
    fun delete(cargo: Cargo)
    fun insert(cargo: Cargo): Int

}