package lt.ltrp.player.data;

import java.util.Date;

/**
 * @author Bebras
 *         2015.12.30.
 */
public class PlayerCrime {

    private int id;
    private String playerName, reporterName;
    private String crime;
    private Date date;
    private int fine;

    public PlayerCrime(String playerName, String reporterName, String crime, int fine) {
        this.playerName = playerName;
        this.reporterName = reporterName;
        this.crime = crime;
        this.fine = fine;
        this.date = new Date();
    }

    public PlayerCrime(int id, String playerName, String reporterName, String crime, Date date, int fine) {
        this.id = id;
        this.playerName = playerName;
        this.reporterName = reporterName;
        this.crime = crime;
        this.date = date;
        this.fine = fine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getCrime() {
        return crime;
    }

    public void setCrime(String crime) {
        this.crime = crime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
