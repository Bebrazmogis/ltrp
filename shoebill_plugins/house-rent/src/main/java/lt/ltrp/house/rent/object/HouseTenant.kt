package lt.ltrp.house.rent.`object`

import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.NamedEntity
import lt.ltrp.player.`object`.PlayerData
import lt.ltrp.house.`object`.House
import java.time.Duration
import java.time.LocalDateTime

/**
 * Created by Bebras on 2016-10-12.
 */
interface HouseTenant: Entity {

    val player: PlayerData
    val house: House
    val rentTime: LocalDateTime

    fun rent()
    fun unRent()
    fun getRentPeriod(): Duration
}