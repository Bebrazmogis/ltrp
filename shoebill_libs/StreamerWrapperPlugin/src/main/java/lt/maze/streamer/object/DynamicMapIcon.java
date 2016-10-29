package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerType;
import net.gtaun.shoebill.constant.MapIconStyle;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.02.16.
 */
public class DynamicMapIcon extends AbstractStreamerItem implements StreamerItem {

    private static Collection<DynamicMapIcon> mapIcons = new ArrayList<>();

    public static Collection<DynamicMapIcon> get() {
        return mapIcons;
    }

    public static DynamicMapIcon get(int id) {
        Optional<DynamicMapIcon> icon = mapIcons.stream().filter(l -> l.getId() == id).findFirst();
        return icon.isPresent() ? icon.get() : null;
    }

    //CreateDynamicMapIcon(float x, float y, float z, int type, int color, int worldid, int interiorid, int playerid, float streamdistance, int style);
    public static DynamicMapIcon create(float x, float y, float z, int type, Color color, int worldid, int interiorid, Player p, float streamdistance, MapIconStyle style) {
        int id = Functions.CreateDynamicMapIcon(x, y, z, type, color.getValue(), worldid, interiorid, p == null ? -1 : p.getId(), streamdistance, style.getValue());
        if(id == Constants.INVALID_STREAMER_ID) {
            throw new CreationFailedException("DynamicLabel could not be created");
        }
        DynamicMapIcon mapIcon = new DynamicMapIcon(id);
        mapIcons.add(mapIcon);
        return mapIcon;
    }

    public static DynamicMapIcon create(float x, float y, float z, int type, Color color) {
        return create(x, y, z, type, color, -1, -1, null, Constants.STREAMER_MAP_ICON_SD, MapIconStyle.LOCAL);
    }

    public static DynamicMapIcon create(Vector3D position, int type, Color color) {
        return create(position.x, position.y, position.z, type, color);
    }



    private DynamicMapIcon(int id) {
        super(id, StreamerType.MapIcon);
    }


    public boolean isValid() {
        return Functions.IsValidDynamicMapIcon(getId()) == 1;
    }

    @Override
    public void destroy() {
        super.destroy();
        Functions.DestroyDynamicMapIcon(getId());
        mapIcons.remove(this);
    }

}
