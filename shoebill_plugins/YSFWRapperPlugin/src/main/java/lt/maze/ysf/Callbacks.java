package lt.maze.ysf;

import lt.maze.ysf.event.player.PlayerPauseStateChangeEvent;
import lt.maze.ysf.event.player.PlayerStateAndWeaponsUpdateEvent;
import lt.maze.ysf.event.playerzone.PlayerEnterPlayerZoneEvent;
import lt.maze.ysf.event.playerzone.PlayerLeavePlayerZoneEvent;
import lt.maze.ysf.event.rcon.RemoteRconPacketEvent;
import lt.maze.ysf.event.server.ServerMessageEvent;
import lt.maze.ysf.event.zone.PlayerEnterZoneEvent;
import lt.maze.ysf.event.zone.PlayerLeaveZoneEvent;
import lt.maze.ysf.object.YSFPlayerZone;
import net.gtaun.shoebill.amx.AmxInstanceManager;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Zone;
import net.gtaun.util.event.EventManager;

class Callbacks {

	public static void registerHandlers(AmxInstanceManager instanceManager, EventManager eventManager) {
		instanceManager.hookCallback("OnPlayerEnterGangZone", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int zoneid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            Zone zone = Zone.get(zoneid);
            if(p != null && zone != null)
                eventManager.dispatchEvent(new PlayerEnterZoneEvent(p, zone), p, zone);
		}, "ii");

		instanceManager.hookCallback("OnPlayerLeaveGangZone", amxCallEvent -> {
			int playerid = (Integer) amxCallEvent.getParameters()[0];
			int zoneid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            Zone zone = Zone.get(zoneid);
            if(p != null && zone != null)
                eventManager.dispatchEvent(new PlayerLeaveZoneEvent(p, zone), p, zone);
		}, "ii");

		instanceManager.hookCallback("OnPlayerEnterPlayerGangZone", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int zoneid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            YSFPlayerZone zone = YSFPlayerZone.get(zoneid);
            if(p != null && zone != null)
                eventManager.dispatchEvent(new PlayerEnterPlayerZoneEvent(p, zone), p, zone);

		}, "ii");

		instanceManager.hookCallback("OnPlayerLeavePlayerGangZone", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int zoneid = (int) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            YSFPlayerZone zone = YSFPlayerZone.get(zoneid);
            if(p != null && zone != null)
                eventManager.dispatchEvent(new PlayerLeavePlayerZoneEvent(p, zone), p, zone);

        }, "ii");
/*
		instanceManager.hookCallback("OnPlayerPickUpPlayerPickup", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
			int pickupid = (int) amxCallEvent.getParameters()[1];

		}, "ii");
*/
		instanceManager.hookCallback("OnPlayerPauseStateChange", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
            boolean pausestate = (boolean) amxCallEvent.getParameters()[1];
            Player p = Player.get(playerid);
            if(p != null) {
                eventManager.dispatchEvent(new PlayerPauseStateChangeEvent(p, pausestate), p);
            }
		}, "ii");

		instanceManager.hookCallback("OnPlayerStatsAndWeaponsUpdate", amxCallEvent -> {
			int playerid = (int) amxCallEvent.getParameters()[0];
            Player p = Player.get(playerid);
            if(p != null) {
                eventManager.dispatchEvent(new PlayerStateAndWeaponsUpdateEvent(p), p);
            }
		}, "i");

		instanceManager.hookCallback("OnRemoteRCONPacket", amxCallEvent -> {
			String ipaddr = (String) amxCallEvent.getParameters()[0];
			int port = (int) amxCallEvent.getParameters()[1];
			String password = (String) amxCallEvent.getParameters()[2];
			int success = (int) amxCallEvent.getParameters()[3];
			String command = (String) amxCallEvent.getParameters()[4];
            eventManager.dispatchEvent(new RemoteRconPacketEvent(ipaddr, port, password, success != 0, command), ipaddr, password, command);
		}, "sisis");

		instanceManager.hookCallback("OnServerMessage", amxCallEvent -> {
			String msg = (String) amxCallEvent.getParameters()[0];
			eventManager.dispatchEvent(new ServerMessageEvent(msg), msg);
		}, "s");

	}

	public static void unregisterHandlers(AmxInstanceManager instanceManager) {
		instanceManager.unhookCallback("OnPlayerEnterGangZone");
		instanceManager.unhookCallback("OnPlayerLeaveGangZone");
		instanceManager.unhookCallback("OnPlayerEnterPlayerGangZone");
		instanceManager.unhookCallback("OnPlayerLeavePlayerGangZone");
		instanceManager.unhookCallback("OnPlayerPickUpPlayerPickup");
		instanceManager.unhookCallback("OnPlayerPauseStateChange");
		instanceManager.unhookCallback("OnPlayerStatsAndWeaponsUpdate");
		instanceManager.unhookCallback("OnRemoteRCONPacket");
		instanceManager.unhookCallback("OnServerMessage");
	}
}