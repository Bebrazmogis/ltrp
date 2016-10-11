package lt.ltrp.`object`.impl

import lt.ltrp.`object`.Inventory
import lt.ltrp.`object`.impl.InventoryEntityImpl
import net.gtaun.shoebill.`object`.Player
import lt.ltrp.`object`.Entity
import lt.ltrp.`object`.PlayerData
import lt.ltrp.constant.TalkStyle
import lt.ltrp.constant.WalkStyle
import java.time.LocalDateTime
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-10-06.
 * This class represents the player data in the database, nothing else
 */
open class PlayerDataImpl(uuid: Int,
                          name: String,
                          var password: String,
                          var secretQuestion: String,
                          var secretAnswer: String,
                          var level: Int,
                          var adminLevel: Int,
                          var modLevel: Int,
                          var hoursOnline: Int,
                          /**
                       * Basically this has one ue: to check if the user is allowed to get payday
                       * If this is larger or equal to {@link lt.ltrp.PlayerController#MINUTES_FOR_PAYDAY} he will get payday
                       */
                        var minutesOnlineSincePayday: Int,
                          var boxStyle: Int,
                          var sex: String,
                          var age: Int,
                          var origin: String,
                          var disease: Int,
                          var respect: Int,
                          open var money: Int,
                          var deaths: Int,
                          open var wantedLevel: Int,
                          var walkStyle: WalkStyle,
                          var talkStyle: TalkStyle,
                          var lastLogIn: LocalDateTime,
                          var hunger: Int,
                          var totalPaycheck: Int,
                          protected var eventManager: EventManager) : InventoryEntityImpl(uuid, name), PlayerData {

    var isLoggedIn = false
        get
        set

    constructor(data: PlayerDataImpl): this(data.UUID, data.name, data.password, data.secretQuestion, data.secretAnswer,
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
            data.eventManager) {

    }

    init {
        this.inventory = Inventory.create(eventManager, this, name)
    }

    fun isValid(): Boolean {
        return UUID == Entity.INVALID_ID;
    }
}