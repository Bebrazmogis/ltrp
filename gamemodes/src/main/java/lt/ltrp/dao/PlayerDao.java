package lt.ltrp.dao;

import lt.ltrp.player.CrashData;
import lt.ltrp.player.JailData;
import lt.ltrp.player.LtrpPlayer;
import lt.ltrp.player.SpawnData;
import net.gtaun.shoebill.object.Player;

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
}
