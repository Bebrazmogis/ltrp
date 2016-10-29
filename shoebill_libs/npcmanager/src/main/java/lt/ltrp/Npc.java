package lt.ltrp;

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Server;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.VehicleParam;

/**
 * @author Bebras
 *         2016.02.07.
 */
public class Npc {

    private String name;
    private String scriptName;
    private Vehicle vehicle;
    private Player player;

    public Npc(String name, String scriptName, Vehicle vehicle) {
        this.name = name;
        this.scriptName = scriptName;
        this.vehicle = vehicle;
        if(vehicle != null) {
            vehicle.getState().setDoors(VehicleParam.PARAM_ON);
        }
    }

    public Npc(String name, String scriptName) {
        this(name, scriptName, null);
    }

    public void connect(Server server) {
        server.connectNPC(name, scriptName);
    }


    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public String getScriptName() {
        return scriptName;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Player getPlayer() {
        return player;
    }
}
