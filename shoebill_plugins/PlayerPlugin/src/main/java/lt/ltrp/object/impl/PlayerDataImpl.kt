package lt.ltrp.`object`.impl

import lt.ltrp.`object`.Inventory
import lt.ltrp.`object`.impl.InventoryEntityImpl
import net.gtaun.shoebill.`object`.Player
import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.PlayerData
import lt.ltrp.constant.TalkStyle
import lt.ltrp.constant.WalkStyle
import lt.ltrp.player.job.data.PlayerJobData
import java.time.LocalDateTime
import net.gtaun.util.event.EventManager
import java.lang.ref.WeakReference

/**
 * Created by Bebras on 2016-10-06.
 * This class represents the player data in the database, nothing else
 */
open class PlayerDataImpl(uuid: Int,
                          name: String,
                          override var password: String,
                          override var secretQuestion: String,
                          override var secretAnswer: String,
                          override var level: Int,
                          override var adminLevel: Int,
                          override var modLevel: Int,
                          override var hoursOnline: Int,
                          /**
                       * Basically this has one ue: to check if the user is allowed to get payday
                       * If this is larger or equal to {@link lt.ltrp.PlayerController#MINUTES_FOR_PAYDAY} he will get payday
                       */
                        override var minutesOnlineSincePayday: Int,
                          override var boxStyle: Int,
                          override var sex: String,
                          override var age: Int,
                          override var origin: String,
                          override var disease: Int,
                          override var respect: Int,
                          override var money: Int,
                          override var deaths: Int,
                          override var wantedLevel: Int,
                          override var walkStyle: WalkStyle,
                          override var talkStyle: TalkStyle,
                          override var lastLogIn: LocalDateTime,
                          override var hunger: Int,
                          override var totalPaycheck: Int,
                          override var jobData: PlayerJobData?,
                          protected var eventManager: EventManager) : InventoryEntityImpl(uuid, name), PlayerData {

    override var isLoggedIn = false
        get
        set

    override var isOnline = false
        get
        set

    constructor(data: PlayerData): this(data.UUID, data.name, data.password, data.secretQuestion, data.secretAnswer,
            data.level,
            data.adminLevel,
            data.modLevel,
            data.hoursOnline,
            data.minutesOnlineSincePayday,
            data.boxStyle,
            data.sex,
            data.age,
            data.origin,
            data.disease,
            data.respect,
            data.money,
            data.deaths,
            data.wantedLevel,
            data.walkStyle,
            data.talkStyle,
            data.lastLogIn,
            data.hunger,
            data.totalPaycheck,
            data.jobData,
            (data as PlayerDataImpl).eventManager) {

    }

    init {
        this.inventory = Inventory.create(eventManager, this, name)
        PlayerData.playerDataList.add(WeakReference(this))
    }

    override fun isValid(): Boolean {
        return UUID == Entity.INVALID_ID;
    }

    override fun isAdmin(): Boolean {
        return adminLevel > 0
    }

    override fun isMod(): Boolean {
        return modLevel > 0
    }



}