package lt.ltrp.vehicle;

import lt.ltrp.command.Commands;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.data.Color;
import lt.ltrp.dialog.radio.RadioOptionListDialog;
import lt.ltrp.item.ItemType;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.data.VehicleState;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManager;

import java.awt.*;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class VehicleCommands extends Commands {

    private Map<LtrpPlayer, Float> maxSpeeds;
    private EventManager eventManager;

    public VehicleCommands(Map<LtrpPlayer, Float> speeds, EventManager eventManager) {
        this.maxSpeeds = speeds;
        this.eventManager = eventManager;
    }



    @BeforeCheck
    public boolean beforeCheck(Player p, String cmd, String params) {
        logger.debug("beforeCheck cmd " + cmd);
        logger.debug("beforeCheck. Player find by player instance" + LtrpPlayer.get(p));
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
        if(vehicle != null) {
            return true;
        } else
            logger.debug("No vehicle near player");
        return false;
    }


    @Command
    @CommandHelp("Parodo transporto priemonës inventoriø")
    public boolean trunk(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        logger.info("trunk command called");
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
        if(vehicle != null) {
            if(vehicle.getState().getBoot() == VehicleParam.PARAM_ON) {
                vehicle.getInventory().show(player);
                return true;
            } else
                player.sendErrorMessage(vehicle.getModelName() + " uþrakinta. Naudokite /trunko");
        } else
            player.sendErrorMessage("Prie jûsø nëra jokios transporto priemonës");
        return false;
    }

    @Command
    @CommandHelp("Uþdaro/atidaro automobilio bagaþinæ")
    public boolean trunko(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
        if(vehicle != null) {
            if(!vehicle.isLocked()) {
                if(vehicle.getState().getBoot() != VehicleParam.PARAM_ON) {
                    vehicle.getState().setBoot(VehicleParam.PARAM_OFF);
                    player.sendActionMessage("uþdaro " + vehicle.getModelName() + " bagaþinæ.");
                } else {
                    vehicle.getState().setBoot(VehicleParam.PARAM_ON);
                    player.sendActionMessage("atidaro " + vehicle.getModelName() + " bagaþinæ.");
                }
                player.playSound(1057);
                return true;
            } else {
                player.sendErrorMessage("Transporto priemonë uþrakinta.");
            }
        } else
            player.sendErrorMessage("Prie jûsø nëra transporto priemonës.");
        return false;
    }


    @Command
    @CommandHelp("Uþdaro/atidaro automobilio kapotà")
    public boolean bonnet(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = player.getVehicle();
        if(vehicle != null) {
            if(player.getState() != PlayerState.DRIVER) {
                if(vehicle.getState().getBonnet() != VehicleParam.PARAM_ON) {
                    vehicle.getState().setBonnet(VehicleParam.PARAM_OFF);
                    player.sendActionMessage("uþdaro " + vehicle.getModelName() + " kapotà.");
                } else {
                    vehicle.getState().setBonnet(VehicleParam.PARAM_ON);
                    player.sendActionMessage("atidaro " + vehicle.getModelName() + " kapotà.");
                }
                player.playSound(1057);
                return true;
            } else {
                player.sendErrorMessage("Jûs neesate transporto priemonës vairuotojas.");
            }
        } else
            player.sendErrorMessage("Jûsø neesate transporto priemonëje.");
        return false;
    }


    @Command
    @CommandHelp("Uþdaro/atidaro automobilio langus")
    public boolean windows(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = player.getVehicle();
        if(vehicle != null) {
            if(LtrpVehicleModel.HasWindows(vehicle.getModelId())) {
                VehicleState windows = vehicle.getWindows();
                int seat = player.getVehicleSeat();
                if(seat < 4) {
                    int newstate = -1;
                    switch(seat) {
                        case 0:
                            if(windows.getDriver() == VehicleParam.PARAM_ON) {
                                windows.setDriver(VehicleParam.PARAM_OFF);
                                newstate = VehicleParam.PARAM_OFF;
                            } else {
                                windows.setDriver(VehicleParam.PARAM_ON);
                                newstate = VehicleParam.PARAM_ON;
                            }
                            break;
                        case 1:
                            if(windows.getPassenger() == VehicleParam.PARAM_ON) {
                                windows.setPassenger(VehicleParam.PARAM_OFF);
                                newstate = VehicleParam.PARAM_OFF;
                            } else {
                                windows.setPassenger(VehicleParam.PARAM_ON);
                                newstate = VehicleParam.PARAM_ON;
                            }
                            break;
                        case 2:
                            if(windows.getBackLeft() == VehicleParam.PARAM_ON) {
                                windows.setBackLeft(VehicleParam.PARAM_OFF);
                                newstate = VehicleParam.PARAM_OFF;
                            } else {
                                windows.setBackLeft(VehicleParam.PARAM_ON);
                                newstate = VehicleParam.PARAM_ON;
                            }
                            break;
                        case 3:
                            if(windows.getBackRight() == VehicleParam.PARAM_ON) {
                                windows.setBackRight(VehicleParam.PARAM_OFF);
                                newstate = VehicleParam.PARAM_OFF;
                            } else {
                                windows.setBackRight(VehicleParam.PARAM_ON);
                                newstate = VehicleParam.PARAM_ON;
                            }
                            break;
                    }
                    if(newstate == VehicleParam.PARAM_ON) {
                        player.sendActionMessage("atidaro langà");
                    } else
                        player.sendActionMessage("uþdaro langà");
                    player.playSound(1057);
                    return true;
                }
            } else {
                player.sendErrorMessage("Jûs neesate transporto priemonës vairuotojas.");
            }
        } else
            player.sendErrorMessage("Jûsø neesate transporto priemonëje.");
        return false;
    }


    @Command
    @CommandHelp("Uþsega/atesga suagos dirþus")
    public boolean seatbelt(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = player.getVehicle();
        if(vehicle != null) {
            if(player.getVehicleSeat() == 0) {
                if(!player.getSeatbelt()) {
                    player.sendActionMessage("patempia saugos dirþà ir juos uþsisega.");
                } else {
                    player.sendActionMessage("atsisega saugos dirþus.");
                }
                player.setSeatbelt(!player.getSeatbelt());
            } else
                player.sendErrorMessage("Jûs neesate transporto priemonës vairuotojas.");
        } else
            player.sendErrorMessage("Jûsø neesate transporto priemonëje.");
        return false;
    }


    @Command
    @CommandHelp("Nustato/paðalina maksimalø automobilio greitá")
    public boolean maxSpeed(Player p, Float speed) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = player.getVehicle();
        if(vehicle == null) {
            player.sendErrorMessage("Jûs neesate transporto priemonëje!");
        } else if(speed == null) {
            player.sendErrorMessage("Naudojimas /maxspeed [Greitis(30 - 130)]");
        } else if(speed < 30 || speed > 130) {
            if(maxSpeeds.containsKey(player)) {
                maxSpeeds.remove(player);
                player.sendMessage(Color.NEWS, "Greièio ribotuvas iðjungtas.");
            }
        } else {
            maxSpeeds.put(player, speed);
            player.sendMessage(Color.WHITE, "* Greièio ribotuvas buvo nustatytas: " + speed + " Km/h ");
            return true;
        }
        return false;
    }

    @Command
    @CommandHelp("Leidþia valdyti automobilio radijà")
    public boolean vradio(Player pp) {
        LtrpPlayer player = LtrpPlayer.get(pp);
        LtrpVehicle vehicle=  LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle == null) {
            player.sendErrorMessage("Jûs turite bûti transporto priemonëje!");
        } else if(!vehicle.getInventory().containsType(ItemType.CarAudio)) {
            player.sendErrorMessage("Ðiame automobilyje nëra automagnetolos.");
        } else if(player.getVehicleSeat() > 1) {
            player.sendErrorMessage("Negalite valdyti radijos sedëdamas gale!");
        } else {
            RadioOptionListDialog.create(player, eventManager, (d, vol) -> {
                vehicle.setRadioVolume(vol);
                player.sendActionMessage("pakeièia radijos garsà á " + vol);
            }, (d, station) -> {
                vehicle.setRadioStation(station);
                player.sendActionMessage("pakeièia radijo stotá á " + station.getName());
            }, (d) -> {
                vehicle.setRadioStation(null);
            });
        }
        return true;
    }
}
