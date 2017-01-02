package lt.ltrp.data;


import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.entities.Player;


import java.sql.Date;

/**
 * @author Bebras
 *         2015.11.12.
 */
public class CrashData {

    private Player player;
    private Location location;
    private Date date;


    public CrashData(Player player, Location location, Date date) {
        this.player = player;
        this.location = location;
        this.date = date;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
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
