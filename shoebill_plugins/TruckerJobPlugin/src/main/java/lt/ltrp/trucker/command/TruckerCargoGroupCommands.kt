package lt.ltrp.trucker.command

import net.gtaun.shoebill.common.command.CommandGroup
import net.gtaun.shoebill.common.command.CommandNotFoundHandler
import net.gtaun.shoebill.data.Color

/**
 * @author Bebras
 * 2016.06.19.
 */
class TruckerCargoGroupCommands: CommandGroup() {

    init {
        registerCommands(this)

        notFoundHandler = CommandNotFoundHandler { player, commandGroup, command ->
            player.sendMessage(Color.GREEN, "__________________________Krovini� valdymas ir komandos__________________________")
            player.sendMessage(Color.WHITE, " TEISINGAS KOMANDOS NAUDOJIMAS: /cargo [KOMANDA], pavyzd�iui: /cargo list")
            player.sendMessage(Color.LIGHTGREY, "PAGRINDIN�S KOMANDOS: info list, place, fork, unfork, putdown, pickup, buy, sell")
            player.sendMessage(Color.WHITE, "KITOS KOMANDOS: /trailer - priekab� valdymas")
            true
        }
    }


}