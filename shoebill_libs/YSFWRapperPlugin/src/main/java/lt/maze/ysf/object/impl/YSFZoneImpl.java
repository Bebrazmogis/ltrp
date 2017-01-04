package lt.maze.ysf.object.impl;

import lt.maze.ysf.Functions;
import lt.maze.ysf.object.YSFZone;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.data.Area;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.entities.Player;
import net.gtaun.shoebill.object.Zone;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class YSFZoneImpl implements YSFZone {

    private Zone zone;

    public YSFZoneImpl(Zone zone) {
        this.zone = zone;
    }

    public Zone getZone() {
        return zone;
    }

    @Override
    public boolean isValid() {
        return Functions.IsValidGangZone(zone.getId()) != 0;
    }

    @Override
    public boolean isPlayerIn(Player p) {
        return Functions.IsPlayerInGangZone(p.getId(), zone.getId()) != 0;
    }

    @Override
    public boolean isVisibleForPlayer(Player p) {
        return Functions.IsGangZoneVisibleForPlayer(p.getId(), zone.getId()) != 0;
    }

    @Override
    public Color getColorForPlayer(Player p) {
        return new Color(Functions.GangZoneGetColorForPlayer(p.getId(), zone.getId()));
    }

    @Override
    public Color getFlashColorForPlayer(Player p) {
        return new Color(Functions.GangZoneGetFlashColorForPlayer(p.getId(), zone.getId()));
    }

    @Override
    public boolean isFlashingForPlayer(Player p) {
        return Functions.IsGangZoneFlashingForPlayer(p.getId(), zone.getId()) != 0;
    }


    @Override
    public Area getBounds() {
        ReferenceFloat minX = new ReferenceFloat(0f);
        ReferenceFloat minY = new ReferenceFloat(0);
        ReferenceFloat maxX = new ReferenceFloat(0f);
        ReferenceFloat maxY = new ReferenceFloat(0f);
        Functions.GangZoneGetPos(zone.getId(), minX, minY, maxX, minY);
        return new Area(minX.getValue(), minY.getValue(), maxX.getValue(), maxY.getValue());
    }
}
