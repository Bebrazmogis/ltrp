package lt.ltrp.`object`

import lt.ltrp.constant.TalkStyle
import lt.ltrp.constant.WalkStyle
import lt.ltrp.player.job.data.PlayerJobData
import net.gtaun.shoebill.`object`.Player
import net.gtaun.util.event.EventManager
import java.lang.ref.WeakReference
import java.time.LocalDateTime

/**
 * Created by Bebras on 2016-10-07.
 */
interface PlayerData: InventoryEntity {

    var password: String
    var secretQuestion: String
    var secretAnswer: String
    var level: Int
    var adminLevel: Int
    var modLevel: Int
    var hoursOnline: Int
    var minutesOnlineSincePayday: Int
    var boxStyle: Int
    var sex: String
    var age: Int
    var origin: String
    var disease: Int
    var respect: Int
    var money: Int
    var deaths: Int
    var wantedLevel: Int
    var walkStyle: WalkStyle
    var talkStyle: TalkStyle
    var lastLogIn: LocalDateTime
    var hunger: Int
    var totalPaycheck: Int
    var isLoggedIn: Boolean
    var isOnline: Boolean
    var jobData: PlayerJobData?


    fun isValid(): Boolean
    fun isAdmin(): Boolean
    fun isMod(): Boolean

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