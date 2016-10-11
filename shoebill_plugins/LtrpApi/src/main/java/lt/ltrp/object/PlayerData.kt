package lt.ltrp.`object`

import lt.ltrp.constant.TalkStyle
import lt.ltrp.constant.WalkStyle
import net.gtaun.util.event.EventManager
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


}