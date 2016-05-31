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
    @CommandHelp("Parodo transporto priemonës inventoriø")
    public boolean trunk(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        logger.info("trunk command called");
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 4.0f);
        if(vehicle != null) {
            if(vehicle.getState().getBoot() == VehicleParam.PARAM_ON) {
                vehicle.getInventory().show(player);
            } else
                player.sendErrorMessage(vehicle.getModelName() + " bagaþinë uþdaryta. Naudokite /trunko");
        } else
            player.sendErrorMessage("Prie jûsø nëra jokios transporto priemonës");
        return true;
    }

    @Command
    @CommandHelp("Uþdaro/atidaro automobilio bagaþinæ")
    public boolean trunko(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getClosest(player, 6.0f);
        if(vehicle != null) {
            if(!vehicle.isLocked()) {
                if(vehicle.getState().getBoot() != VehicleParam.PARAM_ON) {
                    vehicle.getState().setBoot(VehicleParam.PARAM_ON);
                    player.sendActionMessage("atidaro " + vehicle.getModelName() + " bagaþinæ.");
                } else {
                    vehicle.getState().setBoot(VehicleParam.PARAM_OFF);
                    player.sendActionMessage("uþdaro " + vehicle.getModelName() + " bagaþinæ.");
                }
                player.playSound(1057);
            } else {
                player.sendErrorMessage("Transporto priemonë uþrakinta.");
            }
        } else
            player.sendErrorMessage("Prie jûsø nëra transporto priemonës.");
        return true;
    }


    @Command
    @CommandHelp("Uþdaro/atidaro automobilio kapotà")
    public boolean bonnet(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle != null) {
            if(player.getState() == PlayerState.DRIVER) {
                VehicleParam state = vehicle.getState();
                if(state.getBonnet() != VehicleParam.PARAM_ON) {
                    state.setBonnet(VehicleParam.PARAM_ON);
                    player.sendActionMessage("atidaro " + vehicle.getModelName() + " kapotà.");
                } else {
                    state.setBonnet(VehicleParam.PARAM_OFF);
                    player.sendActionMessage("uþdaro " + vehicle.getModelName() + " kapotà.");
                }
                player.playSound(1057);
            } else {
                player.sendErrorMessage("Jûs neesate transporto priemonës vairuotojas.");
            }
        } else
            player.sendErrorMessage("Jûsø neesate transporto priemonëje.");
        return true;
    }


    @Command
    @CommandHelp("Uþdaro/atidaro automobilio langus")
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
                        player.sendActionMessage("uþdaro tr. priemonës langà");
                    } else
                        player.sendActionMessage("atidaro tr. priemonës langà");
                    player.playSound(1057);
                } else {
                    player.sendErrorMessage("Ðis langas neatsidaro!");
                }
            } else {
                player.sendErrorMessage("Jûs neesate transporto priemonës vairuotojas.");
            }
        } else
            player.sendErrorMessage("Jûsø neesate transporto priemonëje.");
        return true;
    }


    @Command
    @CommandHelp("Uþsega/atesga suagos dirþus")
    public boolean seatbelt(Player p) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
        if(vehicle != null) {
            if(player.getVehicleSeat() == 0) {
                if(!player.isSeatbelt()) {
                    player.sendActionMessage("patempia saugos dirþà ir juos uþsisega.");
                } else {
                    player.sendActionMessage("atsisega saugos dirþus.");
                }
                player.setSeatbelt(!player.isSeatbelt());
            } else
                player.sendErrorMessage("Jûs neesate transporto priemonës vairuotojas.");
        } else
            player.sendErrorMessage("Jûsø neesate transporto priemonëje.");
        return true;
    }


    @Command
    @CommandHelp("Nustato/paðalina maksimalø automobilio greitá")
    public boolean maxSpeed(Player p, @CommandParameter(name = "Maksimalus greitis")Float speed) {
        LtrpPlayer player = LtrpPlayer.get(p);
        LtrpVehicle vehicle = LtrpVehicle.getByVehicle(player.getVehicle());
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
        }
        return true;
    }


}
