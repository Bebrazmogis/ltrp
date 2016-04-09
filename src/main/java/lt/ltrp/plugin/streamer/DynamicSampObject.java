package lt.ltrp.plugin.streamer;


import lt.ltrp.player.object.LtrpPlayer;
import lt.ltrp.util.PawnFunc;
import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.constant.ObjectMaterialSize;
import net.gtaun.shoebill.constant.ObjectMaterialTextAlign;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import java.util.ArrayList;

/**
 * Created by Justas on 2015.06.07.
 */
public class DynamicSampObject {

    private static ArrayList<DynamicSampObject> objects;
    private int id, modelId;
    private Player attachedPlayer;
    private float speed, drawDistance;
    private DynamicSampObject attachedObject;
    private Vehicle attachedVehicle;

    public static DynamicSampObject create(int modelid, Location location, float rx, float ry, float rz) {
        return create(modelid, location, rx, ry, rz, 0.0f);
    }

    // CreateDynamicObject(modelid, Float:x, Float:y, Float:z, Float:rx, Float:ry, Float:rz, worldid = -1, interiorid = -1, playerid = -1, Float:streamdistance = 200.0, Float:drawdistance = 0.0);
    public static DynamicSampObject create(int modelid, Location location, float rx, float ry, float rz, float drawdistance) {
        if(objects == null) {
            objects = new ArrayList<>();
        }
        AmxCallable nativeMethod = PawnFunc.getNativeMethod("CreateDynamicObject");
        int id = 0;
        if(nativeMethod != null) {
            id = (Integer)nativeMethod.call(modelid, location.getX(), location.getY(), location.getZ(), rx, ry, rz, location.getWorldId(), location.getInteriorId(), drawdistance);
            DynamicSampObject object = new DynamicSampObject(id, modelid, null, 0.0f, drawdistance, null, null);
            objects.add(object);
        }
        return null;
    }

    private DynamicSampObject(int id, int modelId, Player attachedPlayer, float speed, float drawDistance, DynamicSampObject attachedObject, Vehicle attachedVehicle) {
        this.id = id;
        this.modelId = modelId;
        this.attachedPlayer = attachedPlayer;
        this.speed = speed;
        this.drawDistance = drawDistance;
        this.attachedObject = attachedObject;
        this.attachedVehicle = attachedVehicle;
    }

    public int getId() {
        return id;
    }

    public int getModelId() {
        return modelId;
    }

    public float getSpeed() {
        return speed;
    }

    public float getDrawDistance() {
        return drawDistance;
    }

    public Player getAttachedPlayer() {
        return attachedPlayer;
    }

    public DynamicSampObject getAttachedObject() {
        return attachedObject;
    }

    public Vehicle getAttachedVehicle() {
        return attachedVehicle;
    }

    // native GetDynamicObjectPos(objectid, &Float:x, &Float:y, &Float:z);
    public Location getLocation() {
        AmxCallable function = PawnFunc.getNativeMethod("GetDynamicObjectPos");
        if(function != null) {
            ReferenceFloat x = new ReferenceFloat(0.0f);
            ReferenceFloat y = new ReferenceFloat(0.0f);
            ReferenceFloat z = new ReferenceFloat(0.0f);
            function.call(getId(), x, y, z);
            return new Location(x.getValue(), y.getValue(), z.getValue());
        } else {
            return null;
        }
    }

    // native SetDynamicObjectPos(objectid, Float:x, Float:y, Float:z);
    public void setLocation(Vector3D location) {
        AmxCallable function = PawnFunc.getNativeMethod("SetDynamicObjectPos");
        if(function != null) {
            function.call(getId(), location.getX(), location.getY(), location.getZ());
        }
    }

    public void setLocation(Location location) {
        setLocation(new Vector3D(location.getX(), location.getY(), location.getZ()));
    }

