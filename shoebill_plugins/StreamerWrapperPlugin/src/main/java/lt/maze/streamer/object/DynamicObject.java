package lt.maze.streamer.object;

import lt.maze.streamer.Constants;
import lt.maze.streamer.Functions;
import lt.maze.streamer.constant.StreamerType;
import lt.maze.streamer.data.DynamicObjectMaterial;
import lt.maze.streamer.data.DynamicObjectMaterialText;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.amx.types.ReferenceInt;
import net.gtaun.shoebill.amx.types.ReferenceString;
import net.gtaun.shoebill.constant.ObjectMaterialSize;
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
public class DynamicObject extends AbstractStreamerItem implements StreamerItem {

    private static final Collection<DynamicObject> objcets = new ArrayList<>();

    public static Collection<DynamicObject> get() {
        return objcets;
    }

    public static DynamicObject get(int id) {
        Optional<DynamicObject> ob = objcets.stream().filter(o -> o.getId() == id).findFirst();
        return ob.isPresent() ? ob.get() : null;
    }

    public static DynamicObject create(int modelid, float x, float y, float z, float rx, float ry, float rz, int worldid, int interiorid, Player p, float streamdistance, float drawdistance)  {
        int id = Functions.CreateDynamicObject(modelid, x, y, z, rx, ry, rz, worldid, interiorid, p == null ? -1 : p.getId(), streamdistance, drawdistance);
        if(id == Constants.INVALID_STREAMER_ID)
            throw new CreationFailedException("Streamer object could not be created");
        DynamicObject object = new DynamicObject(id, modelid, worldid, interiorid, streamdistance, drawdistance, p);
        objcets.add(object);
        return object;
    }

    public static DynamicObject create(int modelid, float x, float y, float z, float rx, float ry, float rz) {
        return create(modelid, x, y, z, rx, ry, rz, -1, -1, null, Constants.STREAMER_OBJECT_SD, Constants.STREAMER_OBJECT_DD);
    }

    public static DynamicObject create(int modelid, Vector3D position, Vector3D rotation) {
        return create(modelid, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z);
    }

    public static DynamicObject create(int modelid, Location location, Vector3D rotation) {
        return create(modelid, location.x, location.y, location.z, rotation.x, rotation.y, rotation.z, location.worldId, location.interiorId, null, Constants.STREAMER_OBJECT_SD, Constants.STREAMER_OBJECT_DD);
    }

    private int modelId, worldId, interiorId;
    private float streamDistance, drawDistance;
    private Player player;
    private boolean destroyed;
    private DynamicObjectMoveHandler handler;


    public DynamicObject(int id, int modelId, int worldid, int interiorid, float streamDistance, float drawDistance, Player player) {
        super(id, StreamerType.Object);
        this.modelId = modelId;
        this.streamDistance = streamDistance;
        this.drawDistance = drawDistance;
        this.player = player;
        this.worldId = worldid;
        this.interiorId = interiorid;
    }

    public int getModelId() {
        return modelId;
    }

    public DynamicObjectMoveHandler getHandler() {
        return handler;
    }

    public void setPosition(Vector3D vector3D) {
        setPosition(vector3D.x, vector3D.y, vector3D.z);
    }

    public void setPosition(float x, float y, float z) {
        Functions.SetDynamicObjectPos(getId(), x, y, z);
    }

    public Vector3D getPosition() {
        Vector3D pos = new Vector3D();
        ReferenceFloat refX = new ReferenceFloat(pos.x);
        ReferenceFloat refY = new ReferenceFloat(pos.y);
        ReferenceFloat refZ = new ReferenceFloat(pos.z);
        Functions.GetDynamicObjectPos(getId(), refX, refY, refZ);
        pos.x = refX.getValue();
        pos.y = refY.getValue();
        pos.z = refZ.getValue();
        return pos;
    }

    public Location getLocation() {
        return new Location(getPosition(), interiorId, worldId);
    }

    public void setRotation(Vector3D rotation) {
        Functions.SetDynamicObjectRot(getId(), rotation.x, rotation.y, rotation.z);
    }

    public void setRotation(float x, float y, float z) {
        setRotation(new Vector3D(x, y, z));
    }

    public Vector3D getRotation() {
        Vector3D rot = new Vector3D();
        ReferenceFloat refX = new ReferenceFloat(rot.x);
        ReferenceFloat refY = new ReferenceFloat(rot.y);
        ReferenceFloat refZ = new ReferenceFloat(rot.z);
        Functions.GetDynamicObjectRot(getId(), refX, refY, refZ);
        rot.x = refX.getValue();
        rot.y = refY.getValue();
        rot.z = refZ.getValue();
        return rot;
    }

    public void setNoCameraCol() {
        Functions.SetDynamicObjectNoCameraCol(getId());
    }

    public boolean getNoCamerCol() {
        return Functions.GetDynamicObjectNoCameraCol(getId()) == 1;
    }

    public boolean isValid() {
        return Functions.IsValidDynamicObject(getId()) == 1;
    }

    public int move(float x, float y, float z, float speed, float rx, float ry, float rz, DynamicObjectMoveHandler handler) {
        this.handler = handler;
        return Functions.MoveDynamicObject(getId(), x, y, z, speed, rx, ry, rz);
    }

    public int move(float x, float y, float z, float speed, DynamicObjectMoveHandler handler) {
        return move(x, y, z, speed, -1000f, -1000f, -1000f, handler);
    }

    public int move(Vector3D position, float speed, DynamicObjectMoveHandler handler) {
        return move(position.x, position.y, position.z, speed, handler);
    }

