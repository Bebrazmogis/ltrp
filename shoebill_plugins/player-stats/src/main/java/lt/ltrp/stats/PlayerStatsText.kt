package lt.ltrp.stats

import lt.ltrp.BankPlugin
import lt.ltrp.SpawnPlugin
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.constant.Currency
import lt.ltrp.data.Color
import lt.ltrp.player.job.PlayerJobPlugin
import net.gtaun.shoebill.entities.Player
import net.gtaun.util.event.EventManager
import java.time.format.DateTimeFormatter

/**
 * Created by Bebras on 2016-12-22.
 * A text representation of user stats
 */
class PlayerStatsText(override val player: Player,
                      private val eventManager: EventManager,
                      private val spawnPlugin: SpawnPlugin,
                      private val bankPlugin: BankPlugin,
                      private val playerJobPlugin: PlayerJobPlugin) : PlayerStats {


    override fun show(player: Player) {
        val p = LtrpPlayer.get(player)
        val spawnData = spawnPlugin.getSpawnData(player)
        val bankAccount = bankPlugin.bankController.getAccount(player)
        val jobData = player.jobData
        p.sendMessage(Color.WHITE, String.format("|VEIKËJAS| Amþius: [%s] Tautybë: [%s] Lytis: [%s]  Mirtys: [%d]"),
                p.age, p.nationality, p.sex, p.deaths)
        p.sendMessage(Color.WHITE, String.format("|VEIKËJAS| Áspëjimai: [%d] Atsiradimas: [%s] Forumo vardas: [%s] Dirþas: [%s] Kaukë: [%s] Kaukës vardas: [%s]",
                p.warns, spawnData.type.name, p.forumName, p.seatbelt, if(p.isMasked) "Taip" else "Ne", p.maskName))
        if(bankAccount != null) p.sendMessage(Color.WHITE, String.format("|FINANSAI| Grynieji pinigai: [%d%c]", player.money, Currency.SYMBOL))
        else p.sendMessage(Color.WHITE, String.format("|FINANSAI| Grynieji pinigai: [%d%c] Banko sàskaitoje: [%d%c] Sàskaitos numeris: [%s] Sukaupta alga: [%d]",
                player.money, Currency.SYMBOL, bankAccount.money, Currency.SYMBOL, bankAccount.number, p.totalPaycheck))
        if(bankAccount != null && bankAccount.deposit > 0)
            p.sendMessage(Color.WHITE, String.format("|FINANSAI| Indëlis: [%d%c] Palûkanos: [%d%%] Indëlio data: [%s]",
                    bankAccount.deposit, bankAccount.interest, bankAccount.depositTimestamp.toLocalDateTime().format(DateTimeFormatter.BASIC_ISO_DATE)))
        if(jobData != null) {
            val job = jobData.job
            if(job is ContractJob) {
                p.sendMessage(Color.WHITE, String.format("|DARBAS  | Pavadinimas: [%s] Kontraktas: [%d/%d] Rangas: [%s] Patirties taðkai: [%d]",
                        job.name, job.remainingContract, job.contractLength, job.rank.name, job.xp))
            } else if(job is Faction) {
                p.sendMessage(Color.WHITE, String.format("|FRAKCIJA| Pavadinimas: [%s] Rangas: [%s] Valandos: [%d]",
                        job.name, job.rank.name, job.hours))
            }

        }
        if(player.isAdmin || p.isMod || p.isFactionManager) {
            p.sendMessage(Color.WHITE, String.format("|ADMINISTRACIJA| Administratoriaus lygis: [%d] Moderatoriaus lygis: [%d] Frakcijø priþiûrëtojas: [%s]",
                    p.adminLevel, p.modLevel, if(p.isFactionManager) "Taip" else "Ne"))
            p.sendMessage(Color.WHITE, String.format("|ADMINISTRACIJA| Int: [%d] Virtualus pasaulis: [%d]",
                    player.location.interiorId, player.location.worldId))

        }
    }

    override fun show() {
        show(player)
    }


}