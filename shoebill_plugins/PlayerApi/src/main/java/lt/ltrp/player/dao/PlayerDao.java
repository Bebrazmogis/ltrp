package lt.ltrp.player.dao;


import javafx.util.Pair;
import lt.ltrp.player.data.*;
import lt.ltrp.player.object.LtrpPlayer;
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
    public boolean update(LtrpPlayer player);
    public SpawnData getSpawnData(LtrpPlayer player);
    void setSpawnData(LtrpPlayer player);
    public JailData getJailData(LtrpPlayer player);
    public CrashData getCrashData(LtrpPlayer player);
    public boolean remove(LtrpPlayer player, CrashData data);
    public boolean remove(LtrpPlayer player, JailData jailData);
    public boolean setFactionManager(LtrpPlayer player);
    void insert(JailData data);
    void insertCrime(PlayerCrime crime);
    List<PlayerCrime> getCrimes(LtrpPlayer player);
    //Map<Integer, Pair<Integer, List<PlayerVehiclePermission>>> getVehiclePermissions(LtrpPlayer player);

    void updateLicenses(PlayerLicenses licenses);
    void updateLicense(PlayerLicense license);
    void insertLicense(PlayerLicense license);
    void insert(LicenseWarning warning);
    PlayerLicenses get(LtrpPlayer player);
    void delete(PlayerLicense license);

    // methods for manipulating offline users
    void removeJob(int userId);
    public String getUsername(int userId);
    public int getUserId(String username);
}
