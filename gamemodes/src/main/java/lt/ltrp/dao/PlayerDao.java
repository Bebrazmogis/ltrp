package lt.ltrp.dao;

import javafx.util.Pair;
import lt.ltrp.player.*;
import lt.ltrp.vehicle.PlayerVehiclePermission;
import net.gtaun.shoebill.object.Player;

import java.util.List;
import java.util.Map;

/**
 * @author Bebras
 *         2015.11.12.
 */
public interface PlayerDao {


    public int getUserId(Player player);
    public String getPassword(LtrpPlayer player);
    public boolean loadData(LtrpPlayer player);
    public SpawnData getSpawnData(LtrpPlayer player);
    public JailData getJailData(LtrpPlayer player);
    public CrashData getCrashData(LtrpPlayer player);
    public boolean remove(LtrpPlayer player, CrashData data);
    public boolean remove(LtrpPlayer player, JailData jailData);
    public boolean setFactionManager(LtrpPlayer player);
    void insert(JailData data);
    void insertCrime(String suspect, String crime, String reporterName);
    Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> getVehiclePermissions(LtrpPlayer player);

    void updateLicenses(PlayerLicenses licenses);
    void updateLicense(PlayerLicense license);
    void insertLicense(PlayerLicense license);
    PlayerLicenses get(LtrpPlayer player);
}
