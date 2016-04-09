package lt.ltrp.plugin.streamer;

import lt.ltrp.util.PawnFunc;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Bebras on 2015.06.23.
 * This class is a wrapper for Incognito Streamer plugin for SAMP
 */
public class DynamicLabel implements Destroyable {

    private static List<DynamicLabel> list = new ArrayList<DynamicLabel>();
    private static Logger logger = Logger.getLogger("DynamicLabel");

    private int id;
    private String text;
    private Color color;
    private Location location;
    private float drawDistance;
    private boolean isDestroyed, testLOS;
    private Player player;
    private Vehicle vehicle;

    public static DynamicLabel create(String text, Color color, float x, float y, float z, int worldId, int interiorId, float drawDistance, boolean testLOS) {
        return create(text, color, new Location(x, y, z, interiorId, worldId), drawDistance, testLOS, null, null);
    }

    public static DynamicLabel create(String text, Color color, Location loc, float drawDistance, boolean testLOS) {
       return create(text, color, loc, drawDistance, testLOS, null, null);
    }

    //native Text3D:CreateDynamic3DTextLabel(const text[], color, Float:x, Float:y, Float:z, Float:drawdistance, attachedplayer = INVALID_PLAYER_ID, attachedvehicle = INVALID_VEHICLE_ID, testlos = 0, worldid = -1, interiorid = -1, playerid = -1, Float:streamdistance = 100.0);
    public static DynamicLabel create(String text, Color color, Location loc, float drawDistance, boolean testLOS, Player player, Vehicle vehicle) {
        AmxCallable function = PawnFunc.getNativeMethod("CreateDynamic3DTextLabel");
        if(function != null) {
            int playerid = (player != null) ? (player.getId()) : (Player.INVALID_ID);
            int vehicleid = (vehicle != null) ? (vehicle.getId()) : (Vehicle.INVALID_ID);
            int id = (int)function.call(text, color.toRgbValue(), loc.getX(), loc.getY(), loc.getZ(), drawDistance, playerid, vehicleid, testLOS, loc.getWorldId(), loc.getInteriorId());
            DynamicLabel label = new DynamicLabel(text, color, loc, drawDistance, false, testLOS, player, vehicle);
            list.add(label);
            return label;
        } else {
            errLog("CreateDynamic3DTextLabel function not found");
            return null;
        }
    }

    public static DynamicLabel create(String text, Color color, Vector3D pos, int worldId, int interiorId, float drawDistance, boolean testLOS) {
        return create(text, color, new Location(pos, interiorId, worldId), drawDistance, testLOS, null, null);
    }

    public static DynamicLabel find(int id) {
        for(DynamicLabel label : list) {
            if(label.getId() == id) {
                return label;
            }
        }
        return null;
    }

    private DynamicLabel(String text, Color color, Location location, float drawDistance, boolean isDestroyed, boolean testLOS, Player player, Vehicle vehicle) {
        this.text = text;
        this.color = color;
        this.location = location;
        this.drawDistance = drawDistance;
        this.isDestroyed = isDestroyed;
        this.testLOS = testLOS;
        this.player = player;
        this.vehicle = vehicle;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public Location getLocation() {
        return location;
    }

    public float getDrawDistance() {
        return drawDistance;
    }

    public boolean isTestLOS() {
        return testLOS;
    }

    public Vehicle getAttachedVehicle() {
        return vehicle;
    }

    public Player getAttachedPlayer() {
        return player;
    }

    // native GetDynamic3DTextLabelText(Text3D:id, text[], maxtext = sizeof text);
    public String getText() {
        return text;
    }

    // native UpdateDynamic3DTextLabelText(Text3D:id, color, const text[]);
    public void update(Color color, String text) {
        AmxCallable function = PawnFunc.getNativeMethod("UpdateDynamic3DTextLabelText");
        if(function != null) {
            function.call(this.getId(), getColor().toRgbValue(), getText());
        } else {
            errLog("UpdateDynamic3DTextLabelText function not found");
        }
    }

    public void update(Color color) {
        this.update(color, getText());
    }

    public void update(String text) {
        this.update(getColor(), text);
    }

    @Override
    // native DestroyDynamic3DTextLabel(Text3D:id);
    public void destroy() {
        AmxCallable function = PawnFunc.getNativeMethod("DestroyDynamic3DTextLabel");
        if(function != null) {
            function.call(this.getId());
            isDestroyed = true;
        } else {
            errLog("DestroyDynamic3DTextLabel function not found");
        }
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    private static void errLog(String s) {
        logger.log(Level.SEVERE, s);
    }
}
