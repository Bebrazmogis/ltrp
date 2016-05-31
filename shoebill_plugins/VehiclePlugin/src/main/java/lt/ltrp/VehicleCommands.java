package lt.ltrp;

import lt.ltrp.command.Commands;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.data.Color;
import lt.ltrp.object.LtrpPlayer;
import lt.ltrp.object.LtrpVehicle;
import net.gtaun.shoebill.common.command.BeforeCheck;
import net.gtaun.shoebill.common.command.Command;
import net.gtaun.shoebill.common.command.CommandHelp;
import net.gtaun.shoebill.common.command.CommandParameter;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.data.VehicleState;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.VehicleParam;
import net.gtaun.util.event.EventManager;

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
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 6.0f);
        if(vehicle != null) {
            return true;
        } else
            logger.debug("No vehicle near player");
        return false;
    }


    @Command
    @CommandHelp("Parodo transporto priemon�s inventori�")
    public boolean trunk(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        logger.info("trunk command called");
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
        if(vehicle != null) {
            if(vehicle.getState().getBoot() == VehicleParam.PARAM_ON) {
                vehicle.getInventory().show(player);
            } else
                player.sendErrorMessage(vehicle.getModelName() + " baga�in� u�daryta. Naudokite /trunko");
        } else
            player.sendErrorMessage("Prie j�s� n�ra jokios transporto priemon�s");
        return true;
    }

    @Command
    @CommandHelp("U�daro/atidaro automobilio baga�in�")
    public boolean trunko(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 6.0f);
        if(vehicle != null) {
            if(!vehicle.isLocked()) {
                if(vehicle.getState().getBoot() != VehicleParam.PARAM_ON) {
                    vehicle.getState().setBoot(VehicleParam.PARAM_ON);
                    player.sendActionMessage("atidaro " + vehicle.getModelName() + " baga�in�.");
                } else {
                    vehicle.getState().setBoot(VehicleParam.PARAM_OFF);
                    player.sendActionMessage("u�daro " + vehicle.getModelName() + " baga�in�.");
                }
                player.playSound(1057);
            } else {
                player.sendErrorMessage("Transporto priemon� u�rakinta.");
            }
        } else
            player.sendErrorMessage("Prie j�s� n�ra transporto priemon�s.");
        return true;
    }


    @Command
    @CommandHelp("U�daro/atidaro automobilio kapot�")
    public boolean bonnet(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle != null) {
            if(player.getState() == PlayerState.DRIVER) {
                VehicleParam state = vehicle.getState();
                if(state.getBonnet() != VehicleParam.PARAM_ON) {
                    state.setBonnet(VehicleParam.PARAM_ON);
                    player.sendActionMessage("atidaro " + vehicle.getModelName() + " kapot�.");
                } else {
                    state.setBonnet(VehicleParam.PARAM_OFF);
                    player.sendActionMessage("u�daro " + vehicle.getModelName() + " kapot�.");
                }
                player.playSound(1057);
            } else {
                player.sendErrorMessage("J�s neesate transporto priemon�s vairuotojas.");
            }
        } else
            player.sendErrorMessage("J�s� neesate transporto priemon�je.");
        return true;
    }


    @Command
    @CommandHelp("U�daro/atidaro automobilio langus")
    public boolean windows(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle != null) {
            if(LtrpVehicleModel.HasWindows(vehicle.getModelId())) {
                VehicleState windows = vehicle.getWindows();
                int seat = player.getVehicleSeat();
                if(seat < 4) {
                    int newstate = -1;
                    switch(seat) {
                        case 0:
                            if(windows.getDriver() != VehicleParam.PARAM_OFF) {
                                windows.setDriver(VehicleParam.PARAM_OFF);
                                newstate = VehicleParam.PARAM_OFF;
                            } else {
                                windows.setDriver(VehicleParam.PARAM_ON);
                                newstate = VehicleParam.PARAM_ON;
                            }
                            break;
                        case 1:
                            if(windows.getPassenger() != VehicleParam.PARAM_OFF) {
                                windows.setPassenger(VehicleParam.PARAM_OFF);
                                newstate = VehicleParam.PARAM_OFF;
                            } else {
                                windows.setPassenger(VehicleParam.PARAM_ON);
                                newstate = VehicleParam.PARAM_ON;
                            }
                            break;
                        case 2:
                            if(windows.getBackLeft() != VehicleParam.PARAM_OFF) {
                                windows.setBackLeft(VehicleParam.PARAM_OFF);
                                newstate = VehicleParam.PARAM_OFF;
                            } else {
                                windows.setBackLeft(VehicleParam.PARAM_ON);
                                newstate = VehicleParam.PARAM_ON;
                            }
                            break;
                        case 3:
                            if(windows.getBackRight() != VehicleParam.PARAM_OFF) {
                                windows.setBackRight(VehicleParam.PARAM_OFF);
                                newstate = VehicleParam.PARAM_OFF;
                            } else {
                                windows.setBackRight(VehicleParam.PARAM_ON);
                                newstate = VehicleParam.PARAM_ON;
                            }
                            break;
                    }
                    vehicle.setWindows(windows);
                    if(newstate == VehicleParam.PARAM_ON) {
                        player.sendActionMessage("u�daro tr. priemon�s lang�");
                    } else
                        player.sendActionMessage("atidaro tr. priemon�s lang�");
                    player.playSound(1057);
                } else {
                    player.sendErrorMessage("�is langas neatsidaro!");
                }
            } else {
                player.sendErrorMessage("J�s neesate transporto priemon�s vairuotojas.");
            }
        } else
            player.sendErrorMessage("J�s� neesate transporto priemon�je.");
        return true;
    }


    @Command
    @CommandHelp("U�sega/atesga suagos dir�us")
    public boolean seatbelt(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle != null) {
            if(player.getVehicleSeat() == 0) {
                if(!player.isSeatbelt()) {
                    player.sendActionMessage("patempia saugos dir�� ir juos u�sisega.");
                } else {
                    player.sendActionMessage("atsisega saugos dir�us.");
                }
                player.setSeatbelt(!player.isSeatbelt());
            } else
                player.sendErrorMessage("J�s neesate transporto priemon�s vairuotojas.");
        } else
            player.sendErrorMessage("J�s� neesate transporto priemon�je.");
        return true;
    }


    @Command
    @CommandHelp("Nustato/pa�alina maksimal� automobilio greit�")
    public boolean maxSpeed(Player p, @CommandParameter(name = "Maksimalus greitis")Float speed) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
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
        }
        return true;
    }


}
