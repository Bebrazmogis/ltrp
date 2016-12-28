package lt.ltrp.player.licenses.data;

import lt.ltrp.object.PlayerData;
import lt.ltrp.player.licenses.constant.LicenseType;
import lt.ltrp.object.LtrpPlayer;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class PlayerLicenses {

    private static final int MAX_LICENSES = LicenseType.values().length;

    private PlayerLicense[] playerLicenses;
    private PlayerData player;
    private int licenseCount;

    public PlayerLicenses(PlayerData player) {
        playerLicenses = new PlayerLicense[MAX_LICENSES];
        licenseCount = 0;
        this.player = player;
    }

    public PlayerLicense[] get() {
        return playerLicenses;
    }


    public void add(PlayerLicense license) {
        playerLicenses[licenseCount++] = license;
    }

    public boolean contains(LicenseType type) {
        return get(type) != null;
    }

    public PlayerLicense get(LicenseType type) {
        for(int i = 0; i < licenseCount; i++) {
            if(playerLicenses[ i ].getType() == type) {
                return playerLicenses[ i ];
            }
        }
        return null;
    }

    public void remove(PlayerLicense license) {
        for(int i = 0; i < licenseCount; i++) {
            if(playerLicenses[ i ].equals(license)) {
                playerLicenses[ i ] = playerLicenses[licenseCount-1];
                playerLicenses[licenseCount-1] = null;
                licenseCount--;
                return;
            }
        }
    }
}
