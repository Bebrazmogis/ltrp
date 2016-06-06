package lt.maze.streamer;

import net.gtaun.shoebill.amx.AmxCallable;
import net.gtaun.shoebill.amx.AmxInstance;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.amx.types.ReferenceInt;
import net.gtaun.shoebill.amx.types.ReferenceString;
import net.gtaun.shoebill.amx.types.ReturnType;

import java.util.HashMap;

public class Functions {

	private static HashMap<String, AmxCallable> functions = new HashMap<>();

	public static void registerFunctions(AmxInstance instance) {
		functions.put("Streamer_GetTickRate", instance.getNative("Streamer_GetTickRate", ReturnType.INTEGER));
		functions.put("Streamer_SetTickRate", instance.getNative("Streamer_SetTickRate", ReturnType.INTEGER));
		functions.put("Streamer_GetMaxItems", instance.getNative("Streamer_GetMaxItems", ReturnType.INTEGER));
		functions.put("Streamer_SetMaxItems", instance.getNative("Streamer_SetMaxItems", ReturnType.INTEGER));
		functions.put("Streamer_GetVisibleItems", instance.getNative("Streamer_GetVisibleItems", ReturnType.INTEGER));
		functions.put("Streamer_SetVisibleItems", instance.getNative("Streamer_SetVisibleItems", ReturnType.INTEGER));
		functions.put("Streamer_GetRadiusMultiplier", instance.getNative("Streamer_GetRadiusMultiplier", ReturnType.INTEGER));
		functions.put("Streamer_SetRadiusMultiplier", instance.getNative("Streamer_SetRadiusMultiplier", ReturnType.INTEGER));
		functions.put("Streamer_GetCellDistance", instance.getNative("Streamer_GetCellDistance", ReturnType.INTEGER));
		functions.put("Streamer_SetCellDistance", instance.getNative("Streamer_SetCellDistance", ReturnType.INTEGER));
		functions.put("Streamer_GetCellSize", instance.getNative("Streamer_GetCellSize", ReturnType.INTEGER));
		functions.put("Streamer_SetCellSize", instance.getNative("Streamer_SetCellSize", ReturnType.INTEGER));
		functions.put("Streamer_ToggleErrorCallback", instance.getNative("Streamer_ToggleErrorCallback", ReturnType.INTEGER));
		functions.put("Streamer_IsToggleErrorCallback", instance.getNative("Streamer_IsToggleErrorCallback", ReturnType.INTEGER));
		functions.put("Streamer_ProcessActiveItems", instance.getNative("Streamer_ProcessActiveItems", ReturnType.INTEGER));
		functions.put("Streamer_ToggleIdleUpdate", instance.getNative("Streamer_ToggleIdleUpdate", ReturnType.INTEGER));
		functions.put("Streamer_IsToggleIdleUpdate", instance.getNative("Streamer_IsToggleIdleUpdate", ReturnType.INTEGER));
		functions.put("Streamer_ToggleCameraUpdate", instance.getNative("Streamer_ToggleCameraUpdate", ReturnType.INTEGER));
		functions.put("Streamer_IsToggleCameraUpdate", instance.getNative("Streamer_IsToggleCameraUpdate", ReturnType.INTEGER));
		functions.put("Streamer_ToggleItemUpdate", instance.getNative("Streamer_ToggleItemUpdate", ReturnType.INTEGER));
		functions.put("Streamer_IsToggleItemUpdate", instance.getNative("Streamer_IsToggleItemUpdate", ReturnType.INTEGER));
		functions.put("Streamer_Update", instance.getNative("Streamer_Update", ReturnType.INTEGER));
		functions.put("Streamer_UpdateEx", instance.getNative("Streamer_UpdateEx", ReturnType.INTEGER));
		functions.put("Streamer_GetFloatData", instance.getNative("Streamer_GetFloatData", ReturnType.INTEGER));
		functions.put("Streamer_SetFloatData", instance.getNative("Streamer_SetFloatData", ReturnType.INTEGER));
		functions.put("Streamer_GetIntData", instance.getNative("Streamer_GetIntData", ReturnType.INTEGER));
		functions.put("Streamer_SetIntData", instance.getNative("Streamer_SetIntData", ReturnType.INTEGER));
		functions.put("Streamer_GetArrayData", instance.getNative("Streamer_GetArrayData", ReturnType.INTEGER));
		functions.put("Streamer_SetArrayData", instance.getNative("Streamer_SetArrayData", ReturnType.INTEGER));
		functions.put("Streamer_IsInArrayData", instance.getNative("Streamer_IsInArrayData", ReturnType.INTEGER));
		functions.put("Streamer_AppendArrayData", instance.getNative("Streamer_AppendArrayData", ReturnType.INTEGER));
		functions.put("Streamer_RemoveArrayData", instance.getNative("Streamer_RemoveArrayData", ReturnType.INTEGER));
		functions.put("Streamer_GetUpperBound", instance.getNative("Streamer_GetUpperBound", ReturnType.INTEGER));
		functions.put("Streamer_GetDistanceToItem", instance.getNative("Streamer_GetDistanceToItem", ReturnType.INTEGER));
		functions.put("Streamer_ToggleStaticItem", instance.getNative("Streamer_ToggleStaticItem", ReturnType.INTEGER));
		functions.put("Streamer_IsToggleStaticItem", instance.getNative("Streamer_IsToggleStaticItem", ReturnType.INTEGER));
		functions.put("Streamer_GetItemInternalID", instance.getNative("Streamer_GetItemInternalID", ReturnType.INTEGER));
		functions.put("Streamer_GetItemStreamerID", instance.getNative("Streamer_GetItemStreamerID", ReturnType.INTEGER));
		functions.put("Streamer_IsItemVisible", instance.getNative("Streamer_IsItemVisible", ReturnType.INTEGER));
		functions.put("Streamer_DestroyAllVisibleItems", instance.getNative("Streamer_DestroyAllVisibleItems", ReturnType.INTEGER));
		functions.put("Streamer_CountVisibleItems", instance.getNative("Streamer_CountVisibleItems", ReturnType.INTEGER));
		functions.put("Streamer_DestroyAllItems", instance.getNative("Streamer_DestroyAllItems", ReturnType.INTEGER));
		functions.put("Streamer_CountItems", instance.getNative("Streamer_CountItems", ReturnType.INTEGER));
		functions.put("CreateDynamicObject", instance.getNative("CreateDynamicObject", ReturnType.INTEGER));
		functions.put("DestroyDynamicObject", instance.getNative("DestroyDynamicObject", ReturnType.INTEGER));
		functions.put("IsValidDynamicObject", instance.getNative("IsValidDynamicObject", ReturnType.INTEGER));
		functions.put("SetDynamicObjectPos", instance.getNative("SetDynamicObjectPos", ReturnType.INTEGER));
		functions.put("GetDynamicObjectPos", instance.getNative("GetDynamicObjectPos", ReturnType.INTEGER));
		functions.put("SetDynamicObjectRot", instance.getNative("SetDynamicObjectRot", ReturnType.INTEGER));
		functions.put("GetDynamicObjectRot", instance.getNative("GetDynamicObjectRot", ReturnType.INTEGER));
		functions.put("SetDynamicObjectNoCameraCol", instance.getNative("SetDynamicObjectNoCameraCol", ReturnType.INTEGER));
		functions.put("GetDynamicObjectNoCameraCol", instance.getNative("GetDynamicObjectNoCameraCol", ReturnType.INTEGER));
		functions.put("MoveDynamicObject", instance.getNative("MoveDynamicObject", ReturnType.INTEGER));
		functions.put("StopDynamicObject", instance.getNative("StopDynamicObject", ReturnType.INTEGER));
		functions.put("IsDynamicObjectMoving", instance.getNative("IsDynamicObjectMoving", ReturnType.INTEGER));
		functions.put("AttachCameraToDynamicObject", instance.getNative("AttachCameraToDynamicObject", ReturnType.INTEGER));
		functions.put("AttachDynamicObjectToObject", instance.getNative("AttachDynamicObjectToObject", ReturnType.INTEGER));
		functions.put("AttachDynamicObjectToPlayer", instance.getNative("AttachDynamicObjectToPlayer", ReturnType.INTEGER));
		functions.put("AttachDynamicObjectToVehicle", instance.getNative("AttachDynamicObjectToVehicle", ReturnType.INTEGER));
		functions.put("EditDynamicObject", instance.getNative("EditDynamicObject", ReturnType.INTEGER));
		functions.put("IsDynamicObjectMaterialUsed", instance.getNative("IsDynamicObjectMaterialUsed", ReturnType.INTEGER));
		functions.put("GetDynamicObjectMaterial", instance.getNative("GetDynamicObjectMaterial", ReturnType.INTEGER));
		functions.put("SetDynamicObjectMaterial", instance.getNative("SetDynamicObjectMaterial", ReturnType.INTEGER));
		functions.put("IsDynamicObjectMaterialTextUsed", instance.getNative("IsDynamicObjectMaterialTextUsed", ReturnType.INTEGER));
		functions.put("GetDynamicObjectMaterialText", instance.getNative("GetDynamicObjectMaterialText", ReturnType.INTEGER));
		functions.put("SetDynamicObjectMaterialText", instance.getNative("SetDynamicObjectMaterialText", ReturnType.INTEGER));
		functions.put("CreateDynamicPickup", instance.getNative("CreateDynamicPickup", ReturnType.INTEGER));
		functions.put("DestroyDynamicPickup", instance.getNative("DestroyDynamicPickup", ReturnType.INTEGER));
		functions.put("IsValidDynamicPickup", instance.getNative("IsValidDynamicPickup", ReturnType.INTEGER));
		functions.put("CreateDynamicCP", instance.getNative("CreateDynamicCP", ReturnType.INTEGER));
		functions.put("DestroyDynamicCP", instance.getNative("DestroyDynamicCP", ReturnType.INTEGER));
		functions.put("IsValidDynamicCP", instance.getNative("IsValidDynamicCP", ReturnType.INTEGER));
		functions.put("TogglePlayerDynamicCP", instance.getNative("TogglePlayerDynamicCP", ReturnType.INTEGER));
		functions.put("TogglePlayerAllDynamicCPs", instance.getNative("TogglePlayerAllDynamicCPs", ReturnType.INTEGER));
		functions.put("IsPlayerInDynamicCP", instance.getNative("IsPlayerInDynamicCP", ReturnType.INTEGER));
		functions.put("GetPlayerVisibleDynamicCP", instance.getNative("GetPlayerVisibleDynamicCP", ReturnType.INTEGER));
		functions.put("CreateDynamicRaceCP", instance.getNative("CreateDynamicRaceCP", ReturnType.INTEGER));
		functions.put("DestroyDynamicRaceCP", instance.getNative("DestroyDynamicRaceCP", ReturnType.INTEGER));
		functions.put("IsValidDynamicRaceCP", instance.getNative("IsValidDynamicRaceCP", ReturnType.INTEGER));
		functions.put("TogglePlayerDynamicRaceCP", instance.getNative("TogglePlayerDynamicRaceCP", ReturnType.INTEGER));
		functions.put("TogglePlayerAllDynamicRaceCPs", instance.getNative("TogglePlayerAllDynamicRaceCPs", ReturnType.INTEGER));
		functions.put("IsPlayerInDynamicRaceCP", instance.getNative("IsPlayerInDynamicRaceCP", ReturnType.INTEGER));
		functions.put("GetPlayerVisibleDynamicRaceCP", instance.getNative("GetPlayerVisibleDynamicRaceCP", ReturnType.INTEGER));
		functions.put("CreateDynamicMapIcon", instance.getNative("CreateDynamicMapIcon", ReturnType.INTEGER));
		functions.put("DestroyDynamicMapIcon", instance.getNative("DestroyDynamicMapIcon", ReturnType.INTEGER));
		functions.put("IsValidDynamicMapIcon", instance.getNative("IsValidDynamicMapIcon", ReturnType.INTEGER));
		functions.put("CreateDynamic3DTextLabel", instance.getNative("CreateDynamic3DTextLabel", ReturnType.INTEGER));
		functions.put("DestroyDynamic3DTextLabel", instance.getNative("DestroyDynamic3DTextLabel", ReturnType.INTEGER));
		functions.put("IsValidDynamic3DTextLabel", instance.getNative("IsValidDynamic3DTextLabel", ReturnType.INTEGER));
		functions.put("GetDynamic3DTextLabelText", instance.getNative("GetDynamic3DTextLabelText", ReturnType.INTEGER));
		functions.put("UpdateDynamic3DTextLabelText", instance.getNative("UpdateDynamic3DTextLabelText", ReturnType.INTEGER));
		functions.put("CreateDynamicCircle", instance.getNative("CreateDynamicCircle", ReturnType.INTEGER));
		functions.put("CreateDynamicCylinder", instance.getNative("CreateDynamicCylinder", ReturnType.INTEGER));
		functions.put("CreateDynamicSphere", instance.getNative("CreateDynamicSphere", ReturnType.INTEGER));
		functions.put("CreateDynamicRectangle", instance.getNative("CreateDynamicRectangle", ReturnType.INTEGER));
		functions.put("CreateDynamicCuboid", instance.getNative("CreateDynamicCuboid", ReturnType.INTEGER));
		functions.put("CreateDynamicCube", instance.getNative("CreateDynamicCube", ReturnType.INTEGER));
		functions.put("DestroyDynamicArea", instance.getNative("DestroyDynamicArea", ReturnType.INTEGER));
		functions.put("IsValidDynamicArea", instance.getNative("IsValidDynamicArea", ReturnType.INTEGER));
		functions.put("GetDynamicPolygonNumberPoints", instance.getNative("GetDynamicPolygonNumberPoints", ReturnType.INTEGER));
		functions.put("TogglePlayerDynamicArea", instance.getNative("TogglePlayerDynamicArea", ReturnType.INTEGER));
		functions.put("TogglePlayerAllDynamicAreas", instance.getNative("TogglePlayerAllDynamicAreas", ReturnType.INTEGER));
		functions.put("IsPlayerInDynamicArea", instance.getNative("IsPlayerInDynamicArea", ReturnType.INTEGER));
		functions.put("IsPlayerInAnyDynamicArea", instance.getNative("IsPlayerInAnyDynamicArea", ReturnType.INTEGER));
		functions.put("IsAnyPlayerInDynamicArea", instance.getNative("IsAnyPlayerInDynamicArea", ReturnType.INTEGER));
		functions.put("IsAnyPlayerInAnyDynamicArea", instance.getNative("IsAnyPlayerInAnyDynamicArea", ReturnType.INTEGER));
		functions.put("GetPlayerNumberDynamicAreas", instance.getNative("GetPlayerNumberDynamicAreas", ReturnType.INTEGER));
		functions.put("IsPointInDynamicArea", instance.getNative("IsPointInDynamicArea", ReturnType.INTEGER));
		functions.put("IsPointInAnyDynamicArea", instance.getNative("IsPointInAnyDynamicArea", ReturnType.INTEGER));
		functions.put("GetNumberDynamicAreasForPoint", instance.getNative("GetNumberDynamicAreasForPoint", ReturnType.INTEGER));
		functions.put("AttachDynamicAreaToObject", instance.getNative("AttachDynamicAreaToObject", ReturnType.INTEGER));
		functions.put("AttachDynamicAreaToPlayer", instance.getNative("AttachDynamicAreaToPlayer", ReturnType.INTEGER));
		functions.put("AttachDynamicAreaToVehicle", instance.getNative("AttachDynamicAreaToVehicle", ReturnType.INTEGER));
        functions.put("CreateDynamic3DTextLabelEx", instance.getNative("CreateDynamic3DTextLabelEx", ReturnType.INTEGER));
        functions.put("CreateDynamicPolygon", instance.getNative("CreateDynamicPolygon", ReturnType.INTEGER));
	}


