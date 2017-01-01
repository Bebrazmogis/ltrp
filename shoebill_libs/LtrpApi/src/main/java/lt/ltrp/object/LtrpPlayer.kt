package lt.ltrp.`object`


import lt.ltrp.ActionMessenger
import lt.ltrp.StateMessenger
import lt.ltrp.constant.MODERATOR
import lt.ltrp.data.Animation
import lt.ltrp.data.LtrpWeaponData
import lt.ltrp.data.PlayerOffer
import lt.ltrp.player.PlayerController
import net.gtaun.shoebill.constant.WeaponModel
import net.gtaun.shoebill.constant.WeaponSlot
import net.gtaun.shoebill.data.Color
import net.gtaun.shoebill.data.Location
import net.gtaun.shoebill.entities.Player
import java.util.*


/**
 * @author Bebras
 *         2016.04.07.
 *         The main player object that holds ALL the information about a player
 *         This object represents a connected player, for offline player data refer to [PlayerData]
 *         It should NOT extend [Player] class as it has a field for accessing shoebill/samp data
 */
interface LtrpPlayer : PlayerData, StateMessenger, ActionMessenger {

    val player: Player
    val isModerator: Boolean
        get() = modLevel > 0
    val isAdmin: Boolean
        get() = player.isAdmin || adminLevel > 0

    var animation: Animation?


    val ucpId: Int

    fun sendInfoText(msg: String)  {
        sendInfoText(msg, DEFAULT_INFOTEXT_DURATION)
    }

    fun sendInfoText(msg: String, seconds: Int)

    fun getWeapons(): Array<LtrpWeaponData>
    fun getWeaponData(weaponModel: WeaponModel): LtrpWeaponData
    fun ownsWeapon(model: WeaponModel): Boolean
    fun isWeaponSlotUsed(slot: WeaponSlot): Boolean
    fun removeWeapon(weaponData: LtrpWeaponData)
    fun removeWeapon(model: WeaponModel)
    fun removeJobWeapons()
    fun getArmedWeaponData(): LtrpWeaponData
    fun giveWeapon(weaponData: LtrpWeaponData)

    fun setVolume(volume: Int)
    fun isDataLoaded(): Boolean

    fun getOffers(): Collection<PlayerOffer>
    fun containsOffer(type: Class<*>): Boolean
    fun <T : PlayerOffer> getOffers(type: Class<T>): Collection<T>
    fun <T : PlayerOffer> getOffer(type: Class<T>): T

    fun sendFadeMessage(color: Color, text: String, distance: Float)
    /**
     * Sends a message to the player but fades the colour accordingly from players distance from location
     */
    fun sendFadeMessage(color: Color, text: String, location: Location)
    fun sendErrorMessage(message: String)
    fun sendErrorMessage(errorCode: Int)
    fun sendMessage(s: String, distance: Float) {
        sendMessage(Color.WHITE, s, distance)
    }

    fun sendMessage(color: Color, s: String, distance: Float)
    fun sendDebug(color: Color, message: String)
    fun sendDebug(message: String)
    fun sendDebug(vararg objects: Any)

    /**
     * Just a wrapper for [Player.sendMessage] for convenience
     */
    fun sendMessage(color: Color, message: String) {
        player.sendMessage(color, message)
    }


    fun getClosestPlayer(maxDistance: Float): LtrpPlayer
    fun getClosestPlayer(): LtrpPlayer
    fun getClosestPlayers(maxDistance: Float): Array<LtrpPlayer>
    fun applyAnimation(animation: Animation)
    fun applyLoopAnimation(animLib: String, animation: String, lockX: Boolean, lockY: Boolean, stoppable: Boolean) {
        applyLoopAnimation(animLib, animation, lockX, lockY, false, stoppable)
    }

    /**
     * Deprecated because of freeze parameter, it does not make sense to play a loop and then freeze.
     * @param animLib
     * @param animation
     * @param lockX
     * @param lockY
     * @param freeze
     * @param stoppable
     */
    @Deprecated(message = "")
    fun applyLoopAnimation(animLib: String, animation: String, lockX: Boolean, lockY: Boolean, freeze: Boolean, stoppable: Boolean) {
        applyAnimation(animLib, animation, 4.1f, true, lockX, lockY, freeze, stoppable)
    }

    fun applyAnimation(animLib: String, anim: String, speed: Float, loop: Boolean, lockX: Boolean, lockY: Boolean, freeze: Boolean, time: Int, forceSync: Boolean) {
        applyAnimation(Animation(animLib, anim, speed, loop, lockX, lockY, freeze, forceSync, time))
    }