    public int move(Vector3D position, Vector3D rotation, float speed, DynamicObjectMoveHandler handler) {
        return move(position.x, position.y, position.z, speed, rotation.x, rotation.y, rotation.z, handler);
    }

    public void stop() {
        Functions.StopDynamicObject(getId());
    }

    public boolean isMoving() {
        return Functions.IsDynamicObjectMoving(getId()) == 1;
    }

    public void attachCamera(Player p) {
        Functions.AttachCameraToDynamicObject(p.getId(), getId());
    }

    public void attachToObject(DynamicObject object, Vector3D offsets, Vector3D rotation, boolean sync) {
        Functions.AttachDynamicObjectToObject(object.getId(), getId(), offsets.x, offsets.y, offsets.z, rotation.x, rotation.y, rotation.z, sync ? 1 : 0);
    }

    public void attachToPlayer(Player player, Vector3D offsets, Vector3D rotation) {
        Functions.AttachDynamicObjectToPlayer(player.getId(), getId(), offsets.x, offsets.y, offsets.z, rotation.x, rotation.y, rotation.z);
    }

    public void attachToVehicle(Vehicle vehicle, Vector3D offsets, Vector3D rotation) {
        Functions.AttachDynamicObjectToVehicle(vehicle.getId(), getId(), offsets.x, offsets.y, offsets.z, rotation.x, rotation.y, rotation.z);
    }

    public void edit(Player p) {
        Functions.EditDynamicObject(p.getId(), getId());
    }

    public boolean isMaterialUsed(int index) {
        return Functions.IsDynamicObjectMaterialUsed(getId(), index) == 1;
    }

    //GetDynamicObjectMaterial(STREAMER_TAG_OBJECT objectid, materialindex, &modelid, txdname[], texturename[], &materialcolor, maxtxdname = sizeof txdname, maxtexturename = sizeof texturename);
    public DynamicObjectMaterial getMaterial(int index) {
        int model = 0;
        String txd = null, texture = null;
        int color = 0;
        ReferenceInt modelRef = new ReferenceInt(model);
        ReferenceString txdRef = new ReferenceString(txd, 128);
        ReferenceString textureRef = new ReferenceString(texture, 128);
        ReferenceInt colorRef=  new ReferenceInt(color);
        Functions.GetDynamicObjectMaterial(getId(), index, modelRef, txdRef, textureRef, colorRef, 128, 128);
        return new DynamicObjectMaterial(modelRef.getValue(), txdRef.getValue(), textureRef.getValue(), new Color(colorRef.getValue()));
    }

    // SetDynamicObjectMaterial(STREAMER_TAG_OBJECT objectid, materialindex, modelid, const txdname[], const texturename[], materialcolor = 0);
    public void setMaterial(int index, DynamicObjectMaterial material) {
        Functions.SetDynamicObjectMaterial(getId(), index, material.getModelId(), material.getTxdName(), material.getTextureName(), material.getColor().toArgbValue());
    }

    // native IsDynamicObjectMaterialTextUsed(STREAMER_TAG_OBJECT objectid, materialindex);
    public boolean isMaterialTextUsed(int index) {
        return Functions.IsDynamicObjectMaterialTextUsed(getId(), index) == 1;
    }

    //native GetDynamicObjectMaterialText(STREAMER_TAG_OBJECT objectid, materialindex, text[], &materialsize, fontface[], &fontsize, &bold, &fontcolor, &backcolor, &textalignment, maxtext = sizeof text, maxfontface = sizeof fontface);
    public DynamicObjectMaterialText getMaterialText(int index) {
        ReferenceInt materialSize = new ReferenceInt(0),
            fontSize = new ReferenceInt(0),
            bold = new ReferenceInt(0),
            fontColor = new ReferenceInt(0),
            backColor = new ReferenceInt(0),
            alignment = new ReferenceInt(0);
        ReferenceString text = new ReferenceString("", 0),
                fontFace = new ReferenceString("", 0);
        Functions.GetDynamicObjectMaterialText(getId(), index, text, materialSize, fontFace, fontSize, bold, fontColor, backColor, alignment, text.getLength(), fontFace.getLength());
        return new DynamicObjectMaterialText(
                text.getValue(),
                ObjectMaterialSize.get(materialSize.getValue()),
                fontFace.getValue(),
                fontSize.getValue(),
                alignment.getValue(),
                bold.getValue() == 1,
                new Color(fontColor.getValue()),
                new Color(backColor.getValue())
        );
    }

    // native SetDynamicObjectMaterialText(STREAMER_TAG_OBJECT objectid, materialindex, const text[], materialsize = OBJECT_MATERIAL_SIZE_256x128, const fontface[] = "Arial", fontsize = 24, bold = 1, fontcolor = 0xFFFFFFFF, backcolor = 0, textalignment = 0);
    public void setMaterialText(int index, DynamicObjectMaterialText text) {
        Functions.SetDynamicObjectMaterialText(getId(), index, text.getText(), text.getSize().getValue(), text.getFontFace(), text.getFontSize(), text.isBold() ? 1 : 0, text.getFontColor().toArgbValue(), text.getBackColor().toArgbValue(), text.getAlignment());
    }

    @Override
    public void destroy() {
        super.destroy();
        destroyed = true;
        objcets.remove(this);
        Functions.DestroyDynamicObject(getId());
    }


    @FunctionalInterface
    public interface DynamicObjectMoveHandler {
        void onDynamicObjectMove(DynamicObject object);
    }
}