    public static int CreateDynamicPolygon(Float[] points, float minz, float maxz, int maxpoints, int worldid, int interiorid, int playerid)
    {
        return (int)functions.get("CreateDynamicPolygon").call((Object)points, minz, maxz, maxpoints, worldid, interiorid, playerid);
    }

	public static int Streamer_GetTickRate() {
		return (int) functions.get("Streamer_GetTickRate").call();
	}

	public static int Streamer_SetTickRate(int rate) {
		return (int) functions.get("Streamer_SetTickRate").call(rate);
	}

	public static int Streamer_GetMaxItems(int type) {
		return (int) functions.get("Streamer_GetMaxItems").call(type);
	}

	public static int Streamer_SetMaxItems(int type, int items) {
		return (int) functions.get("Streamer_SetMaxItems").call(type, items);
	}

	public static int Streamer_GetVisibleItems(int type, int playerid) {
		return (int) functions.get("Streamer_GetVisibleItems").call(type, playerid);
	}

	public static int Streamer_SetVisibleItems(int type, int items, int playerid) {
		return (int) functions.get("Streamer_SetVisibleItems").call(type, items, playerid);
	}

	public static int Streamer_GetRadiusMultiplier(int type, ReferenceFloat multiplier, int playerid) {
		return (int) functions.get("Streamer_GetRadiusMultiplier").call(type, multiplier, playerid);
	}

