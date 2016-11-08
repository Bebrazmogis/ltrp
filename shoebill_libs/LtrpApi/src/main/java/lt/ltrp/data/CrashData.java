package lt.ltrp.data;


import lt.ltrp.object.LtrpPlayer;
import net.gtaun.shoebill.data.Location;


import java.sql.Date;

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
