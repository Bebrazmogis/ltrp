package lt.ltrp.trucker.command

import lt.ltrp.`object`.Business
import lt.ltrp.`object`.LtrpPlayer
import lt.ltrp.`object`.LtrpVehicle
import lt.ltrp.constant.Currency
import lt.ltrp.trucker.TruckerPlayerLifecycleHolder
import lt.ltrp.trucker.TruckerVehicleLifecycleHolder
import lt.ltrp.trucker.`object`.Industry
import lt.ltrp.trucker.`object`.PlacedCargoBox
import lt.ltrp.trucker.`object`.TruckerJob
import lt.ltrp.trucker.constant.TruckerVehicleConstants
import lt.ltrp.trucker.controller.BusinessCommodityController
import lt.ltrp.trucker.controller.IndustryController
import lt.ltrp.trucker.dialog.TruckerVehicleCargoListDialog
import net.gtaun.shoebill.`object`.Player
import net.gtaun.shoebill.common.command.BeforeCheck
import net.gtaun.shoebill.common.command.Command
import net.gtaun.shoebill.common.command.CommandGroup
import net.gtaun.shoebill.common.command.CommandNotFoundHandler
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder
import net.gtaun.shoebill.constant.VehicleModel
import net.gtaun.shoebill.data.Color
import net.gtaun.util.event.EventManager

/**
 * @author Bebras
 * 2016.06.19.
 */
