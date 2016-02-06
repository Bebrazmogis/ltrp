package lt.ltrp.player;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.object.Player;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class CrashData {

    private LtrpPlayer player;
    private Location location;
    private Date date;


    public CrashData(LtrpPlayer player, Location location, Date date) {
        this.player = player;
        this.location = location;
        this.date = date;
    }

    public LtrpPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LtrpPlayer player) {
        this.player = player;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
