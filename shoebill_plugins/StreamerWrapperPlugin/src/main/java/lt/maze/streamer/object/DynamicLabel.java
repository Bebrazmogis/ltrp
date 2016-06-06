package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.amx.types.ReferenceString;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicLabel extends AbstractStreamerItem implements StreamerItem {

    private static final Collection<DynamicLabel> labels = new ArrayList<>();

    public static Collection<DynamicLabel> get() {
        return labels;
    }

    public static DynamicLabel get(int id) {
        Optional<DynamicLabel> label = labels.stream().filter(l -> l.getId() == id).findFirst();
        return label.isPresent() ? label.get() : null;
    }

    //int CreateDynamic3DTextLabel(string text, int color, float x, float y, float z, float drawdistance, int attachedplayer, int attachedvehicle, int testlos, int worldid, int interiorid, int playerid, float streamdistance);
    public static DynamicLabel create(String text, Color color, float x, float y, float z, float drawdistance, Player attachedplayer, Vehicle attachedvehicle, boolean testlos, int worldid, int interiorid, Player p, float streamdistance) {
        int id = Functions.CreateDynamic3DTextLabel(
                text,
                color.getValue(),
                x,
                y,
                z,
                drawdistance,
                attachedplayer == null ? Player.INVALID_ID : attachedplayer.getId(),
                attachedvehicle == null ? Vehicle.INVALID_ID : attachedvehicle.getId(),
                testlos ? 1 :0,
                worldid,
                interiorid,
                p == null ? -1 : p.getId(),
                streamdistance
        );
        if(id == Constants.INVALID_STREAMER_ID) {
            throw new CreationFailedException("DynamicLabel could not be created");
        }
        DynamicLabel label = new DynamicLabel(id);
        labels.add(label);
        return label;
    }

    // CreateDynamic3DTextLabelEx(const text[], color, Float:x, Float:y, Float:z, Float:drawdistance, attachedplayer = INVALID_PLAYER_ID,
    //  attachedvehicle = INVALID_VEHICLE_ID, testlos = 0, Float:streamdistance = STREAMER_3D_TEXT_LABEL_SD, worlds[] = { -1 },
    //  interiors[] = { -1 }, players[] = { -1 }, maxworlds = sizeof worlds, maxinteriors = sizeof interiors, maxplayers = sizeof players);
    public static DynamicLabel create(String text, Color color, Vector3D location, float drawDistance, Player attachedPlayer, Vehicle attachedVehicle,
                                      boolean testLos, float streamDistance, Integer[] worlds, Integer[] interiors, Integer[] players) {
        int id = Functions.CreateDynamic3DTextLabelEx(
                text,
                color.getValue(),
                location.x,
                location.y,
                location.z,
                drawDistance,
                attachedPlayer != null ? attachedPlayer.getId() : Player.INVALID_ID,
                attachedVehicle != null ? attachedVehicle.getId() : Vehicle.INVALID_ID,
                testLos,
                streamDistance,
                worlds != null ? worlds : new Integer[]{ -1 },
                interiors != null ? interiors : new Integer[]{ -1 },
                players != null ? players : new Integer[]{ -1 },
                worlds != null ? worlds.length : 0,
                interiors != null ? interiors.length : 0,
                players != null ? players.length : 0
        );
        if(id == Constants.INVALID_STREAMER_ID) {
            throw new CreationFailedException("DynamicLabel could not be created");
        }
        DynamicLabel label = new DynamicLabel(id);
        labels.add(label);
        return label;
    }

    public static DynamicLabel create(String text, Color color, Vector3D position, float drawDistance, Player attachedPlayer, boolean testLos, float streamDistance, Integer[] players) {
        return create(text, color, position, drawDistance, attachedPlayer, null, testLos, streamDistance, null, null, players);
    }

    public static DynamicLabel create(String text, Color color, Vector3D vector3D, float drawDistance, Player attachedPlayer, Integer[] players) {
        return create(text, color, vector3D, drawDistance, attachedPlayer, true, Constants.STREAMER_3D_TEXT_LABEL_SD, players);
    }

    public static DynamicLabel create(String text, Color color, Location location, float drawDistance, Player attachedPlayer, boolean testLOS) {
        return create(text, color, location.x, location.y, location.z, drawDistance, attachedPlayer, null, testLOS, location.worldId, location.interiorId, null, Constants.STREAMER_3D_TEXT_LABEL_SD);
    }

    public static DynamicLabel create(String text, Color color, Location location, float drawDistance, Vehicle attachedVehicle, boolean testLOS) {
        return create(text, color, location.x, location.y, location.z, drawDistance, null, attachedVehicle, testLOS, location.worldId, location.interiorId, null, Constants.STREAMER_3D_TEXT_LABEL_SD);
    }

    public static DynamicLabel create(String text, Color color, Vector3D position, float drawdistance) {
        return create(text, color, position.x, position.y, position.z, drawdistance, null, null, false, -1, -1, null, Constants.STREAMER_3D_TEXT_LABEL_SD);
    }

    public static DynamicLabel create(String text, Color color, Vector3D position) {
        return create(text, color, position, 20f);
    }




    private DynamicLabel(int id) {
        super(id, StreamerType.Label);
    }

    public boolean isValid() {
        return Functions.IsValidDynamic3DTextLabel(getId()) == 1;
    }

    public String getText() {
        ReferenceString refS = new ReferenceString("", 0);
        Functions.GetDynamic3DTextLabelText(getId(), refS, refS.getLength());
        return refS.getValue();
    }

    public void update(Color color, String text) {
        Functions.UpdateDynamic3DTextLabelText(getId(), color.getValue(), text);
    }

    @Override
    public void destroy() {
        super.destroy();
        labels.remove(this);
        Functions.DestroyDynamic3DTextLabel(getId());
    }


}
