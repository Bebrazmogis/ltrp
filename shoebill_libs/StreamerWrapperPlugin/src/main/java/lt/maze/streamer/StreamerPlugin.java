package lt.maze.streamer;

import lt.maze.streamer.constant.StreamerType;
import lt.maze.streamer.object.*;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.amx.AmxInstanceManager;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.entities.Destroyable;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.resource.Plugin;
import org.slf4j.Logger;


/**
 * @author Bebras
 *         2016.02.16.
 */
public class StreamerPlugin extends Plugin {

    private static StreamerPlugin instance;
    private static Logger logger;

    public static StreamerPlugin getInstance() {
        return instance;
    }


    @Override
    public void onEnable() throws Throwable {
        instance = this;
        logger = getLogger();

        Callbacks.registerHandlers(AmxInstanceManager.get());
        Functions.registerFunctions(AmxInstance.getDefault());
        logger.info("Streamer wrapper initialized");
    }

    @Override
    public void onDisable() throws Throwable {
        Callbacks.unregisterHandlers(AmxInstanceManager.get());
        DynamicArea.get().forEach(DynamicArea::destroy);
        DynamicCheckpoint.get().forEach(DynamicCheckpoint::destroy);
        DynamicLabel.get().forEach(DynamicLabel::destroy);
        DynamicMapIcon.get().forEach(DynamicMapIcon::destroy);
        DynamicObject.get().forEach(DynamicObject::destroy);
        DynamicPickup.get().forEach(DynamicPickup::destroy);
        DynamicRaceCheckpoint.get().forEach(Destroyable::destroy);
        logger.info("Streamer wrapper uninitialized");
    }

    public void setTickRate(int ticks) {
        Functions.Streamer_SetTickRate(ticks);
    }

    public int getTickRate() {
        return Functions.Streamer_GetTickRate();
    }

    public int getMaxItems(StreamerType type) {
        return Functions.Streamer_GetMaxItems(type.getValue());
    }

    public void setMaxItems(StreamerType type, int items) {
        Functions.Streamer_SetMaxItems(type.getValue(), items);
    }

    public int getVisibleItems(StreamerType type, Player p) {
        return Functions.Streamer_GetVisibleItems(type.getValue(), p.getId());
    }

    public void setVisibleItems(StreamerType type, int items,Player p) {
        Functions.Streamer_SetVisibleItems(type.getValue(), items, p.getId());
    }

    public float getRadiusMultiplier(StreamerType type, Player p) {
        ReferenceFloat ref = new ReferenceFloat(0f);
        Functions.Streamer_GetRadiusMultiplier(type.getValue(), ref, p.getId());
        return ref.getValue();
    }

    public void setRadiusMultiplier(StreamerType type, float mul, Player p) {
        Functions.Streamer_SetRadiusMultiplier(type.getValue(), mul, p.getId());
    }

    public void setCellDistance(float distance) {
        Functions.Streamer_SetCellDistance(distance);
    }

    public float getCellDistance() {
        ReferenceFloat ref = new ReferenceFloat(0f);
        Functions.Streamer_GetCellDistance(ref);
        return ref.getValue();
    }

    public void setCellSize(float size) {
        Functions.Streamer_SetCellSize(size);
    }

    public float getCellSize() {
        ReferenceFloat ref = new ReferenceFloat(0f);
        Functions.Streamer_GetCellSize(ref);
        return ref.getValue();
    }

    public void toggleErrorCallback(boolean toggle) {
        Functions.Streamer_ToggleErrorCallback(toggle ? 1 : 0);
    }

    public boolean isErrorCallback() {
        return Functions.Streamer_IsToggleErrorCallback() == 1;
    }

    public void processActiveItems() {
        Functions.Streamer_ProcessActiveItems();
    }

    public void toggleIdleUpdate(Player p, boolean toggle) {
        Functions.Streamer_ToggleIdleUpdate(p.getId(), toggle ? 1 : 0);
    }

    public boolean isIdleUpdateToggled(Player p) {
        return Functions.Streamer_IsToggleIdleUpdate(p.getId()) == 1;
    }

    public void toggleCameraUpdate(Player p, boolean toggle) {
        Functions.Streamer_ToggleCameraUpdate(p.getId(), toggle ? 1 : 0);
    }

    public boolean isCameraUpdateToggled(Player p) {
        return Functions.Streamer_IsToggleCameraUpdate(p.getId()) == 1;
    }

    public void toggleItemUpdate(Player p, StreamerType type, boolean toggle) {
        Functions.Streamer_ToggleItemUpdate(p.getId(), type.getValue(), toggle ? 1 : 0);
    }