	public static int Streamer_SetRadiusMultiplier(int type, float multiplier, int playerid) {
		return (int) functions.get("Streamer_SetRadiusMultiplier").call(type, multiplier, playerid);
	}

	public static int Streamer_GetCellDistance(ReferenceFloat distance) {
		return (int) functions.get("Streamer_GetCellDistance").call(distance);
	}

	public static int Streamer_SetCellDistance(float distance) {
		return (int) functions.get("Streamer_SetCellDistance").call(distance);
	}

	public static int Streamer_GetCellSize(ReferenceFloat size) {
		return (int) functions.get("Streamer_GetCellSize").call(size);
	}

	public static int Streamer_SetCellSize(float size) {
		return (int) functions.get("Streamer_SetCellSize").call(size);
	}

	public static int Streamer_ToggleErrorCallback(int toggle) {
		return (int) functions.get("Streamer_ToggleErrorCallback").call(toggle);
	}

	public static int Streamer_IsToggleErrorCallback() {
		return (int) functions.get("Streamer_IsToggleErrorCallback").call();
	}

	public static int Streamer_ProcessActiveItems() {
		return (int) functions.get("Streamer_ProcessActiveItems").call();
	}

	public static int Streamer_ToggleIdleUpdate(int playerid, int toggle) {
		return (int) functions.get("Streamer_ToggleIdleUpdate").call(playerid, toggle);
	}

