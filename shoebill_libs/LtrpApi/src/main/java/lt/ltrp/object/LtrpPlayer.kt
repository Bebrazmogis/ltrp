package lt.ltrp.`object`


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
 */
abstract class LtrpPlayer() : Player(), PlayerData {

    var isModerator: Boolean = false
        get() { return modLevel > 0 }

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
                    .filter { it.location.distance(player.location) <= maxDistance }
                    .minBy { it.location.distance(player.location) }
        }

        fun getClosestPlayers(player: Player, maxDistance: Float): List<LtrpPlayer> {
            return get().filter { it.location.distance(player.location) <= maxDistance }
        }

        fun sendAdminMessage(s: String) {
            get()
                    .filter { it.isAdmin }
                    .forEach { it.sendMessage(Color.GREENYELLOW, s) }
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

    fun sendInfoText(msg: String)  {
        sendInfoText(msg, DEFAULT_INFOTEXT_DURATION);
    }

    internal abstract fun sendInfoText(msg: String, seconds: Int)

    abstract fun getWeapons(): Array<LtrpWeaponData>
    abstract fun getWeaponData(weaponModel: WeaponModel): LtrpWeaponData
    abstract fun ownsWeapon(model: WeaponModel): Boolean
    abstract fun isWeaponSlotUsed(slot: WeaponSlot): Boolean
    abstract fun removeWeapon(weaponData: LtrpWeaponData)
    abstract fun removeWeapon(model: WeaponModel)
    abstract fun removeJobWeapons()
    abstract fun getArmedWeaponData(): LtrpWeaponData
    abstract fun giveWeapon(weaponData: LtrpWeaponData)

    abstract fun setVolume(volume: Int)

    abstract fun isDataLoaded(): Boolean

    abstract fun getOffers(): Collection<PlayerOffer>
    abstract fun containsOffer(type: Class<*>): Boolean
    abstract fun <T : PlayerOffer> getOffers(type: Class<T>): Collection<T>
    abstract fun <T : PlayerOffer> getOffer(type: Class<T>): T

    abstract fun sendFadeMessage(color: Color, text: String, distance: Float)
    /**
     * Sends a message to the player but fades the colour accordingly from players distance from location
     */
    abstract fun sendFadeMessage(color: Color, text: String, location: Location)
    abstract fun sendErrorMessage(message: String)
    abstract fun sendErrorMessage(errorCode: Int)
    abstract fun sendActionMessage(message: String, distance: Float)
    abstract fun sendActionMessage(s: String)
    abstract fun sendStateMessage(s: String, distance: Float)
    abstract fun sendStateMessage(s: String)
    fun sendMessage(s: String, distance: Float) {
        sendMessage(Color.WHITE, s, distance)
    }

    abstract fun sendMessage(color: Color, s: String, distance: Float)
    abstract fun sendDebug(color: Color, message: String)
    abstract fun sendDebug(message: String)
    abstract fun sendDebug(vararg objects: Any)


    abstract fun getClosestPlayer(maxDistance: Float): LtrpPlayer
    abstract fun getClosestPlayer(): LtrpPlayer
    abstract fun getClosestPlayers(maxDistance: Float): Array<LtrpPlayer>
    abstract fun applyAnimation(animation: Animation)
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
        applyAnimation(animLib, anim, speed, loop, lockX, lockY, freeze, time, forceSync, false)
    }

    abstract fun applyAnimation(animLib: String, anim: String, speeed: Float, loop: Boolean, lockX: Boolean, lockY: Boolean, freeze: Boolean, time: Int, forceSync: Boolean, stopable: Boolean)
    fun applyAnimation(animLib: String, anim: String, lockX: Boolean, lockY: Boolean, freeze: Boolean, time: Int, stoppable: Boolean) {
        applyAnimation(animLib, anim, 4.1f, false, lockX, lockY, freeze, time, true, stoppable)
    }

    abstract fun clearAnimations()
    abstract fun getAnimation(): Animation

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
        applyAnimation(animLib, animName, speed, loop, lockX, lockY, freeze, false)
    }

    abstract fun applyAnimation(animLib: String, animName: String, speed: Float, loop: Boolean, lockX: Boolean, lockY: Boolean, freeze: Boolean, stoppable: Boolean)
    abstract fun isAudioConnected(): Boolean

    abstract fun getUcpId(): Int
    abstract fun setUcpId(ucpId: Int)

    abstract fun freeze()
    abstract fun unfreeze()

    abstract fun mute()
    abstract fun unMute()
}