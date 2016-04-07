package lt.maze.ysf.object;

import lt.maze.ysf.Functions;
import lt.maze.ysf.object.impl.YSFPlayerZoneImpl;
import net.gtaun.shoebill.data.Area;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;

import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.03.
 */
public interface YSFPlayerZone extends Destroyable {

    int getId();
    void show(Color color);
    void hide();
    void flash(Color color);
    void stopFlash();
    boolean isValid();
    boolean isPlayerIn();
    boolean isVisible();
    Color getColor();
    Color getFlashColor();
    boolean isFlashing();
    Area getBounds();

    public static YSFPlayerZone create(Player p, Area area) {
        return create(p, area.minX, area.minY, area.maxX, area.maxY);
    }

    public static  YSFPlayerZone create(Player p, float minx, float miny, float maxx, float maxy) {
        int id = Functions.CreatePlayerGangZone(p.getId(), minx, miny, maxx, maxy);
        YSFPlayerZone zone = new YSFPlayerZoneImpl(p, id);
        YSFObjectManager.getInstance().getPlayerZones().add(zone);
        return zone;
    }

    public static YSFPlayerZone get(int id) {
        Optional<YSFPlayerZone> op = YSFObjectManager.getInstance().getPlayerZones()
                .stream()
                .filter(z -> z.getId() == id)
                .findFirst();
        return op.isPresent() ? op.get() : null;
    }
}

/*
native CreatePlayerGangZone(playerid, Float:minx, Float:miny, Float:maxx, Float:maxy);
native PlayerGangZoneDestroy(playerid, zoneid);
native PlayerGangZoneShow(playerid, zoneid, color);
native PlayerGangZoneHide(playerid, zoneid);
native PlayerGangZoneFlash(playerid, zoneid, color);
native PlayerGangZoneStopFlash(playerid, zoneid);
native IsValidPlayerGangZone(playerid, zoneid);
native IsPlayerInPlayerGangZone(playerid, zoneid);
native IsPlayerGangZoneVisible(playerid, zoneid);
native PlayerGangZoneGetColor(playerid, zoneid);
native PlayerGangZoneGetFlashColor(playerid, zoneid);
native IsPlayerGangZoneFlashing(playerid, zoneid);
native PlayerGangZoneGetPos(playerid, zoneid, &Float:fMinX, &Float:fMinY, &Float:fMaxX, &Float:fMaxY);
 */