	public static int Streamer_IsToggleIdleUpdate(int playerid) {
		return (int) functions.get("Streamer_IsToggleIdleUpdate").call(playerid);
	}

	public static int Streamer_ToggleCameraUpdate(int playerid, int toggle) {
		return (int) functions.get("Streamer_ToggleCameraUpdate").call(playerid, toggle);
	}

	public static int Streamer_IsToggleCameraUpdate(int playerid) {
		return (int) functions.get("Streamer_IsToggleCameraUpdate").call(playerid);
	}

	public static int Streamer_ToggleItemUpdate(int playerid, int type, int toggle) {
		return (int) functions.get("Streamer_ToggleItemUpdate").call(playerid, type, toggle);
	}

	public static int Streamer_IsToggleItemUpdate(int playerid, int type) {
		return (int) functions.get("Streamer_IsToggleItemUpdate").call(playerid, type);
	}

	public static int Streamer_Update(int playerid, int type) {
		return (int) functions.get("Streamer_Update").call(playerid, type);
	}

	public static int Streamer_UpdateEx(int playerid, float x, float y, float z, int worldid, int interiorid, int type) {
		return (int) functions.get("Streamer_UpdateEx").call(playerid, x, y, z, worldid, interiorid, type);
	}

	public static int Streamer_GetFloatData(int type, int id, int data, ReferenceFloat result) {
		return (int) functions.get("Streamer_GetFloatData").call(type, id, data, result);
	}