    public boolean isItemUpdateToggled(Player p, StreamerType type) {
        return Functions.Streamer_IsToggleItemUpdate(p.getId(), type.getValue()) == 1;
    }

    public void update(Player p, StreamerType type) {
        Functions.Streamer_Update(p.getId(), type.getValue());
    }

    public void update(Player p, Location location, StreamerType type) {
        Functions.Streamer_UpdateEx(p.getId(), location.x, location.y, location.z, location.worldId, location.interiorId, type.getValue());
    }

    public float getFloatData(StreamerType type, StreamerItem item, int data) {
        ReferenceFloat ref = new ReferenceFloat(0f);
        Functions.Streamer_GetFloatData(type.getValue(), item.getId(), data, ref);
        return ref.getValue();
    }

    public void setFloatData(StreamerType type, StreamerItem item, int data, float value) {
        Functions.Streamer_SetFloatData(type.getValue(), item.getId(), data, value);
    }

    public int getIntData(StreamerType type, StreamerItem item, int data) {
        return Functions.Streamer_GetIntData(type.getValue(), item.getId(), data);
    }

    public void setIntData(StreamerType type, StreamerItem item, int data, int value) {
        Functions.Streamer_SetIntData(type.getValue(), item.getId(), data, value);
    }

    // TODO Implement Streamer_GetArrayData
    // TODO Implement Streamer_SetArrayData
    // TODO Implement Streamer_IsInArrayData
    // TODO Implement Streamer_AppendArrayData
    // TODO Implement Streamer_RemoveArrayData

    public int getUpperBount(StreamerType type) {
        return Functions.Streamer_GetUpperBound(type.getValue());
    }

    public float getDistanceToItem(Vector3D position, StreamerType type, StreamerItem item, int dimensions) {
        ReferenceFloat ref = new ReferenceFloat(0f);
        Functions.Streamer_GetDistanceToItem(position.x, position.y, position.z, type.getValue(), item.getId(), ref, dimensions);
        return ref.getValue();
    }

    public void toggleStaticItem(StreamerType type, StreamerItem item, boolean toggle) {
        Functions.Streamer_ToggleStaticItem(type.getValue(), item.getId(), toggle ? 1 : 0);
    }

    public boolean isStaticItemToggled(StreamerType type, StreamerItem item) {
        return Functions.Streamer_IsToggleStaticItem(type.getValue(), item.getId()) == 1;
    }

    public int getInternalId(Player p, StreamerType type, StreamerItem item) {
        return Functions.Streamer_GetItemInternalID(p.getId(), type.getValue(), item.getId());
    }

    public StreamerItem getItem(Player p, StreamerType type, int inernalid) {
        int id = Functions.Streamer_GetItemStreamerID(p.getId(), type.getValue(), inernalid);
        if(id != Constants.INVALID_STREAMER_ID) {
            switch(type) {
                case Object:
                    return DynamicObject.get(id);
                case Pickup:
                    return DynamicPickup.get(id);
                case Checkpoint:
                    return DynamicCheckpoint.get(id);
                case RaceCheckpoint:
                    return DynamicRaceCheckpoint.get(id);
                case MapIcon:
                    return DynamicMapIcon.get(id);
                case Label:
                    return DynamicLabel.get(id);
                case Area:
                    return DynamicArea.get(id);
            }
        }
        return null;
    }


    public void destroyAllVisibleItems(Player p, StreamerType type, boolean serverwide) {
        Functions.Streamer_DestroyAllVisibleItems(p.getId(), type.getValue(), serverwide ? 1 : 0);
    }

    public void destroyAllVisibleItems(Player p, StreamerType type) {
        destroyAllVisibleItems(p, type, true);
    }

    public int getVisibleItems(Player p, StreamerType type, boolean serverwide) {
        return Functions.Streamer_CountVisibleItems(p.getId(), type.getValue(), serverwide ? 1 : 0);
    }

    public int getVisibleItems(Player p, StreamerType type) {
        return getVisibleItems(p, type, true);
    }

    public void destroyAllItems(StreamerType type, boolean serverwide) {
        Functions.Streamer_DestroyAllItems(type.getValue(), serverwide ? 1 : 0);
    }

    public void destroyAllItems(StreamerType type) {
        destroyAllItems(type, true);
    }

    public int countItems(StreamerType type, boolean serverwide) {
        return Functions.Streamer_CountItems(type.getValue(), serverwide ? 1 : 0);
    }

    public int countItems(StreamerType type) {
        return countItems(type, true);
    }



}
