package lt.maze.shoebilleventlogger;

import lt.maze.shoebilleventlogger.event.LogEvent;
import net.gtaun.shoebill.event.actor.ActorEvent;
import net.gtaun.shoebill.event.actor.ActorStreamInEvent;
import net.gtaun.shoebill.event.actor.ActorStreamOutEvent;
import net.gtaun.shoebill.event.amx.AmxLoadEvent;
import net.gtaun.shoebill.event.amx.AmxUnloadEvent;
import net.gtaun.shoebill.event.checkpoint.CheckpointEnterEvent;
import net.gtaun.shoebill.event.checkpoint.CheckpointLeaveEvent;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointEnterEvent;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointLeaveEvent;
import net.gtaun.shoebill.event.destroyable.DestroyEvent;
import net.gtaun.shoebill.event.dialog.DialogCloseEvent;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.event.menu.MenuExitedEvent;
import net.gtaun.shoebill.event.menu.MenuSelectedEvent;
import net.gtaun.shoebill.event.object.ObjectMovedEvent;
import net.gtaun.shoebill.event.object.PlayerObjectMovedEvent;
import net.gtaun.shoebill.event.player.*;
import net.gtaun.shoebill.event.rcon.RconCommandEvent;
import net.gtaun.shoebill.event.rcon.RconLoginEvent;
import net.gtaun.shoebill.event.resource.ResourceDisableEvent;
import net.gtaun.shoebill.event.resource.ResourceLoadEvent;
import net.gtaun.shoebill.event.resource.ResourceUnloadEvent;
import net.gtaun.shoebill.event.server.GameModeExitEvent;
import net.gtaun.shoebill.event.server.GameModeInitEvent;
import net.gtaun.shoebill.event.server.IncomingConnectionEvent;
import net.gtaun.shoebill.event.service.ServiceRegisterEvent;
import net.gtaun.shoebill.event.service.ServiceUnregisterEvent;
import net.gtaun.shoebill.event.vehicle.*;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;

/**
 * @author Bebras
 *         2016.03.18.
 */
public class EventHandler implements Destroyable {

    private EventManagerNode node;
    private boolean destroyed;

    public EventHandler(EventManager eventManager) {
        this.node = eventManager.createChildNode();

        node.registerHandler(ActorStreamInEvent.class, e -> {
             node.dispatchEvent(new LogEvent(e, String.format("player:%s actor:%s", e.getPlayer(), e.getActor())));
        });

        node.registerHandler(ActorStreamOutEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s actor:%s", e.getPlayer(), e.getActor())));
        });

        node.registerHandler(AmxLoadEvent.class, e -> {
           node.dispatchEvent(new LogEvent(e, String.format("amx:%s", e.getAmxInstance())));
        });