	public static int Streamer_SetFloatData(int type, int id, int data, float value) {
		return (int) functions.get("Streamer_SetFloatData").call(type, id, data, value);
	}

	public static int Streamer_GetIntData(int type, int id, int data) {
		return (int) functions.get("Streamer_GetIntData").call(type, id, data);
	}

	public static int Streamer_SetIntData(int type, int id, int data, int value) {
		return (int) functions.get("Streamer_SetIntData").call(type, id, data, value);
	}

	public static int Streamer_GetArrayData(int type, int id, int data, String dest, int maxdest) {
		return (int) functions.get("Streamer_GetArrayData").call(type, id, data, dest, maxdest);
	}

	public static int Streamer_SetArrayData(int type, int id, int data, String src, int maxsrc) {
		return (int) functions.get("Streamer_SetArrayData").call(type, id, data, src, maxsrc);
	}

	public static int Streamer_IsInArrayData(int type, int id, int data, int value) {
		return (int) functions.get("Streamer_IsInArrayData").call(type, id, data, value);
	}

	public static int Streamer_AppendArrayData(int type, int id, int data, int value) {
		return (int) functions.get("Streamer_AppendArrayData").call(type, id, data, value);
	}

	public static int Streamer_RemoveArrayData(int type, int id, int data, int value) {
		return (int) functions.get("Streamer_RemoveArrayData").call(type, id, data, value);
	}

	public static int Streamer_GetUpperBound(int type) {
		return (int) functions.get("Streamer_GetUpperBound").call(type);
	}

	public static int Streamer_GetDistanceToItem(float x, float y, float z, int type, int id, ReferenceFloat distance, int dimensions) {
		return (int) functions.get("Streamer_GetDistanceToItem").call(x, y, z, type, id, distance, dimensions);
	}

	public static int Streamer_ToggleStaticItem(int type, int id, int toggle) {
		return (int) functions.get("Streamer_ToggleStaticItem").call(type, id, toggle);
	}

	public static int Streamer_IsToggleStaticItem(int type, int id) {
		return (int) functions.get("Streamer_IsToggleStaticItem").call(type, id);
	}

	public static int Streamer_GetItemInternalID(int playerid, int type, int streamerid) {
		return (int) functions.get("Streamer_GetItemInternalID").call(playerid, type, streamerid);
	}

	public static int Streamer_GetItemStreamerID(int playerid, int type, int internalid) {
		return (int) functions.get("Streamer_GetItemStreamerID").call(playerid, type, internalid);
	}

	public static int Streamer_IsItemVisible(int playerid, int type, int id) {
		return (int) functions.get("Streamer_IsItemVisible").call(playerid, type, id);
	}

	public static int Streamer_DestroyAllVisibleItems(int playerid, int type, int serverwide) {
		return (int) functions.get("Streamer_DestroyAllVisibleItems").call(playerid, type, serverwide);
	}

	public static int Streamer_CountVisibleItems(int playerid, int type, int serverwide) {
		return (int) functions.get("Streamer_CountVisibleItems").call(playerid, type, serverwide);
	}

	public static int Streamer_DestroyAllItems(int type, int serverwide) {
		return (int) functions.get("Streamer_DestroyAllItems").call(type, serverwide);
	}

	public static int Streamer_CountItems(int type, int serverwide) {
		return (int) functions.get("Streamer_CountItems").call(type, serverwide);
	}

	public static int CreateDynamicObject(int modelid, float x, float y, float z, float rx, float ry, float rz, int worldid, int interiorid, int playerid, float streamdistance, float drawdistance) {
		return (int) functions.get("CreateDynamicObject").call(modelid, x, y, z, rx, ry, rz, worldid, interiorid, playerid, streamdistance, drawdistance);
	}

	public static int DestroyDynamicObject(int objectid) {
		return (int) functions.get("DestroyDynamicObject").call(objectid);
	}

	public static int IsValidDynamicObject(int objectid) {
		return (int) functions.get("IsValidDynamicObject").call(objectid);
	}

	public static int SetDynamicObjectPos(int objectid, float x, float y, float z) {
		return (int) functions.get("SetDynamicObjectPos").call(objectid, x, y, z);
	}

	public static int GetDynamicObjectPos(int objectid, ReferenceFloat x, ReferenceFloat y, ReferenceFloat z) {
		return (int) functions.get("GetDynamicObjectPos").call(objectid, x, y, z);
	}

