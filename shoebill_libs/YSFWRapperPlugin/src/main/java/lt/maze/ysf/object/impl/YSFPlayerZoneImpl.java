package lt.maze.ysf.object.impl;

import lt.maze.ysf.Functions;
import lt.maze.ysf.object.YSFPlayerZone;
import net.gtaun.shoebill.amx.types.ReferenceFloat;
import net.gtaun.shoebill.data.Area;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;

/**
 * @author Bebras
 *         2016.04.03.
 */
public class YSFPlayerZoneImpl implements YSFPlayerZone {

    private int id;
    private Player player;
    private boolean destroyed;

    public YSFPlayerZoneImpl(Player p, int id) {
        this.id = id;
        this.player = p;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void show(Color color) {
        Functions.PlayerGangZoneShow(player.getId(), id, color.getValue());
    }

    @Override
    public void hide() {
        Functions.PlayerGangZoneHide(player.getId(), id);
    }

    @Override
    public void flash(Color color) {
        Functions.PlayerGangZoneFlash(player.getId(), id, color.getValue());
    }

    @Override
    public void stopFlash() {
        Functions.PlayerGangZoneStopFlash(player.getId(), id);
    }

    @Override
    public boolean isValid() {
        return Functions.IsValidPlayerGangZone(player.getId(), id) != 0;
    }

    @Override
    public boolean isPlayerIn() {
        return Functions.IsPlayerInPlayerGangZone(player.getId(), id) != 0;
    }

    @Override
    public boolean isVisible() {
        return Functions.IsPlayerGangZoneVisible(player.getId(), id) != 0;
    }

    @Override
    public Color getColor() {
        return new Color(Functions.PlayerGangZoneGetColor(player.getId(), id));
    }

    @Override
    public Color getFlashColor() {
        return new Color(Functions.PlayerGangZoneGetFlashColor(player.getId(), id));
    }

    @Override
    public boolean isFlashing() {
        return Functions.IsPlayerGangZoneFlashing(player.getId(), id) != 0;
    }

    @Override
    public Area getBounds() {
        ReferenceFloat minX = new ReferenceFloat(0f);
        ReferenceFloat minY = new ReferenceFloat(0);
        ReferenceFloat maxX = new ReferenceFloat(0f);
        ReferenceFloat maxY = new ReferenceFloat(0f);
        Functions.PlayerGangZoneGetPos(player.getId(), id, minX, minY, maxX, minY);
        return new Area(minX.getValue(), minY.getValue(), maxX.getValue(), maxY.getValue());
    }

    @Override
    public void destroy() {
        Functions.PlayerGangZoneDestroy(player.getId(), id);
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