        node.registerHandler(AmxUnloadEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("amx:%s", e.getAmxInstance())));
        });

        node.registerHandler(CheckpointEnterEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s checkpoint:%s", e.getPlayer(), e.getCheckpoint())));
        });

        node.registerHandler(CheckpointLeaveEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s checkpoint:%s", e.getPlayer(), e.getCheckpoint())));
        });

        node.registerHandler(RaceCheckpointEnterEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s checkpoint:%s", e.getPlayer(), e.getCheckpoint())));
        });

        node.registerHandler(RaceCheckpointLeaveEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s checkpoint:%s", e.getPlayer(), e.getCheckpoint())));
        });

        node.registerHandler(DestroyEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("destroyable:%s", e.getDestroyable())));
        });

        node.registerHandler(DialogCloseEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s dialog:%s closetype:%s", e.getPlayer(), e.getDialog(), e.getType())));
        });

        node.registerHandler(DialogResponseEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s dialog:%s response:%s", e.getPlayer(), e.getDialog(), e.getDialogResponse())));
        });

        node.registerHandler(MenuExitedEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s menu:%s", e.getPlayer(), e.getMenu())));
        });

        node.registerHandler(MenuSelectedEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s menu:%s row:%s", e.getPlayer(), e.getMenu(), e.getRow())));
        });

        node.registerHandler(ObjectMovedEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("object:%s", e.getObject())));
        });

        node.registerHandler(PlayerObjectMovedEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s object:%s", e.getPlayer(), e.getObject())));
        });

        node.registerHandler(RconCommandEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("command:%s response:%d", e.getCommand(), e.getResponse())));
        });

        node.registerHandler(RconLoginEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("ip:%s password:not-showed", e.getIp())));
        });

        node.registerHandler(ResourceDisableEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("resource:%s", e.getResource())));
        });

        node.registerHandler(ResourceDisableEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("resource:%s", e.getResource())));
        });

        node.registerHandler(ResourceLoadEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("resource:%s", e.getResource())));
        });

        node.registerHandler(ResourceUnloadEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("resource:%s", e.getResource())));
        });

        node.registerHandler(GameModeInitEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, ""));
        });

        node.registerHandler(GameModeExitEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, ""));
        });

        node.registerHandler(IncomingConnectionEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("ip:%s playerid:%d port:%d", e.getIpAddress(), e.getPlayerId(), e.getPort())));
        });

        node.registerHandler(ServiceRegisterEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("service:%s type:%s previousprovider:%s previouservice:%s previoustype:%s serviceentry:%s previousserviceentry:%s proider:%s", e.getService(), e.getType(), e.getPreviousProvider(), e.getPreviousService(), e.getPreviousType(), e.getServiceEntry(), e.getPreviousServiceEntry(), e.getProvider())));
        });

        node.registerHandler(ServiceUnregisterEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("service:%s type:%sserviceentry:%s provider:%s", e.getService(), e.getType(), e.getServiceEntry(), e.getProvider())));
        });

        node.registerHandler(TrailerUpdateEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s vehicle:%s response:%d", e.getPlayer(), e.getVehicle(), e.getResponse())));
        });

        node.registerHandler(UnoccupiedVehicleUpdateEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s vehicle:%s newlocation:%s velocity:%s response:%d", e.getPlayer(), e.getVehicle(), e.getNewLocation(), e.getVelocity(), e.getResponse())));
        });

        node.registerHandler(VehicleCreateEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("vehicle:%s", e.getVehicle())));
        });

        node.registerHandler(VehicleDeathEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("vehicle:%s killer:%s", e.getVehicle(), e.getKiller())));
        });

        node.registerHandler(VehicleEnterEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s killer:%s", e.getPlayer(), e.getVehicle())));
        });

        node.registerHandler(VehicleExitEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s vehicle:%s", e.getPlayer(), e.getVehicle())));
        });

        node.registerHandler(VehicleModEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("vehicle:%s response:%d componentid:%d", e.getVehicle(), e.getResponse(), e.getComponentId())));
        });

        node.registerHandler(VehiclePaintjobEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("vehicle:%s paintjobid:%d", e.getVehicle(), e.getPaintjobId())));
        });

        node.registerHandler(VehicleResprayEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("vehicle:%s color1:%d color2:%d response:%d" ,e.getVehicle(), e.getColor1(), e.getColor2(), e.getResponse())));
        });

        node.registerHandler(VehicleSirenStateChangeEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s vehicle:%s newstate:%b", e.getPlayer(), e.getVehicle(), e.getNewState())));
        });

        node.registerHandler(VehicleSpawnEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("vehicle:%s",  e.getVehicle())));
        });

        node.registerHandler(VehicleStreamInEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s vehicle:%s", e.getPlayer(), e.getVehicle())));
        });

        node.registerHandler(VehicleStreamOutEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s vehicle:%s", e.getPlayer(), e.getVehicle())));
        });

        node.registerHandler(VehicleUpdateDamageEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s vehicle:%s", e.getPlayer(), e.getVehicle())));
        });

        node.registerHandler(VehicleUpdateEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s vehicle:%s playerseat:%d", e.getPlayer(), e.getVehicle(), e.getPlayerSeat())));
        });

        node.registerHandler(PlayerClickMapEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s position:%s", e.getPlayer(), e.getPosition())));
        });

        node.registerHandler(PlayerClickPlayerEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s clickedplayer:%s source:%s", e.getPlayer(), e.getClickedPlayer(), e.getSource())));
        });

        node.registerHandler(PlayerClickTextDrawEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s textdraw:%s response:%d", e.getPlayer(), e.getTextdraw(), e.getResponse())));
        });

        node.registerHandler(PlayerClickPlayerTextDrawEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s playertextdraw:%s response:%d", e.getPlayer(), e.getPlayerTextdraw(), e.getResponse())));
        });

        node.registerHandler(PlayerCommandEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s command:%s response:%d", e.getPlayer(), e.getCommand(), e.getResponse())));
        });

        node.registerHandler(PlayerConnectEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s response:%d", e.getPlayer(), e.getResponse())));
        });

        node.registerHandler(PlayerDamageActorEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s actor:%s amount:%d bodypart:%s weapon:%s", e.getPlayer(), e.getActor(), e.getAmount(), e.getBodypart(), e.getWeapon())));
        });

        node.registerHandler(PlayerDeathEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s killer:%s reason:%s", e.getPlayer(), e.getKiller(), e.getReason())));
        });

        node.registerHandler(PlayerDisconnectEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s reason:%s response:%d", e.getPlayer(), e.getReason(), e.getResponse())));
        });

        node.registerHandler(PlayerEditAttachedObjectEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s slot:%s offset:%s rotation:%s scale:%s response:%b", e.getPlayer(), e.getSlot(), e.getOffset(), e.getRotation(), e.getScale(), e.getResponse())));
        });

        node.registerHandler(PlayerEditObjectEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s newlocation:%s newrotation:%s object:%s", e.getPlayer(), e.getNewLocation(), e.getNewRotation(), e.getObject())));
        });

        node.registerHandler(PlayerEditPlayerObjectEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s newlocation:%s newrotation:%s object:%s", e.getPlayer(), e.getNewLocation(), e.getNewRotation(), e.getObject())));
        });

        node.registerHandler(PlayerEnterExitModShopEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s enterexit:%d interiorid:%d", e.getPlayer(), e.getEnterExit(), e.getInteriorId())));
        });

        node.registerHandler(PlayerGiveDamageEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s victim:%s weapon:%s amount:%f bodypart:%s", e.getPlayer(), e.getVictim(), e.getWeapon(), e.getAmount(), e.getBodyPart())));
        });

        node.registerHandler(PlayerInteriorChangeEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s oldinteriorid:%d", e.getPlayer(), e.getOldInteriorId())));
        });

        node.registerHandler(PlayerKeyStateChangeEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s oldstate:%s response:%d", e.getPlayer(), e.getOldState(), e.getResponse())));
        });

        node.registerHandler(PlayerKillEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s victim:%s reason:%s", e.getPlayer(), e.getVictim(), e.getReason())));
        });

        node.registerHandler(PlayerPickupEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s pickup:%s", e.getPlayer(), e.getPickup())));
        });

        node.registerHandler(PlayerRequestClassEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s classid:%d response:%d", e.getPlayer(), e.getClassId(), e.getResponse())));
        });

        node.registerHandler(PlayerRequestSpawnEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s response:%d", e.getPlayer(), e.getResponse())));
        });

        node.registerHandler(PlayerSelectObjectEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s object:%s", e.getPlayer(), e.getObject())));
        });

        node.registerHandler(PlayerSelectPlayerObjectEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s object:%s", e.getPlayer(), e.getObject())));
        });

        node.registerHandler(PlayerSpawnEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s response:%d", e.getPlayer(), e.getResponse())));
        });

        node.registerHandler(PlayerStateChangeEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s oldstate:%s", e.getPlayer(), e.getOldState())));
        });

        node.registerHandler(PlayerStreamInEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s forplayer:%s", e.getPlayer(), e.getForPlayer())));
        });

        node.registerHandler(PlayerStreamOutEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s forplayer:%s", e.getPlayer(), e.getForPlayer())));
        });

        node.registerHandler(PlayerTakeDamageEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s issuer:%s amount:%f bodypart:%s weapon:%s response:%d", e.getPlayer(), e.getIssuer(), e.getAmount(), e.getBodyPart(), e.getWeapon(), e.getResponse())));
        });

        node.registerHandler(PlayerTextEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s text:%s response:%d", e.getPlayer(), e.getText(), e.getResponse())));
        });

        node.registerHandler(PlayerUpdateEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s response:%d", e.getPlayer(), e.getResponse())));
        });

        node.registerHandler(PlayerWeaponShotEvent.class, e -> {
            node.dispatchEvent(new LogEvent(e, String.format("player:%s weapon:%s position:%s hittype:%s hitobject:%s hitplayer:%s hitplayerobject:%s response:%d", e.getPlayer(), e.getWeapon(), e.getPosition(), e.getHitType(), e.getHitObject(), e.getHitPlayer(), e.getHitPlayerObject(), e.getResponse())));
        });
    }

    @Override
    public void destroy() {
        destroyed = true;
        node.cancelAll();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