class TruckerCargoGroupCommands(private val truckerJob: TruckerJob,
                                private val eventManager: EventManager,
                                private val truckerHolder: TruckerPlayerLifecycleHolder,
                                private val vehicleHolder: TruckerVehicleLifecycleHolder): CommandGroup() {

    init {
        registerCommands(this)

        notFoundHandler = CommandNotFoundHandler { player, commandGroup, command ->
            player.sendMessage(Color.GREEN, "__________________________Kroviniø valdymas ir komandos__________________________")
            player.sendMessage(Color.WHITE, " TEISINGAS KOMANDOS NAUDOJIMAS: /cargo [KOMANDA], pavyzdþiui: /cargo list")
            player.sendMessage(Color.LIGHTGREY, "PAGRINDINËS KOMANDOS: info list, place, fork, unfork, putdown, pickup, buy, sell")
            player.sendMessage(Color.WHITE, "KITOS KOMANDOS: /trailer - priekabø valdymas")
            true
        }
    }

    @BeforeCheck fun bc(p: Player, cmd: String, params: String): Boolean {
        val player = LtrpPlayer.get(p)
        return player.jobData?.job == truckerJob
    }

    @Command
    fun info(p: Player) {

    }

    @Command
    fun list(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        var vehicle = LtrpVehicle.getByVehicle(player.vehicle)
        if(vehicle == null)
            vehicle = LtrpVehicle.getClosest(player, 5.0f)

        if(vehicle == null)
            player.sendErrorMessage("Prie jûsø nëra jokios transporto priemonës!")
        else if(!TruckerVehicleConstants.isTruckerVehicle(vehicle.modelId) &&
                VehicleModel.getType(vehicle.modelId) != VehicleModel.VehicleType.TRAILER)
            player.sendErrorMessage("Ði transporto priemonë tuðèia!")
        else if(vehicle.isLocked && !vehicle.isPlayerIn(player))
            player.sendErrorMessage("Transporto priemonë uþrakinta.")
        else {
            TruckerVehicleCargoListDialog.create(player, eventManager, vehicle, {
                caption { vehicle.modelName + " krovinys" }
                buttonOk { "Paimti" }
                buttonCancel { "Uþdaryti" }
                selectHandler { dialog, cargo ->
                    val trucker = truckerHolder.getObject(player)
                    if(player.isInAnyVehicle)
                        player.sendErrorMessage("Negalite paimti krovinio sëdëdamas transporto priemonëje.")
                    else if(!cargo.commodity.type.isCarryable)
                        player.sendErrorMessage("Paimti ðio krovinio negalite")
                    else if(trucker.cargo != null)
                        player.sendErrorMessage("Jûs jau kaþkà laikote!")
                    else {
                        cargo.amount--
                        trucker.cargo = cargo.commodity
                    }
                }
            })
            .show()
        }
        return true
    }


    @Command
    fun place(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val trucker = truckerHolder.getObject(player)
        val commodity = trucker.cargo
        val closestVehicle = vehicleHolder.getObject(LtrpVehicle.getClosest(player, 5f))

        if(player.isInAnyVehicle)
            player.sendErrorMessage("Ðios komandos transporto priemonëje naudoti negalite")
        else if(commodity == null)
            player.sendErrorMessage("Jûs rankose nelaikote jokio krovinio")
        else if(closestVehicle == null)
            player.sendErrorMessage("Prie jûsø nëra jokios transporto priemonës!")
        else if(!TruckerVehicleConstants.isTruckerVehicle((closestVehicle.vehicle.modelId)))
            player.sendErrorMessage("Ðio krovinio negalite ádëti á " + closestVehicle.vehicle.modelName)
        else if(closestVehicle.isFull(commodity.type))
            player.sendErrorMessage("Á " + closestVehicle.vehicle.modelName + " daugiau kroviniø nebetelpa")
        else {
            closestVehicle.addCargo(commodity)
            trucker.cargo = null
        }
        return true
    }

    @Command fun putDown(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val trucker = truckerHolder.getObject(player)
        if(trucker.cargo == null)
            player.sendErrorMessage("Jûs nelaikote jokio krovinio kurá galëtumëte padëti")
        else {
            PlacedCargoBox.create(trucker.cargo!!, player.location)
            player.sendActionMessage("padeda ant þemës dëþæ ant kurios paraðyta \"" + trucker.cargo!!.name + "\"")
            trucker.cargo = null
        }
        return true
    }

    @Command fun pickup(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val trucker = truckerHolder.getObject(player)
        val cargo = trucker.cargo
        if(cargo != null)
            player.sendErrorMessage("Jûs jau laikote kaþkà rankose!")
        else {
            val closestBox = PlacedCargoBox.cargoBoxes.minBy { it.location.distance(player.location) }
            if(closestBox == null || closestBox.location.distance(player.location) > 3f || closestBox.isDestroyed)
                player.sendErrorMessage("Prie jûsø nëra nieko kà galëtumëte paimti!")
            else {
                trucker.cargo = closestBox.commodity
                closestBox.destroy()
                player.sendActionMessage("pakelia nuo þemës dëþæ ant kurios paraðyta \"" + closestBox.commodity.name + "\"")
            }
        }
        return true
    }

    @Command fun buy(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val industry = IndustryController.INSTANCE.getClosest(player.location, 5f)

        if(industry == null)
            player.sendErrorMessage("Prie jûsø nëra jokios industrijos!")
        else {
            industry.showSoldCommodities(truckerHolder.getObject(player))
        }
        return true
    }

    @Command fun sell(p: Player): Boolean {
        val player = LtrpPlayer.get(p)
        val trucker = truckerHolder.getObject(player)
        val business = Business.getClosest(player.location, 5f)
        val industry = IndustryController.INSTANCE.getClosest(player.location, 10f)
        val commodity = trucker.cargo

        if(business != null) {
            if(commodity == null)
                player.sendErrorMessage("Jûs nelaikote jokios prekës!")
            else if(business.resourcePrice == 0)
                player.sendErrorMessage("Verslas neperka jokiø prekiø!")
            else if(BusinessCommodityController.instance.get(business.businessType) != commodity)
                player.sendErrorMessage("Verslas ðios prekës neperka!")
            else {
                business.addResources()
                player.giveMoney(business.resourcePrice)
                player.sendMessage(Color.BURLYWOOD, "\"" + commodity.name + "\" parduota uþ " + business.resourcePrice + Currency.SYMBOL)
                trucker.cargo = null
            }

        } else if(industry != null) {
            val vehicle = LtrpVehicle.getByVehicle(player.vehicle)
            if(vehicle != null) {
                TruckerVehicleCargoListDialog.create(player, eventManager, vehicle, {
                    caption { "Pasirinkitæ prekæ kurià norite parduoti" }
                    buttonOk { "Parduoti" }
                    buttonCancel { "Uþdaryti" }
                    selectHandler{ d, cargo ->
                        val industryCommodity = industry.boughtStock.firstOrNull{ cargo.commodity == it }
                        if(industryCommodity == null)
                            player.sendErrorMessage("Industrija ðios prekës neperka!")
                        else if(industryCommodity.currentStock >= industryCommodity.maxStock)
                            player.sendErrorMessage("Industrijos sandëlis ðiai prekei pilnas!")
                        // Maybe someone took it out while he was looking at the player-stats
                        else if(cargo.amount == 0)
                            player.sendErrorMessage("Ðios prekës sunkveþimyje nebëra!")
                        else {
                            cargo.amount--
                            industryCommodity.currentStock++
                            player.giveMoney(industryCommodity.price)
                            player.sendMessage("Parduotvëtæ prekæ \"" + industryCommodity.cargo.name + "\" uþ " + industryCommodity.price + Currency.SYMBOL)
                            d.show()
                        }
                    }
                }).show()
            } else if(!player.isInAnyVehicle) {
                if(trucker.cargo == null)
                    player.sendErrorMessage("Jûs nelaikote jokios prekës!")
                else {
                    val industryCommodity = industry.boughtStock.firstOrNull{ trucker.cargo == it }
                    if(industryCommodity == null)
                        player.sendErrorMessage("Ði industrija neperka jûsø laikomos prekës")
                    else if(industryCommodity.currentStock >= industryCommodity.maxStock)
                        player.sendErrorMessage("Daugiau prekiø á ðià industrijà netelpa!")
                    else {
                        industryCommodity.currentStock++
                        player.giveMoney(industryCommodity.price)
                        trucker.cargo = null
                        player.sendMessage("Prekë parduota uþ " + industryCommodity.price + Currency.SYMBOL)
                    }
                }
            } else {
                player.sendErrorMessage("Jûs nelaikote prekës ir neesate transporto priemonëje kuri gali turëti kroviná!")
            }
        }
        return true
    }
}