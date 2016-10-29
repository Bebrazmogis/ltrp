package lt.maze.ysf.object;

import lt.maze.ysf.Functions;
import lt.maze.ysf.object.impl.YSFObjectImpl;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.amx.types.ReferenceInt;
import net.gtaun.shoebill.amx.types.ReferenceString;
import net.gtaun.shoebill.common.player.PlayerLifecycleObject;
import net.gtaun.shoebill.constant.*;
import net.gtaun.shoebill.data.*;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.exception.IllegalLengthException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerObject;
import net.gtaun.shoebill.object.SampObject;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class YSFPlayer extends PlayerLifecycleObject {

    public YSFPlayer(EventManager eventManager, Player player) {
        super(eventManager, player);
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onDestroy() {

    }

    public float getGravity() {
        return Functions.GetPlayerGravity(getPlayer().getId());
    }

    public void setGravity(float gravity) {
        Functions.SetPlayerGravity(getPlayer().getId(), gravity);
    }

    public void setRconAdmin(boolean set) {
        Functions.SetPlayerAdmin(getPlayer().getId(), set);
    }

    public void setTeamForPlayer(Player player, int teamId) {
        Functions.SetPlayerTeamForPlayer(getPlayer().getId(), player.getId(), teamId);
    }

    public int getTeamForPlayer(Player player) {
        return Functions.GetPlayerTeamForPlayer(getPlayer().getId(), player.getId());
    }

    public void setSkinForPlayer(Player p, int skin) {
        Functions.SetPlayerSkinForPlayer(getPlayer().getId(), p.getId(), skin);
    }

    public int getSkinForPlayer(Player p) {
        return Functions.GetPlayerSkinForPlayer(getPlayer().getId(), p.getId());
    }

    public void setNameForPlayer(Player p, String name) throws IllegalLengthException, AlreadyExistException {
        p.setName(name);
        Functions.SetPlayerNameForPlayer(getPlayer().getId(), p.getId(), name);
    }

    public String getNameForPlayer(Player p) {
        ReferenceString refS = new ReferenceString("", Player.MAX_NAME_LENGTH);
        Functions.GetPlayerNameForPlayer(getPlayer().getId(), p.getId(), refS, refS.getLength());
        return refS.getValue();
    }
    public void setFightStyleForPlayer(Player p, FightStyle fightStyle) {
        Functions.SetPlayerFightStyleForPlayer(getPlayer().getId(), p.getId(), fightStyle.getValue());
    }

    public FightStyle getFightStyleForPlayer(Player p) {
        return FightStyle.get(Functions.GetPlayerFightStyleForPlayer(getPlayer().getId(), p.getId()));
    }

    public void setPosForPlayer(Player p, Vector3D position, boolean sync) {
        Functions.SetPlayerPosForPlayer(getPlayer().getId(), p.getId(), position.x, position.y, position.z, sync);
    }

    public void setPosForPlayer(Player p, Vector3D position) {
       setPosForPlayer(p, position, true);
    }

    public void setRotationQuatForPlayer(Player p, float v, Vector3D vector3D, boolean sync) {
        Functions.SetPlayerRotationQuatForPlayer(getPlayer().getId(), p.getId(), v, vector3D.x, vector3D.y, vector3D.z, sync);
    }

    public void setRotationQuatForPlayer(Player p, float v, Vector3D vector3D) {
        setRotationQuatForPlayer(p, v, vector3D, true);
    }

    public int getWeather() {
        return Functions.GetPlayerWeather(getPlayer().getId());
    }

    public void setWidescreen(boolean set) {
        Functions.TogglePlayerWidescreen(getPlayer().getId(), set);
    }

    public boolean isWidescreen() {
        return Functions.IsPlayerWidescreenToggled(getPlayer().getId()) != 0;
    }

    public int getSkillLevel(WeaponSkill skill) {
        return Functions.GetPlayerSkillLevel(getPlayer().getId(), skill.getValue());
    }

    public boolean isCheckpointActive() {
        return Functions.IsPlayerCheckpointActive(getPlayer().getId()) != 0;
    }

    public Radius getCheckpoint() {
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        ReferenceFloat size = new ReferenceFloat(0f);
        Functions.GetPlayerCheckpoint(getPlayer().getId(), x, y, z, size);
        return new Radius(x.getValue(), y.getValue(), z.getValue(), size.getValue());
    }

    public boolean isRaceCheckpointActive() {
        return Functions.IsPlayerRaceCheckpointActive(getPlayer().getId()) != 0;
    }
/*
native GetPlayerRaceCheckpoint(playerid, &Float:fX, &Float:fY, &Float:fZ, &Float:fNextX, &Float:fNextY, &Float:fNextZ, &Float:fSize);
    public Radius getRaceCheckpoint() {
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        ReferenceFloat nx = new ReferenceFloat(0f);
        ReferenceFloat ny = new ReferenceFloat(0f);
        ReferenceFloat nz = new ReferenceFloat(0f);
        ReferenceFloat size = new ReferenceFloat(0f);
        Functions.GetPlayerRaceCheckpoint(getPlayer().getId(), x, y, z, nx, ny, nz, size);
        return new Radius(x.getValue(), y.getValue(), z.getValue(), size.getValue());
    }
*/

    public Area getWorldBounds() {
        ReferenceFloat minX = new ReferenceFloat(0f);
        ReferenceFloat minY = new ReferenceFloat(0);
        ReferenceFloat maxX = new ReferenceFloat(0f);
        ReferenceFloat maxY = new ReferenceFloat(0f);
        Functions.GetPlayerWorldBounds(getPlayer().getId(), maxX, minX, maxY, minY);
        return new Area(minX.getValue(), minY.getValue(), maxX.getValue(), maxY.getValue());
    }

    public boolean isInModShop() {
        return Functions.IsPlayerInModShop(getPlayer().getId()) != 0;
    }

    public boolean getSirenState() {
        return Functions.GetPlayerSirenState(getPlayer().getId()) != 0;
    }

    public boolean getLandingGearState() {
        return Functions.GetPlayerLandingGearState(getPlayer().getId()) != 0;
    }

    public boolean getHydraReactorAngle() {
        return Functions.GetPlayerHydraReactorAngle(getPlayer().getId()) != 0;
    }

    public float getTrainSpeed() {
        return Functions.GetPlayerTrainSpeed(getPlayer().getId());
    }

    public float getZAim() {
        return Functions.GetPlayerZAim(getPlayer().getId());
    }

    public Vector3D getSurfingOffsets() {
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        Functions.GetPlayerSurfingOffsets(getPlayer().getId(), x, y, z);
        return new Vector3D(x.getValue(), y.getValue(), z.getValue());
    }

    public Radius getRotationQuat() {
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        ReferenceFloat size = new ReferenceFloat(0f);
        Functions.GetPlayerRotationQuat(getPlayer().getId(), x, y, z, size);
        return new Radius(x.getValue(), y.getValue(), z.getValue(), size.getValue());
    }

    public Player getSpectate() {
        return Player.get(Functions.GetPlayerSpectateID(getPlayer().getId()));
    }

    public SpectateMode getSpectateType() {
        return SpectateMode.get(Functions.GetPlayerSpectateType(getPlayer().getId()));
    }

    public Vehicle getLastSyncedVehicle() {
        return Vehicle.get(Functions.GetPlayerLastSyncedVehicleID(getPlayer().getId()));
    }

    public Vehicle getLastSyncedTrailer() {
        return Vehicle.get(Functions.GetPlayerLastSyncedTrailerID(getPlayer().getId()));
    }

    public SpawnInfo getSpawnInfo() {
        ReferenceInt team = new ReferenceInt(0);
        ReferenceInt skin = new ReferenceInt(0);
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        ReferenceFloat angle = new ReferenceFloat(0f);
        ReferenceInt wep1 = new ReferenceInt(0);
        ReferenceInt ammo1 = new ReferenceInt(0);
        ReferenceInt wep2 = new ReferenceInt(0);
        ReferenceInt ammo2 = new ReferenceInt(0);
        ReferenceInt wep3 = new ReferenceInt(0);
        ReferenceInt ammo3 = new ReferenceInt(0);
        Functions.GetSpawnInfo(getPlayer().getId(), team, skin, x, y, z, angle, wep1, ammo1, wep2, ammo2, wep3, ammo3);
        return new SpawnInfo(x.getValue(), y.getValue(), z.getValue(), 0, 0, angle.getValue(), skin.getValue(), team.getValue(),
                WeaponModel.get(wep1.getValue()), ammo1.getValue(), WeaponModel.get(wep2.getValue()), ammo2.getValue(), WeaponModel.get(wep3.getValue()), ammo3.getValue());
    }

    public void sendBulletData(int hitid, BulletHitType hitType, WeaponModel weaponModel, Vector3D originPos, Vector3D hitTarget, Vector3D hitCenter, Player p) {
        Functions.SendBulletData(getPlayer().getId(), hitid, hitType.getValue(), weaponModel.getId(), originPos.x, originPos.y, originPos.z, hitTarget.x, hitTarget.y, hitTarget.z, hitCenter.x, hitCenter.y, hitCenter.z, p == null ? -1 : p.getId());
    }

    public void showPlayer(Player p) {
        Functions.ShowPlayerForPlayer(getPlayer().getId(), p.getId());
    }

    public void hidePlayer(Player p) {
        Functions.HidePlayerForPlayer(getPlayer().getId(), p.getId());
    }

    public void setChatBubble(Player p, String text, Color color, float distance, int expiretime) {
        Functions.SetPlayerChatBubbleForPlayer(getPlayer().getId(), p.getId(), text, color.getValue(), distance, expiretime);
    }

    public void setVersion(String version) {
        Functions.SetPlayerVersion(getPlayer().getId(), version);
    }

    public boolean isSpawned() {
        return Functions.IsPlayerSpawned(getPlayer().getId()) != 0;
    }

    public boolean isPlayerControllable() {
        return Functions.IsPlayerControllable(getPlayer().getId()) != 0;
    }

    public void spawnForWorld() {
        Functions.SpawnForWorld(getPlayer().getId());
    }

    public void broadcastDeath() {
        Functions.BroadcastDeath(getPlayer().getId());
    }

    public boolean isCameraTargetEnabled() {
        return Functions.IsPlayerCameraTargetEnabled(getPlayer().getId()) != 0;
    }

    public void setDisabledKeysSync(int keys) {
        Functions.SetPlayerDisabledKeysSync(getPlayer().getId(), keys);
    }

    public int getDisabledKeysSync() {
        return Functions.GetPlayerDisabledKeysSync(getPlayer().getId());
    }

    public void setScoresPingUpdate(boolean toggle) {
        Functions.TogglePlayerScoresPingsUpdate(getPlayer().getId(), toggle);
    }

    public void setFakePing(boolean toggle) {
        Functions.TogglePlayerFakePing(getPlayer().getId(), toggle);
    }

    public void setFakePing(int ping) {
        Functions.SetPlayerFakePing(getPlayer().getId(), ping);
    }

    public void setOnPlayerList(boolean toggle) {
        Functions.TogglePlayerOnPlayerList(getPlayer().getId(), toggle);
    }

    public boolean isOnPlayerList() {
        return Functions.IsPlayerToggledOnPlayerList(getPlayer().getId()) != 0;
    }

    public boolean isPaused() {
        return Functions.IsPlayerPaused(getPlayer().getId()) != 0;
    }

    public int getPausedTime() {
        return Functions.GetPlayerPausedTime(getPlayer().getId());
    }

    public void setTimeoutTime(int ms) {
        Functions.SetTimeoutTime(getPlayer().getId(), ms);
    }

    public YSFObject getAttachedObject(int index) {
        ReferenceInt model = new ReferenceInt(0);
        ReferenceInt bone = new ReferenceInt(0);
        ReferenceFloat x = new ReferenceFloat(0f);
        ReferenceFloat y = new ReferenceFloat(0f);
        ReferenceFloat z = new ReferenceFloat(0f);
        ReferenceFloat rx = new ReferenceFloat(0f);
        ReferenceFloat ry = new ReferenceFloat(0f);
        ReferenceFloat rz = new ReferenceFloat(0f);
        ReferenceFloat sx = new ReferenceFloat(0f);
        ReferenceFloat sy = new ReferenceFloat(0f);
        ReferenceFloat sz = new ReferenceFloat(0f);
        ReferenceInt matcl1 = new ReferenceInt(0);
        ReferenceInt matcl2 = new ReferenceInt(0);
        YSFObject object = new YSFObjectImpl(SampObject.get(Functions.GetPlayerAttachedObject(getPlayer().getId(), index, model, bone, x, y, z, rx, ry, rz, sx, sy, sz, matcl1, matcl2)));
        return object;
    }

    public void attachObject(PlayerObject object, PlayerObject attachTo, Vector3D offsets, Vector3D rotations, boolean syncrot) {
        Functions.AttachPlayerObjectToObject(getPlayer().getId(), object.getId(), attachTo.getId(), offsets.x, offsets.y, offsets.z, rotations.x, rotations.y, rotations.z, syncrot ? 1 : 0);
    }

    public void attachObject(PlayerObject object, PlayerObject attachTo, Vector3D offsets, Vector3D rotations) {
        attachObject(object, attachTo, offsets, rotations, true);
    }
}
/*

// special - for attached objects
native GetPlayerAttachedObject(playerid, index, &modelid, &bone, &Float:fX, &Float:fY, &Float:fZ, &Float:fRotX, &Float:fRotY, &Float:fRotZ, &Float:fSacleX, &Float:fScaleY, &Float:fScaleZ, &materialcolor1, &materialcolor2);

 */