	public static int SetDynamicObjectRot(int objectid, float rx, float ry, float rz) {
		return (int) functions.get("SetDynamicObjectRot").call(objectid, rx, ry, rz);
	}

	public static int GetDynamicObjectRot(int objectid, ReferenceFloat rx, ReferenceFloat ry, ReferenceFloat rz) {
		return (int) functions.get("GetDynamicObjectRot").call(objectid, rx, ry, rz);
	}

	public static int SetDynamicObjectNoCameraCol(int objectid) {
		return (int) functions.get("SetDynamicObjectNoCameraCol").call(objectid);
	}

	public static int GetDynamicObjectNoCameraCol(int objectid) {
		return (int) functions.get("GetDynamicObjectNoCameraCol").call(objectid);
	}

	public static int MoveDynamicObject(int objectid, float x, float y, float z, float speed, float rx, float ry, float rz) {
		return (int) functions.get("MoveDynamicObject").call(objectid, x, y, z, speed, rx, ry, rz);
	}

	public static int StopDynamicObject(int objectid) {
		return (int) functions.get("StopDynamicObject").call(objectid);
	}

	public static int IsDynamicObjectMoving(int objectid) {
		return (int) functions.get("IsDynamicObjectMoving").call(objectid);
	}

	public static int AttachCameraToDynamicObject(int playerid, int objectid) {
		return (int) functions.get("AttachCameraToDynamicObject").call(playerid, objectid);
	}

	public static int AttachDynamicObjectToObject(int objectid, int attachtoid, float offsetx, float offsety, float offsetz, float rx, float ry, float rz, int syncrotation) {
		return (int) functions.get("AttachDynamicObjectToObject").call(objectid, attachtoid, offsetx, offsety, offsetz, rx, ry, rz, syncrotation);
	}

	public static int AttachDynamicObjectToPlayer(int objectid, int playerid, float offsetx, float offsety, float offsetz, float rx, float ry, float rz) {
		return (int) functions.get("AttachDynamicObjectToPlayer").call(objectid, playerid, offsetx, offsety, offsetz, rx, ry, rz);
	}

	public static int AttachDynamicObjectToVehicle(int objectid, int vehicleid, float offsetx, float offsety, float offsetz, float rx, float ry, float rz) {
		return (int) functions.get("AttachDynamicObjectToVehicle").call(objectid, vehicleid, offsetx, offsety, offsetz, rx, ry, rz);
	}

	public static int EditDynamicObject(int playerid, int objectid) {
		return (int) functions.get("EditDynamicObject").call(playerid, objectid);
	}

	public static int IsDynamicObjectMaterialUsed(int objectid, int materialindex) {
		return (int) functions.get("IsDynamicObjectMaterialUsed").call(objectid, materialindex);
	}

	public static int GetDynamicObjectMaterial(int objectid, int materialindex, ReferenceInt modelid, ReferenceString txdname, ReferenceString texturename, ReferenceInt materialcolor, int maxtxdname, int maxtexturename) {
		return (int) functions.get("GetDynamicObjectMaterial").call(objectid, materialindex, modelid, txdname, texturename, materialcolor, maxtxdname, maxtexturename);
	}

	public static int SetDynamicObjectMaterial(int objectid, int materialindex, int modelid, String txdname, String texturename, int materialcolor) {
		return (int) functions.get("SetDynamicObjectMaterial").call(objectid, materialindex, modelid, txdname, texturename, materialcolor);
	}

	public static int IsDynamicObjectMaterialTextUsed(int objectid, int materialindex) {
		return (int) functions.get("IsDynamicObjectMaterialTextUsed").call(objectid, materialindex);
	}

	public static int GetDynamicObjectMaterialText(int objectid, int materialindex, ReferenceString text, ReferenceInt materialsize, ReferenceString fontface, ReferenceInt fontsize, ReferenceInt bold, ReferenceInt fontcolor, ReferenceInt backcolor, ReferenceInt textalignment, int maxtext, int maxfontface) {
		return (int) functions.get("GetDynamicObjectMaterialText").call(objectid, materialindex, text, materialsize, fontface, fontsize, bold, fontcolor, backcolor, textalignment, maxtext, maxfontface);
	}

	public static int SetDynamicObjectMaterialText(int objectid, int materialindex, String text, int materialsize, String fontface, int fontsize, int bold, int fontcolor, int backcolor, int textalignment) {
		return (int) functions.get("SetDynamicObjectMaterialText").call(objectid, materialindex, text, materialsize, fontface, fontsize, bold, fontcolor, backcolor, textalignment);
	}

	public static int CreateDynamicPickup(int modelid, int type, float x, float y, float z, int worldid, int interiorid, int playerid, float streamdistance) {
		return (int) functions.get("CreateDynamicPickup").call(modelid, type, x, y, z, worldid, interiorid, playerid, streamdistance);
	}

	public static int DestroyDynamicPickup(int pickupid) {
		return (int) functions.get("DestroyDynamicPickup").call(pickupid);
	}

