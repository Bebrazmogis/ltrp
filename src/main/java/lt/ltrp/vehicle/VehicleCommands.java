package lt.ltrp.vehicle;

import lt.ltrp.command.Commands;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.data.Color;
import lt.ltrp.player.LtrpPlayer;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.data.VehicleState;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.VehicleParam;

import java.util.Map;

/**
 * @author Bebras
 *         2015.12.06.
 */
public class VehicleCommands extends Commands {

    private Map<LtrpPlayer, Float> maxSpeeds;

    public VehicleCommands(Map<LtrpPlayer, Float> speeds) {
        this.maxSpeeds = speeds;
    }



    @BeforeCheck
    public boolean beforeCheck(LtrpPlayer p, String cmd, String params) {
        logger.debug("beforeCheck cmd " + cmd);
        logger.debug("beforeCheck. Player find by player instance" + LtrpPlayer.get(p));
        LtrpVehicle vehicle = LtrpVehicle.getClosest(p, 4.0f);
        if(vehicle != null) {
            return true;
        } else
            logger.debug("No vehicle near player");
        return false;
    }


    @Command
    @CommandHelp("Parodo transporto priemon�s inventori�")
    public boolean trunk(LtrpPlayer player) {
        logger.info("trunk command called");
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
        if(vehicle != null) {
            if(vehicle.getState().getBoot() == VehicleParam.PARAM_ON) {
                vehicle.getInventory().show(player);
                return true;
            } else
                player.sendErrorMessage(vehicle.getModelName() + " u�rakinta. Naudokite /trunko");
        } else
            player.sendErrorMessage("Prie j�s� n�ra jokios transporto priemon�s");
        return false;
    }

    @Command
    @CommandHelp("U�daro/atidaro automobilio baga�in�")
    public boolean trunko(LtrpPlayer player) {
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
        if(vehicle != null) {
            if(!vehicle.isLocked()) {
                if(vehicle.getState().getBoot() != VehicleParam.PARAM_ON) {
                    vehicle.getState().setBoot(VehicleParam.PARAM_OFF);
                    player.sendActionMessage("u�daro " + vehicle.getModelName() + " baga�in�.");
                } else {
                    vehicle.getState().setBoot(VehicleParam.PARAM_ON);
                    player.sendActionMessage("atidaro " + vehicle.getModelName() + " baga�in�.");
                }
                player.playSound(1057);
                return true;
            } else {
                player.sendErrorMessage("Transporto priemon� u�rakinta.");
            }
        } else
            player.sendErrorMessage("Prie j�s� n�ra transporto priemon�s.");
        return false;
    }


    @Command
    @CommandHelp("U�daro/atidaro automobilio kapot�")
    public boolean bonnet(LtrpPlayer player) {
        LtrpVehicle vehicle = player.getVehicle();
        if(vehicle != null) {
            if(player.getState() != PlayerState.DRIVER) {
                if(vehicle.getState().getBonnet() != VehicleParam.PARAM_ON) {
                    vehicle.getState().setBonnet(VehicleParam.PARAM_OFF);
                    player.sendActionMessage("u�daro " + vehicle.getModelName() + " kapot�.");
                } else {
                    vehicle.getState().setBonnet(VehicleParam.PARAM_ON);
                    player.sendActionMessage("atidaro " + vehicle.getModelName() + " kapot�.");
                }
                player.playSound(1057);
                return true;
            } else {
                player.sendErrorMessage("J�s neesate transporto priemon�s vairuotojas.");
            }
        } else
            player.sendErrorMessage("J�s� neesate transporto priemon�je.");
        return false;
    }


    @Command
    @CommandHelp("U�daro/atidaro automobilio langus")
    public boolean windows(LtrpPlayer player) {
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
                        player.sendActionMessage("atidaro lang�");
                    } else
                        player.sendActionMessage("u�daro lang�");
                    player.playSound(1057);
                    return true;
                }
            } else {
                player.sendErrorMessage("J�s neesate transporto priemon�s vairuotojas.");
            }
        } else
            player.sendErrorMessage("J�s� neesate transporto priemon�je.");
        return false;
    }


    @Command
    @CommandHelp("U�sega/atesga suagos dir�us")
    public boolean seatbelt(LtrpPlayer player) {
        LtrpVehicle vehicle = player.getVehicle();
        if(vehicle != null) {
            if(player.getVehicleSeat() == 0) {
                if(!player.getSeatbelt()) {
                    player.sendActionMessage("patempia saugos dir�� ir juos u�sisega.");
                } else {
                    player.sendActionMessage("atsisega saugos dir�us.");
                }
                player.setSeatbelt(!player.getSeatbelt());
            } else
                player.sendErrorMessage("J�s neesate transporto priemon�s vairuotojas.");
        } else
            player.sendErrorMessage("J�s� neesate transporto priemon�je.");
        return false;
    }


    @Command
    @CommandHelp("Nustato/pa�alina maksimal� automobilio greit�")
    public boolean maxSpeed(LtrpPlayer player, Float speed) {
        LtrpVehicle vehicle = player.getVehicle();
        if(vehicle == null) {
            player.sendErrorMessage("J�s neesate transporto priemon�je!");
        } else if(speed == null) {
            player.sendErrorMessage("Naudojimas /maxspeed [Greitis(30 - 130)]");
        } else if(speed < 30 || speed > 130) {
            if(maxSpeeds.containsKey(player)) {
                maxSpeeds.remove(player);
                player.sendMessage(Color.NEWS, "Grei�io ribotuvas i�jungtas.");
            }
        } else {
            maxSpeeds.put(player, speed);
            player.sendMessage(Color.WHITE, "* Grei�io ribotuvas buvo nustatytas: " + speed + " Km/h ");
            return true;
        }
        return false;
    }
}
