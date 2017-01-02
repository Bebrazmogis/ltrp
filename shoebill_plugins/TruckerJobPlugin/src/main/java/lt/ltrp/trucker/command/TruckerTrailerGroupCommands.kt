package lt.ltrp.trucker.command;

import lt.ltrp.player.vehicle.PlayerVehiclePlugin
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.player.vehicle.`object`.PlayerVehicle
import lt.ltrp.constant.Currency
import lt.ltrp.player.vehicle.constant.PlayerVehiclePermission
import lt.ltrp.data.Color
import lt.ltrp.trucker.data.PlayerBuyTrailerOFfer
import lt.ltrp.trucker.dialog.TruckerVehicleCargoListDialog
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.`object`.VehicleParam
import net.gtaun.shoebill.common.command.BeforeCheck
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandGroup
import net.gtaun.shoebill.common.command.CommandParameter
import net.gtaun.util.event.EventManager

/**
 * Created by Bebras on 2016-11-06.
 * Commands for interacting with a trailer
 * Commands should only work if a player is inside a vehicle
 * And the vehicle has an attached trailer
 */
class TruckerTrailerGroupCommands(private val eventManager: EventManager):
        CommandGroup() {

    init {
        registerCommands(this)

        setNotFoundHandler { player, group, cmd ->
            player.sendMessage(Color.GREEN, "__________________________Trailer Cargo komandos ir naudojimas__________________________")
            player.sendMessage(Color.WHITE, "  KOMANDOS NAUDOJIMAS: /trailer [komanda], pavyzd�iui: /trailer cargo")
            player.sendMessage(Color.GRAY, "  PAGRINDINES: lock, detach, lights, cargo sellto")
            true
        }
    }

    @BeforeCheck fun bc(p: Player, cmd: String, parmas: String): Boolean {
        val player = LtrpPlayer.get(p)
        val hauler = LtrpVehicle.getByVehicle(player.vehicle)
        val trailer = LtrpVehicle.getByVehicle(hauler?.trailer)
        if(hauler == null || player.vehicleSeat != 0)
            player.sendErrorMessage("�i� komand� galite naudoti tik sed�damas vilkike u� vairo!")
        else if(trailer == null)
            player.sendErrorMessage("Prie j�s� transporto priemon�s neprikabinta jokia priekaba!")
        else
            return true

        return false
    }

    @Command fun lock(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val hauler = LtrpVehicle.getByVehicle(player.vehicle)
        val trailer = LtrpVehicle.getByVehicle(hauler?.trailer)
        if(hauler == null || trailer == null)
            return false

        if(!PlayerVehicle.getByVehicle(trailer).getPermissions(player.UUID).contains(PlayerVehiclePermission.Lock))
            player.sendErrorMessage("Priekaba jums nepriklauso tod�l negalite jos u�rakinti/atrakinti")

        trailer.isLocked = !trailer.isLocked
        if(trailer.isLocked)
            player.sendInfoText("~r~UZRAKINTA")
        else
            player.sendInfoText("~g~ATAKINTA")
        player.playSound(1052)
        return true
    }

    @Command fun detach(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val hauler = LtrpVehicle.getByVehicle(player.vehicle)
        val trailer = LtrpVehicle.getByVehicle(hauler?.trailer)
        if(hauler == null || trailer == null)
            return false

        hauler.detachTrailer()
        player.playSound(1052) // TODO find better sound
        return true
    }

    @Command fun lights(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val hauler = LtrpVehicle.getByVehicle(player.vehicle)
        val trailer = LtrpVehicle.getByVehicle(hauler?.trailer)
        if(hauler == null || trailer == null)
            return false

        if(trailer.state.lights == VehicleParam.PARAM_ON) {
            trailer.state.lights = VehicleParam.PARAM_OFF
        } else {
            trailer.state.lights = VehicleParam.PARAM_ON
        }
        return true
    }

    @Command fun cargo(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val hauler = LtrpVehicle.getByVehicle(player.vehicle)
        val trailer = LtrpVehicle.getByVehicle(hauler?.trailer)
        if(hauler == null || trailer == null)
            return false

        TruckerVehicleCargoListDialog.create(player, eventManager, trailer, {
            caption { "Kroviniai" }
            buttonOk { "Gerai" }
            buttonCancel { "U�daryti" }
        }).show()
        return true
    }

    @Command fun sellTo(p: Player, @CommandParameter(name = "�aid�jo ID/Dalis Vardo")target: LtrpPlayer,
                        @CommandParameter(name = "Kaina")price: Int): Boolean {
        val player = LtrpPlayer.get(p)
        val hauler = LtrpVehicle.getByVehicle(player.vehicle)
        val trailer = PlayerVehicle.getByVehicle(hauler?.trailer)
        if(hauler == null || trailer == null)
            return false

        if(trailer.ownerId != player.UUID)
            player.sendErrorMessage("�i priekaba jums nepriklauso")

        else if(target.location.distance(player.location) > 5f)
            player.sendErrorMessage(target.name + " yra per toli.")

        else if(target.containsOffer(PlayerBuyTrailerOFfer::class.java))
            player.sendErrorMessage(target.name + " jau yra gav�s pasi�lym� pirkti priekab�.")

        else if(price < 0)
            player.sendErrorMessage("Kaina negali b�ti neigiama.")

        else {
            val offer = PlayerBuyTrailerOFfer(target, player, trailer, price, eventManager)
            target.offers.add(offer)
            player.sendMessage("Pasi�lymas pirkti j�s� priekab� u� " + price + Currency.SYMBOL + " �aid�jui " + target.name + " i�si�stas, laukite atsakymo")
            target.sendMessage(player.name + " si�lo jums pirkti priekab� u� " + price + Currency.SYMBOL + ", naudokite /accept trailer sutikti")
        }
        return true
    }

}
