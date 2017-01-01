package lt.ltrp.`object`

import lt.ltrp.constant.TalkStyle
import lt.ltrp.constant.WalkStyle
import lt.ltrp.data.PlayerDrugs
import lt.ltrp.player.licenses.data.PlayerLicenses
import lt.ltrp.player.settings.data.PlayerSettings
import net.gtaun.shoebill.entities.Vehicle
import java.lang.ref.WeakReference
import java.time.LocalDateTime

/**
 * Created by Bebras on 2016-10-07.
 */
interface PlayerData: InventoryEntity {

    var password: String
    var secretQuestion: String?
    var secretAnswer: String?
    var level: Int
    var adminLevel: Int
    var modLevel: Int
    var hoursOnline: Int
    /**
     * Basically this has one ue: to check if the user is allowed to get payday
     * If this is larger or equal to {@link PlayerController#MINUTES_FOR_PAYDAY} he will get payday
     */
    var minutesOnlineSincePayday: Int
    var boxStyle: Int
    var sex: String
    var age: Int
    var origin: String
    var deaths: Int
    var walkStyle: WalkStyle
    var talkStyle: TalkStyle
    var lastLogin: LocalDateTime
    var lastUsedVehicle: Vehicle?
    var hunger: Int
    var boxingStyle: Int
    var forumName: String
    var money: Int

    /**
     * Current(current paydays) paycheck
     */
    var currentPaycheck: Int

    /**
     * Paydays a user spent online
     */
    var onlineHours: Int

    var description: String?

    /*********************************************
     *  Various flags
     */

    /**
     * Total unclaimed job money
     */
    var totalPaycheck: Int
    var isLoggedIn: Boolean
    var isMuted: Boolean
    var isFrozen: Boolean
    var isFactionManager: Boolean
    var isInComa: Boolean
    var isAnimationPlaying: Boolean
    var isMasked: Boolean
    var isSeatbelt: Boolean
    var isInJail: Boolean
    var isCuffed: Boolean
    var isMod: Boolean

    fun addTotalPaycheck(amount: Int)

    /**
     * References to other data classes
     */
    val drugs: PlayerDrugs
    val licenses: PlayerLicenses
    val settings: PlayerSettings
    //var jobData: PlayerJobData?

    val firstName: String
        get() {
            val index = name.indexOf("_")
            return if(index != -1) name.substring(0, index) else name.substring(0, name.length / 2)
        }

    val lastName: String
        get() {
            val index = name.indexOf("_")
            if(index != -1) return name.substring(index, name.length)
            else return name.substring(name.length / 2, name.length)
        }

    val charName: String
        get() = name.replace("_", " ")

    val maskName: String
        get() = "((Kaukëtasis " + (UUID + 400) + "))"

    val isValid: Boolean
        get() = UUID != Entity.INVALID_ID

    companion object {

        // TODO should be at least protected, but Kotlin does not support that yet
        var playerDataList = mutableListOf<WeakReference<PlayerData>>()

        fun get(uuid: Int): PlayerData? {
            cleanup()
            return playerDataList.firstOrNull { it.get().UUID == uuid }?.get()
        }

        private fun cleanup() {
            playerDataList.forEach {
                if(it.get() == null) {
                    playerDataList.remove(it)
                }
            }
        }
    }
}