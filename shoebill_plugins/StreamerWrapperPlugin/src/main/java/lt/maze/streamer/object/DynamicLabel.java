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
