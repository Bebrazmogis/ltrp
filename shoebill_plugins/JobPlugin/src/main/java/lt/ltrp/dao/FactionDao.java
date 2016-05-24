package lt.ltrp.dao;

import lt.ltrp.LoadingException;
import lt.ltrp.object.Faction;
import lt.ltrp.object.FactionRank;

import java.util.Collection;

/**
 * @author Bebras
 *         2016.05.23.
 */
public interface FactionDao extends JobDao {

    /**
     * Adds a new leader to a faction. Note that it does not remove any previous leaders.
     * @param faction faction
     * @param userId leader UUID
     */
    void addLeader(Faction faction, int userId);

    /**
     * Removes the faction leader
     * @param faction faction
     * @param userId leader UUID
     */
    void removeLeader(Faction faction, int userId);

    Collection<FactionRank> getRanks(Faction faction) throws LoadingException;
    void insertRank(FactionRank rank);
    void removeRank(FactionRank rank);

    Faction get(int uuid);
    void update(Faction faction);

}
