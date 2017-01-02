package lt.ltrp.player.`object`.impl

import lt.ltrp.`object`.impl.NamedEntityImpl
import lt.ltrp.constant.TalkStyle
import lt.ltrp.constant.WalkStyle
import lt.ltrp.player.`object`.PlayerData
import net.gtaun.shoebill.entities.Vehicle
import net.gtaun.util.event.EventManager
import java.time.LocalDateTime

/**
 * Created by Bebras on 2016-10-06.
 * This class represents the player data in the database, nothing else
 */
open class PlayerDataImpl(uuid: Int, name: String,
                          override var password: String,
                          override var secretQuestion: String?,
                          override var secretAnswer: String?,
                          override var level: Int,
                          override var adminLevel: Int,
                          override var modLevel: Int,
                          override var hoursOnline: Int,
                          override var minutesOnlineSincePayday: Int,
                          override var boxStyle: Int,
                          override var sex: String,
                          override var age: Int,
                          override var origin: String,
                          override var deaths: Int,
                          override var walkStyle: WalkStyle,
                          override var talkStyle: TalkStyle,
                          override var lastLogin: LocalDateTime,
                          override var lastUsedVehicle: Vehicle?,
                          override var hunger: Int,
                          override var boxingStyle: Int,
                          override var forumName: String,
                          override var currentPaycheck: Int,
                          override var onlineHours: Int,
                          override var description: String?,
                          override var totalPaycheck: Int,
                          override var isLoggedIn: Boolean,
                          override var isMuted: Boolean,
                          override var isFrozen: Boolean,
                          override var isFactionManager: Boolean,
                          override var isInComa: Boolean,
                          override var isAnimationPlaying: Boolean,
                          override var isMasked: Boolean,
                          override var isSeatbelt: Boolean,
                          override var isInJail: Boolean,
                          override var isCuffed: Boolean,
                          override var isMod: Boolean,
                          //override val drugs: PlayerDrugs,
                          //override val licenses: PlayerLicenses,
                          //override val settings: PlayerSettings,
                          override var money: Int,
                          protected val eventManager: EventManager):
        PlayerData, NamedEntityImpl(uuid, name) {

    constructor(pd: PlayerData, eventManager: EventManager): this(pd.UUID, pd.name, pd.password, pd.secretQuestion, pd.secretAnswer,
            pd.level, pd.adminLevel, pd.modLevel, pd.hoursOnline, pd.minutesOnlineSincePayday, pd.boxStyle,
            pd.sex, pd.age, pd.origin, pd.deaths, pd.walkStyle, pd.talkStyle, pd.lastLogin, pd.lastUsedVehicle,
            pd.hunger, pd.boxingStyle, pd.forumName, pd.currentPaycheck, pd.onlineHours, pd.description,
            pd.totalPaycheck, pd.isLoggedIn, pd.isMuted, pd.isFrozen, pd.isFactionManager,
            pd.isInComa, pd.isAnimationPlaying, pd.isMasked, pd.isSeatbelt, pd.isInJail, pd.isCuffed,
            pd.isMod, pd.money, eventManager) {

    }

    override fun addTotalPaycheck(amount: Int) {
        totalPaycheck += amount
    }
}


/*
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
                          //override var money: Int,
                          override var deaths: Int,
                          //override var wantedLevel: Int,
                          override var walkStyle: WalkStyle,
                          override var talkStyle: TalkStyle,
                          //override var lastLogIn: LocalDateTime,
                          override var hunger: Int,
                          override var totalPaycheck: Int,
                          protected var eventManager: EventManager) : InventoryEntityImpl(uuid, name), PlayerData {

    override var isLoggedIn = false
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
            //data.lastLogIn,
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
        return UUID != Entity.INVALID_ID
    }

    override fun isAdmin(): Boolean {
        return adminLevel > 0
    }

    override fun isMod(): Boolean {
        return modLevel > 0
    }

    override fun toString(): String {
        return super.toString() + "[uuid=" + UUID + ",name=" + name + ",level=" + level + ",adminLevel=" + adminLevel +
                ",modLevel=" + modLevel + ",hoursOnline=" + hoursOnline + ",minutesOnlineSincePayday=" + minutesOnlineSincePayday +
                ",boxStyle=" + boxStyle + ",sex=" + sex + ",age=" + age + ",origin=" + origin + ",disease=" + disease +
                ",respect=" + respect + ",money=" + money + ",deaths=" + deaths + ",wantedLevel=" + wantedLevel + ",walkStyle=" + walkStyle +
                ",talkStyle=" + talkStyle + ",lastLogIn=" + lastLogIn + ",hunger=" + hunger + ",totalPaycheck=" + totalPaycheck +
                "jobData=" + jobData +
                "]"
    }

}*/