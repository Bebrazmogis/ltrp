package lt.ltrp.player;

import lt.ltrp.constant.LicenseType;

/**
 * @author Bebras
 *         2015.12.13.
 */
public class PlayerLicenses {

    private static final int MAX_LICENSES = LicenseType.values().length;

    public PlayerLicense[] playerLicenses;
    public LtrpPlayer player;
    public int licenseCount;

    public PlayerLicenses(LtrpPlayer player) {
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
}
