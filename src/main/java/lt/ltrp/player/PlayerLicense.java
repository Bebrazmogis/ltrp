package lt.ltrp.player;

import lt.ltrp.constant.LicenseType;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class PlayerLicense {

    private int id;
    private LicenseType type;
    private int stage;
    private Timestamp dateAquired;
    private LtrpPlayer player;
    private LicenseWarning[] warnings;

    public LtrpPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LtrpPlayer player) {
        this.player = player;
    }

    public int getId() {
        return id;
    }

    public void setWarnings(LicenseWarning[] warnings) {
        this.warnings = warnings;
    }

    public LicenseWarning[] getWarnings() {
        return warnings;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LicenseType getType() {
        return type;
    }

    public void setType(LicenseType type) {
        this.type = type;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public Timestamp getDateAquired() {
        return dateAquired;
    }

    public void setDateAquired(Timestamp dateAquired) {
        this.dateAquired = dateAquired;
    }
}