    fun applyAnimation(animLib: String, anim: String, speed: Float, loop: Boolean, lockX: Boolean, lockY: Boolean, freeze: Boolean, time: Int, forceSync: Boolean, stoppable: Boolean) {
        applyAnimation(Animation(animLib, anim, speed, loop, lockX, lockY, freeze, forceSync, time, stoppable))
    }
    fun applyAnimation(animLib: String, anim: String, lockX: Boolean, lockY: Boolean, freeze: Boolean, time: Int, stoppable: Boolean) {
        applyAnimation(Animation(animLib, anim, 4.1f, false, lockX, lockY, freeze, true, time, stoppable))
    }

    fun clearAnimations()

    /**

     * @param animLib
     * @param animName
     * @param speed
     * @param loop
     * @param lockX
     * @param lockY
     * @param freeze
     */
    fun applyAnimation(animLib: String, animName: String, speed: Float, loop: Boolean, lockX: Boolean, lockY: Boolean, freeze: Boolean) {
        applyAnimation(Animation(animLib, animName, speed, loop, lockX, lockY, freeze, true, 0))
    }

    fun applyAnimation(animLib: String, animName: String, speed: Float, loop: Boolean, lockX: Boolean, lockY: Boolean, freeze: Boolean, stoppable: Boolean) {
        applyAnimation(Animation(animLib, animName, speed, loop, lockX, lockY, freeze, true, 0, stoppable))
    }

    fun isAudioConnected(): Boolean

    fun freeze()
    fun unfreeze()

    fun mute()
    fun unMute()

    companion object {
        val DEFAULT_PLAYER_COLOR = Color(0xFF, 0xFF, 0xFF, 0x00)
        val DEFAULT_ACTION_MESSAGE_DISTANCE = 20f
        val DEFAULT_INFOTEXT_DURATION = 60

        fun get(): Collection<LtrpPlayer> {
            return PlayerController.instance.getPlayers()
        }

        fun get(uuid: Int): LtrpPlayer? {
            return get().firstOrNull { it.UUID == uuid }
        }

        fun get(player: Player): LtrpPlayer? {
            if(player.isNpc) return null
            return get().firstOrNull { it == player }
        }

        fun get(name: String): LtrpPlayer? {
            return get().firstOrNull { it.name == name }
        }

        fun getByPartName(name: String): LtrpPlayer? {
            val unmatchedChars: SortedMap<Int, LtrpPlayer> = TreeMap()
            get().filter { it.name.contains(name) }.forEach {
                unmatchedChars.put(it.name.length - name.length, it)
            }
            return unmatchedChars.values.first()
        }

        fun getClosest(player: Player, maxDistance: Float): LtrpPlayer? {
            return get()
                    .filter { it.player.location.distance(player.location) <= maxDistance }
                    .minBy { it.player.location.distance(player.location) }
        }

        fun getClosestPlayers(player: Player, maxDistance: Float): List<LtrpPlayer> {
            return get().filter { it.player.location.distance(player.location) <= maxDistance }
        }

        fun sendAdminMessage(s: String) {
            get()
                    .filter { it.isAdmin }
                    .forEach { it.player.sendMessage(Color.GREENYELLOW, s) }
        }

        /**
         * Sends an admin message to administration that passes the specified condition
         * @param s the message to send
         * @param condition condition that the user has to pass to receive message
         */
        fun sendAdminMessage(s: String, condition: (LtrpPlayer) -> Boolean) {
            get()
                    .filter { it.isAdmin && it.isModerator &&  condition.invoke(it) }
                    .forEach { it.sendMessage(Color.GREENYELLOW, s) }
        }

        fun sendModMessage(message: String) {
            get()
                    .filter{ (it.isModerator || it.isAdmin) && !it.settings.isModChatDisabled }
                    .forEach { it.sendMessage(Color.MODERATOR, message) }
        }

        fun sendGlobalOocMessage(message: String) {
            get()
                    .filter { !it.settings.isOocDisabled }
                    .forEach { it.sendMessage(Color(0xB1C8FB), message) }
        }

        fun sendGlobalMessage(message: String) {
            sendGlobalMessage(Color.GREENYELLOW, message)
        }

        fun sendGlobalMessage(color: Color, message: String) {
            get().forEach { it.sendMessage(color, message) }
        }
    }
}