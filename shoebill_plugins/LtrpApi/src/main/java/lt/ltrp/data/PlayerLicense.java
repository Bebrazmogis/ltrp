package lt.ltrp.data;

import lt.ltrp.constant.LicenseType;
import lt.ltrp.object.LtrpPlayer;

import java.sql.Timestamp;

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

    public void addWarning(LicenseWarning warn) {
        int index = -1;
        for(int i = 0 ; i < warnings.length; i++)
            if(warnings[i] == null)
            {
                index = i;
                break;
            }
        if(index != -1) {
            warnings[index] = warn;
        } else {
            LicenseWarning[] tmp = new LicenseWarning[warnings.length+1];
            index = 0;
            for(LicenseWarning warning : getWarnings()) {
                tmp[index++] = warning;
            }
            tmp[index] = warn;
            setWarnings(tmp);
        }
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

    @Override
    public boolean equals(Object o) {
        return o instanceof PlayerLicense && ((PlayerLicense) o).getId() == getId();
    }
}
