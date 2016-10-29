package lt.maze.streamer;

import lt.maze.streamer.event.*;
import lt.maze.streamer.object.*;
import net.gtaun.shoebill.amx.AmxInstanceManager;
import net.gtaun.shoebill.constant.ObjectEditResponse;
import net.gtaun.shoebill.constant.WeaponModel;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

class Callbacks {


    public static void registerHandlers(AmxInstanceManager instanceManager) {
        EventManager eventManager = StreamerPlugin.getInstance().getEventManager();
        instanceManager.hookCallback("OnDynamicObjectMoved", amxCallEvent -> {
			int objectid = (int) amxCallEvent.getParameters()[0];
            DynamicObject object = DynamicObject.get(objectid);
            if(object != null) {
                if(object.getHandler() != null) {
                    object.getHandler().onDynamicObjectMove(object);
                }
                eventManager.dispatchEvent(new DynamicObjectMovedEvent(object), object);
            }
		}, "i");

		instanceManager.hookCallback("OnPlayerEditDynamicObject", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int objectid = (int) amxCallEvent.getParameters()[1];
			int response = (int) amxCallEvent.getParameters()[2];
			float x = (float) amxCallEvent.getParameters()[3];
			float y = (float) amxCallEvent.getParameters()[4];
			float z = (float) amxCallEvent.getParameters()[5];
			float rx = (float) amxCallEvent.getParameters()[6];
			float ry = (float) amxCallEvent.getParameters()[7];
			float rz = (float) amxCallEvent.getParameters()[8];
            Player p = Player.get(playerid);
            DynamicObject obj = DynamicObject.get(objectid);
            ObjectEditResponse resp = ObjectEditResponse.get(response);
            if(p != null && obj != null && resp != null) {
                eventManager.dispatchEvent(new PlayerEditDynamicObjectEvent(p, new Vector3D(x, y, z), new Vector3D(rx, ry, rz), resp, obj), p, obj, resp);
            }
		}, "iiiffffff");

		instanceManager.hookCallback("OnPlayerSelectDynamicObject", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int objectid = (int) amxCallEvent.getParameters()[1];
			int modelid = (int) amxCallEvent.getParameters()[2];
			float x = (float) amxCallEvent.getParameters()[3];
			float y = (float) amxCallEvent.getParameters()[4];
			float z = (float) amxCallEvent.getParameters()[5];
            Player p = Player.get(playerid);
            DynamicObject obj = DynamicObject.get(objectid);
            if(p != null && obj != null) {
                eventManager.dispatchEvent(new PlayerSelectDynamicObjectEvent(p, obj, modelid, new Vector3D(x, y, z)), p, obj);
            }
		}, "iiifff");

		instanceManager.hookCallback("OnPlayerShootDynamicObject", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int weaponid = (int) amxCallEvent.getParameters()[1];
			int objectid = (int) amxCallEvent.getParameters()[2];
			float x = (float) amxCallEvent.getParameters()[3];
			float y = (float) amxCallEvent.getParameters()[4];
			float z = (float) amxCallEvent.getParameters()[5];
            Player p = Player.get(playerid);
            DynamicObject obj = DynamicObject.get(objectid);
            WeaponModel wep = WeaponModel.get(weaponid);
            if(p != null && obj != null) {
                eventManager.dispatchEvent(new PlayerShootDynamicObjectEvent(p, obj, wep, new Vector3D(x, y, z)), p, obj, wep);
            }
		}, "iiifff");

		instanceManager.hookCallback("OnPlayerPickUpDynamicPickup", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int pickupid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            DynamicPickup pickup = DynamicPickup.get(pickupid);
            if(p != null && pickup != null) {
                eventManager.dispatchEvent(new PlayerDynamicPickupEvent(p, pickup), p, pickup);
            }
		}, "ii");

		instanceManager.hookCallback("OnPlayerEnterDynamicCP", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int checkpointid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            DynamicCheckpoint cp = DynamicCheckpoint.get(checkpointid);
            if(p != null && cp != null) {
                eventManager.dispatchEvent(new PlayerEnterDynamicCheckpointEvent(p, cp), p, cp);
            }
		}, "ii");

		instanceManager.hookCallback("OnPlayerLeaveDynamicCP", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int checkpointid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            DynamicCheckpoint cp = DynamicCheckpoint.get(checkpointid);
            if(p != null && cp != null) {
                eventManager.dispatchEvent(new PlayerLeaveDynamicCheckpointEvent(p, cp), p, cp);
            }
		}, "ii");

		instanceManager.hookCallback("OnPlayerEnterDynamicRaceCP", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int checkpointid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            DynamicRaceCheckpoint cp = DynamicRaceCheckpoint.get(checkpointid);
            if(p != null && cp != null) {
                eventManager.dispatchEvent(new PlayerEnterDynamicRaceCheckpointEvent(p, cp), p, cp);
            }
		}, "ii");

		instanceManager.hookCallback("OnPlayerLeaveDynamicRaceCP", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int checkpointid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            DynamicRaceCheckpoint cp = DynamicRaceCheckpoint.get(checkpointid);
            if(p != null && cp != null) {
                eventManager.dispatchEvent(new PlayerLeaveDynamicRaceCheckpointEvent(p, cp), p, cp);
            }
		}, "ii");

		instanceManager.hookCallback("OnPlayerEnterDynamicArea", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int areaid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            DynamicArea area = DynamicArea.get(areaid);
            if(p != null && area != null) {
                eventManager.dispatchEvent(new PlayerEnterDynamicAreaEvent(p, area), p, area);
            }
		}, "ii");

		instanceManager.hookCallback("OnPlayerLeaveDynamicArea", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int areaid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            DynamicArea area = DynamicArea.get(areaid);
            if(p != null && area != null) {
                eventManager.dispatchEvent(new PlayerLeaveDynamicAreaEvent(p, area), p, area);
            }
		}, "ii");

		instanceManager.hookCallback("Streamer_OnPluginError", amxCallEvent -> {
			String error = (String) amxCallEvent.getParameters()[0];
			eventManager.dispatchEvent(new StreamerErrorEvent(error), error);
		}, "s");

	}

	public static void unregisterHandlers(AmxInstanceManager instanceManager) {
		instanceManager.unhookCallback("OnDynamicObjectMoved");
		instanceManager.unhookCallback("OnPlayerEditDynamicObject");
		instanceManager.unhookCallback("OnPlayerSelectDynamicObject");
		instanceManager.unhookCallback("OnPlayerShootDynamicObject");
		instanceManager.unhookCallback("OnPlayerPickUpDynamicPickup");
		instanceManager.unhookCallback("OnPlayerEnterDynamicCP");
		instanceManager.unhookCallback("OnPlayerLeaveDynamicCP");
		instanceManager.unhookCallback("OnPlayerEnterDynamicRaceCP");
		instanceManager.unhookCallback("OnPlayerLeaveDynamicRaceCP");
		instanceManager.unhookCallback("OnPlayerEnterDynamicArea");
		instanceManager.unhookCallback("OnPlayerLeaveDynamicArea");
		instanceManager.unhookCallback("Streamer_OnPluginError");
	}
}