    // native GetDynamicObjectRot(objectid, &Float:rx, &Float:ry, &Float:rz);
    public Vector3D getRotation() {
        AmxCallable function = PawnFunc.getNativeMethod("GetDynamicObjectRot");
        if(function != null) {
            ReferenceFloat x = new ReferenceFloat(0.0f);
            ReferenceFloat y = new ReferenceFloat(0.0f);
            ReferenceFloat z = new ReferenceFloat(0.0f);
            function.call(getId(), x, y, z);
            return new Location(x.getValue(), y.getValue(), z.getValue());
        } else {
            return null;
        }
    }

    // native SetDynamicObjectRot(objectid, Float:rx, Float:ry, Float:rz);
    public void setRotation(float rx, float ry, float rz) {
        AmxCallable function = PawnFunc.getNativeMethod("SetDynamicObjectRot");
        if(function != null) {
            function.call(getId(), rx, ry, rz);
        }
    }

    public void setRotation(Vector3D vector3D) {
        setRotation(vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }

    // native IsDynamicObjectMoving(objectid);
    public boolean isMoving() {
        AmxCallable function = PawnFunc.getNativeMethod("IsDynamicObjectMoving");
        if(function != null) {
            return (Boolean) function.call(getId());
        } else {
            return false;
        }
    }

    //native MoveDynamicObject(objectid, Float:x, Float:y, Float:z, Float:speed, Float:rx = -1000.0, Float:ry = -1000.0, Float:rz = -1000.0);
    public int move(float x, float y, float z, float speed) {
        return move(x, y, z, speed, getRotation().getX(), getRotation().getY(), getRotation().getZ());
    }

    public int move(float x, float y, float z, float speed, float rx, float ry, float rz) {
        this.speed = speed;
        AmxCallable function = PawnFunc.getNativeMethod("MoveDynamicObject");
        if(function == null) {
            return 0;
        } else {
            return (Integer)function.call(getId(), x, y, z, speed, rx, ry, rz);
        }
    }

    public int move(Vector3D pos, float speed) {
        return move(pos.getX(), pos.getY(), pos.getZ(), speed, getRotation().getX(), getRotation().getY(), getRotation().getZ());
    }

    public int move(Vector3D pos, float speed, Vector3D rotation) {
        return move(pos.getX(), pos.getY(), pos.getZ(), speed, rotation.getX(), rotation.getY(), rotation.getZ());
    }

    public void stop() {
        AmxCallable function = PawnFunc.getNativeMethod("StopDynamicObject");
        if(function != null) {
            function.call(getId());
        }
    }

    // native AttachDynamicObjectToPlayer(objectid, playerid, Float:offsetx, Float:offsety, Float:offsetz, Float:rx, Float:ry, Float:rz);
    public void attach(Player player, float offx, float offy, float offz, float rx, float ry, float rz) {
        AmxCallable function = PawnFunc.getNativeMethod("AttachDynamicObjectToPlayer");
        if(function != null) {
            attachedPlayer = player;
            function.call(getId(), player.getId(), offx, offy, offz, rx, ry, rz);
        }
    }

    public void attach(Player player, Vector3D vector3D, Vector3D rotation) {
        attach(player, vector3D.getX(), vector3D.getY(), vector3D.getZ(), rotation.getX(), rotation.getY(), rotation.getZ());
    }

    // native AttachDynamicObjectToObject(objectid, attachtoid, Float:offsetx, Float:offsety, Float:offsetz, Float:rx, Float:ry, Float:rz, syncrotation = 1);
    public void attach(DynamicSampObject object, float offsetx, float offsety, float offsetz, float rx, float ry, float rz, boolean syncrotation) {
        AmxCallable function = PawnFunc.getNativeMethod("AttachDynamicObjectToObject");
        if(function != null) {
            attachedObject = object;
            function.call(getId(), object.getId(), offsetx, offsety, offsetz, rx, ry, rz, syncrotation);
        }
    }

    public void attach(DynamicSampObject object, Vector3D offset, Vector3D rotation, boolean sync) {
        attach(object, offset.getX(), offset.getY(), offset.getZ(), rotation.getX(), rotation.getY(), rotation.getZ(), sync);
    }

    // native AttachDynamicObjectToVehicle(objectid, vehicleid, Float:offsetx, Float:offsety, Float:offsetz, Float:rx, Float:ry, Float:rz);
    public void attach(Vehicle vehicle, float offsetx, float offsety, float offsetz, float rx, float ry, float rz) {
        AmxCallable function = PawnFunc.getNativeMethod("AttachDynamicObjectToObject");
        if(function != null) {
            attachedVehicle = vehicle;
            function.call(getId(), vehicle.getId(), offsetx, offsety, offsetz, rx, ry, rz);
        }
    }

    public void attach(Vehicle vehicle, Vector3D offsets, Vector3D location) {
        attach(vehicle, offsets.getX(), offsets.getY(), offsets.getZ(), location.getX(), location.getY(), location.getZ());
    }

    // native AttachCameraToDynamicObject(playerid, objectid);
    public void attachCamera(Player player) {
        AmxCallable function = PawnFunc.getNativeMethod("AttachCameraToDynamicObject");
        if(function != null) {
            function.call(player.getId(), getId());
        }
    }

    // native SetDynamicObjectMaterial(objectid, materialindex, modelid, const txdname[], const texturename[], materialcolor = 0);
    public void setMaterial(int materialindex, int modelid, String txdname, String texturename, Color color) {
        AmxCallable function = PawnFunc.getNativeMethod("SetDynamicObjectMaterial");
        if(function != null) {
            function.call(getId(), materialindex, modelid, txdname, texturename, color.getR() | color.getG() | color.getB() | color.getA());
        }
    }

    public void setMaterial(int materialindex, int modelid, String txdname, String texturename) {
        setMaterial(materialindex, modelid, txdname, texturename, new Color(0));
    }

    // native SetDynamicObjectMaterialText(objectid, materialindex, const text[], materialsize = OBJECT_MATERIAL_SIZE_256x128, const fontface[] = "Arial", fontsize = 24, bold = 1, fontcolor = 0xFFFFFFFF, backcolor = 0, textalignment = 0);
    public void setMaterialText(String text, int materialindex, ObjectMaterialSize materialsize, String font, int fontsize, boolean bold, Color fontcolor, Color backcolor, ObjectMaterialTextAlign objectMaterialTextAlign) {
        AmxCallable function = PawnFunc.getNativeMethod("SetDynamicObjectMaterialText");
        if(function != null) {
            function.call(getId(), materialindex, text, materialsize.getValue(), font, fontsize, bold, fontcolor.toArgbValue(), backcolor.toArgbValue(), objectMaterialTextAlign.getValue());
        }
    }

    public void setMaterialText(String text) {
        setMaterialText(text, 0, ObjectMaterialSize.SIZE_256x128, "Arial", 24, true, Color.WHITE, Color.BLACK, ObjectMaterialTextAlign.LEFT);
    }

    // native SetDynamicObjectNoCameraCol(objectid);
    public void setNoCameraCol() {
        AmxCallable function = PawnFunc.getNativeMethod("SetDynamicObjectNoCameraCol");
        if(function != null) {
            function.call(getId());
        }
    }

    // native DestroyDynamicObject(objectid);
    public void destroy() {
        objects.remove(this);
        AmxCallable function = PawnFunc.getNativeMethod("DestroyDynamicObject");
        if(function != null) {
            function.call(getId());
            id = 0;
        }
    }

    // native IsValidDynamicObject(objectid);
    public boolean isDestroyed() {
        AmxCallable function = PawnFunc.getNativeMethod("IsValidDynamicObject");
        if(function != null) {
            return !(Boolean)function.call(getId());
        } else {
            return false;
        }
    }

    // EditDynamicObject(playerid, STREAMER_TAG_OBJECT objectid);
    public void edit(LtrpPlayer player) {
        AmxCallable editDynamicObject = PawnFunc.getNativeMethod("EditDynamicObject");
        if(editDynamicObject != null) {
            editDynamicObject.call(player.getId(), this.getId());
        }
    }

    public static DynamicSampObject findById(int id) {
        for(DynamicSampObject object : objects) {
            if(object.getId() == id) {
                return object;
            }
        }
        return null;
    }


}