	public static int IsValidDynamicPickup(int pickupid) {
		return (int) functions.get("IsValidDynamicPickup").call(pickupid);
	}

	public static int CreateDynamicCP(float x, float y, float z, float size, int worldid, int interiorid, int playerid, float streamdistance) {
		return (int) functions.get("CreateDynamicCP").call(x, y, z, size, worldid, interiorid, playerid, streamdistance);
	}

	public static int DestroyDynamicCP(int checkpointid) {
		return (int) functions.get("DestroyDynamicCP").call(checkpointid);
	}

	public static int IsValidDynamicCP(int checkpointid) {
		return (int) functions.get("IsValidDynamicCP").call(checkpointid);
	}

	public static int TogglePlayerDynamicCP(int playerid, int checkpointid, int toggle) {
		return (int) functions.get("TogglePlayerDynamicCP").call(playerid, checkpointid, toggle);
	}

	public static int TogglePlayerAllDynamicCPs(int playerid, int toggle) {
		return (int) functions.get("TogglePlayerAllDynamicCPs").call(playerid, toggle);
	}

	public static int IsPlayerInDynamicCP(int playerid, int checkpointid) {
		return (int) functions.get("IsPlayerInDynamicCP").call(playerid, checkpointid);
	}

	public static int GetPlayerVisibleDynamicCP(int playerid) {
		return (int) functions.get("GetPlayerVisibleDynamicCP").call(playerid);
	}

	public static int CreateDynamicRaceCP(int type, float x, float y, float z, float nextx, float nexty, float nextz, float size, int worldid, int interiorid, int playerid, float streamdistance) {
		return (int) functions.get("CreateDynamicRaceCP").call(type, x, y, z, nextx, nexty, nextz, size, worldid, interiorid, playerid, streamdistance);
	}

	public static int DestroyDynamicRaceCP(int checkpointid) {
		return (int) functions.get("DestroyDynamicRaceCP").call(checkpointid);
	}

	public static int IsValidDynamicRaceCP(int checkpointid) {
		return (int) functions.get("IsValidDynamicRaceCP").call(checkpointid);
	}

	public static int TogglePlayerDynamicRaceCP(int playerid, int checkpointid, int toggle) {
		return (int) functions.get("TogglePlayerDynamicRaceCP").call(playerid, checkpointid, toggle);
	}

	public static int TogglePlayerAllDynamicRaceCPs(int playerid, int toggle) {
		return (int) functions.get("TogglePlayerAllDynamicRaceCPs").call(playerid, toggle);
	}

	public static int IsPlayerInDynamicRaceCP(int playerid, int checkpointid) {
		return (int) functions.get("IsPlayerInDynamicRaceCP").call(playerid, checkpointid);
	}

	public static int GetPlayerVisibleDynamicRaceCP(int playerid) {
		return (int) functions.get("GetPlayerVisibleDynamicRaceCP").call(playerid);
	}

	public static int CreateDynamicMapIcon(float x, float y, float z, int type, int color, int worldid, int interiorid, int playerid, float streamdistance, int style) {
		return (int) functions.get("CreateDynamicMapIcon").call(x, y, z, type, color, worldid, interiorid, playerid, streamdistance, style);
	}

	public static int DestroyDynamicMapIcon(int iconid) {
		return (int) functions.get("DestroyDynamicMapIcon").call(iconid);
	}

	public static int IsValidDynamicMapIcon(int iconid) {
		return (int) functions.get("IsValidDynamicMapIcon").call(iconid);
	}

	public static int CreateDynamic3DTextLabel(String text, int color, float x, float y, float z, float drawdistance, int attachedplayer, int attachedvehicle, int testlos, int worldid, int interiorid, int playerid, float streamdistance) {
		return (int) functions.get("CreateDynamic3DTextLabel").call(text, color, x, y, z, drawdistance, attachedplayer, attachedvehicle, testlos, worldid, interiorid, playerid, streamdistance);
	}


    public static int CreateDynamic3DTextLabelEx(String text, int color, float x, float y, float z, float drawDistance, int attachedPlayer, int attachedVehicle,
                                                 boolean testLos, float streamDistance, Integer[] worlds, Integer[] interiors, Integer[] players, int maxWorlds, int maxInteriors, int maxPlayers) {
        return (int) functions.get("CreateDynamic3DTextLabelEx").call(text, color, x, y, z, drawDistance, attachedPlayer, attachedVehicle, testLos ? 1 : 0,
                streamDistance, (Object)worlds, (Object)interiors, (Object)players, maxWorlds, maxInteriors, maxPlayers);
    }

	public static int DestroyDynamic3DTextLabel(int id) {
		return (int) functions.get("DestroyDynamic3DTextLabel").call(id);
	}

	public static int IsValidDynamic3DTextLabel(int id) {
		return (int) functions.get("IsValidDynamic3DTextLabel").call(id);
	}

