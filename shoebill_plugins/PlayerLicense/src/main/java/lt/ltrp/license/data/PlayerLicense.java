package lt.ltrp.license.data;

import lt.ltrp.object.PlayerData;
import lt.ltrp.player.licenses.constant.LicenseType;
import lt.ltrp.player.object.LtrpPlayer;

import java.time.LocalDateTime;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class PlayerLicense {

    private int id;
    private LicenseType type;
    private int stage;
    private LocalDateTime dateAcquired;
    private PlayerData player;
    private LicenseWarning[] warnings;

    public PlayerLicense(int id, LicenseType type, int stage, LocalDateTime date, PlayerData player) {
        this.id = id;
        this.type = type;
        this.stage = stage;
        this.dateAcquired = date;
        this.player = player;
    }

    public PlayerLicense(LicenseType type, int stage, LtrpPlayer player) {
        this(0, type, stage, LocalDateTime.now(), player);
    }

    public PlayerData getPlayer() {
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

    public LocalDateTime getDateAquired() {
        return dateAcquired;
    }

    public void setDateAquired(LocalDateTime dateAcquired) {
        this.dateAcquired = dateAcquired;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PlayerLicense && ((PlayerLicense) o).getId() == getId();
    }
}
