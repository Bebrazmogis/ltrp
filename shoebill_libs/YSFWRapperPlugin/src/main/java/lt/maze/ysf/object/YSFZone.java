package lt.maze.ysf.object;

import lt.maze.ysf.object.impl.YSFVehicleImpl;
import lt.maze.ysf.object.impl.YSFZoneImpl;
import net.gtaun.shoebill.data.Area;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.03.
 */
public interface YSFZone {

    boolean isValid();
    boolean isPlayerIn(Player p);
    boolean isVisibleForPlayer(Player p);
    Color getColorForPlayer(Player p);
    Color getFlashColorForPlayer(Player p);
    boolean isFlashingForPlayer(Player p);
    Area getBounds();


    public static Collection<YSFZone> get() {
        return YSFObjectManager.getInstance().getZones();
    }

    public static YSFZone get(int id) {
        Optional<YSFZone> op = YSFObjectManager.getInstance().getZones()
                .stream()
                .filter(v -> v instanceof YSFZoneImpl && ((YSFZoneImpl)v).getZone().getId() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    public static YSFZone get(YSFZone zone) {
        Optional<YSFZone> op = YSFObjectManager.getInstance().getZones()
                .stream()
                .filter(v -> v instanceof YSFZoneImpl && ((YSFZoneImpl) v).getZone().equals(zone))
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }

    /*
    native IsValidGangZone(zoneid);
native IsPlayerInGangZone(playerid, zoneid);
native IsGangZoneVisibleForPlayer(playerid, zoneid);
native GangZoneGetColorForPlayer(playerid, zoneid);
native GangZoneGetFlashColorForPlayer(playerid, zoneid);
native IsGangZoneFlashingForPlayer(playerid, zoneid);
native GangZoneGetPos(zoneid, &Float:fMinX, &Float:fMinY, &Float:fMaxX, &Float:fMaxY);
     */

}