	public static int GetDynamic3DTextLabelText(int id, ReferenceString text, int maxtext) {
		return (int) functions.get("GetDynamic3DTextLabelText").call(id, text, maxtext);
	}

	public static int UpdateDynamic3DTextLabelText(int id, int color, String text) {
		return (int) functions.get("UpdateDynamic3DTextLabelText").call(id, color, text);
	}

	public static int CreateDynamicCircle(float x, float y, float size, int worldid, int interiorid, int playerid) {
		return (int) functions.get("CreateDynamicCircle").call(x, y, size, worldid, interiorid, playerid);
	}

	public static int CreateDynamicCylinder(float x, float y, float minz, float maxz, float size, int worldid, int interiorid, int playerid) {
		return (int) functions.get("CreateDynamicCylinder").call(x, y, minz, maxz, size, worldid, interiorid, playerid);
	}

	public static int CreateDynamicSphere(float x, float y, float z, float size, int worldid, int interiorid, int playerid) {
		return (int) functions.get("CreateDynamicSphere").call(x, y, z, size, worldid, interiorid, playerid);
	}

	public static int CreateDynamicRectangle(float minx, float miny, float maxx, float maxy, int worldid, int interiorid, int playerid) {
		return (int) functions.get("CreateDynamicRectangle").call(minx, miny, maxx, maxy, worldid, interiorid, playerid);
	}

	public static int CreateDynamicCuboid(float minx, float miny, float minz, float maxx, float maxy, float maxz, int worldid, int interiorid, int playerid) {
		return (int) functions.get("CreateDynamicCuboid").call(minx, miny, minz, maxx, maxy, maxz, worldid, interiorid, playerid);
	}

	public static int CreateDynamicCube(float minx, float miny, float minz, float maxx, float maxy, float maxz, int worldid, int interiorid, int playerid) {
		return (int) functions.get("CreateDynamicCube").call(minx, miny, minz, maxx, maxy, maxz, worldid, interiorid, playerid);
	}

	public static int DestroyDynamicArea(int areaid) {
		return (int) functions.get("DestroyDynamicArea").call(areaid);
	}

	public static int IsValidDynamicArea(int areaid) {
		return (int) functions.get("IsValidDynamicArea").call(areaid);
	}

	public static int GetDynamicPolygonNumberPoints(int areaid) {
		return (int) functions.get("GetDynamicPolygonNumberPoints").call(areaid);
	}

	public static int TogglePlayerDynamicArea(int playerid, int areaid, int toggle) {
		return (int) functions.get("TogglePlayerDynamicArea").call(playerid, areaid, toggle);
	}

	public static int TogglePlayerAllDynamicAreas(int playerid, int toggle) {
		return (int) functions.get("TogglePlayerAllDynamicAreas").call(playerid, toggle);
	}

	public static int IsPlayerInDynamicArea(int playerid, int areaid, int recheck) {
		return (int) functions.get("IsPlayerInDynamicArea").call(playerid, areaid, recheck);
	}

	public static int IsPlayerInAnyDynamicArea(int playerid, int recheck) {
		return (int) functions.get("IsPlayerInAnyDynamicArea").call(playerid, recheck);
	}

	public static int IsAnyPlayerInDynamicArea(int areaid, int recheck) {
		return (int) functions.get("IsAnyPlayerInDynamicArea").call(areaid, recheck);
	}

	public static int IsAnyPlayerInAnyDynamicArea(int recheck) {
		return (int) functions.get("IsAnyPlayerInAnyDynamicArea").call(recheck);
	}

	public static int GetPlayerNumberDynamicAreas(int playerid) {
		return (int) functions.get("GetPlayerNumberDynamicAreas").call(playerid);
	}

	public static int IsPointInDynamicArea(int areaid, float x, float y, float z) {
		return (int) functions.get("IsPointInDynamicArea").call(areaid, x, y, z);
	}

	public static int IsPointInAnyDynamicArea(float x, float y, float z) {
		return (int) functions.get("IsPointInAnyDynamicArea").call(x, y, z);
	}

	public static int GetNumberDynamicAreasForPoint(float x, float y, float z) {
		return (int) functions.get("GetNumberDynamicAreasForPoint").call(x, y, z);
	}

	public static int AttachDynamicAreaToObject(int areaid, int objectid, int type, int playerid, float offsetx, float offsety, float offsetz) {
		return (int) functions.get("AttachDynamicAreaToObject").call(areaid, objectid, type, playerid, offsetx, offsety, offsetz);
	}

	public static int AttachDynamicAreaToPlayer(int areaid, int playerid, float offsetx, float offsety, float offsetz) {
		return (int) functions.get("AttachDynamicAreaToPlayer").call(areaid, playerid, offsetx, offsety, offsetz);
	}

	public static int AttachDynamicAreaToVehicle(int areaid, int vehicleid, float offsetx, float offsety, float offsetz) {
		return (int) functions.get("AttachDynamicAreaToVehicle").call(areaid, vehicleid, offsetx, offsety, offsetz);
	}

}