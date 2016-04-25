package lt.ltrp.dao;


import lt.ltrp.LoadingException;
import lt.ltrp.data.JobData;
import lt.ltrp.data.TrashMissions;
import lt.ltrp.object.*;

import java.util.Collection;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.06.
 */
public interface JobDao {

    DrugDealerJob getDrugDealerJob(int id) throws LoadingException;
    MechanicJob getMechanicJob(int id) throws LoadingException;
    MedicFaction getMedicJob(int id) throws LoadingException;
    PoliceFaction getOfficerJob(int id) throws LoadingException;
    TrashManJob getTrashmanJob(int id) throws LoadingException;
    VehicleThiefJob getVehicleThiefJob(int id) throws LoadingException;

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

    Map<String, Rank> getEmployeeList(Job job);


    Collection<JobVehicle> getVehicles(Job job) throws LoadingException;
    void insertVehicle(JobVehicle vehicle);
    void removeVehicle(JobVehicle vehicle);
    void updateVehicle(JobVehicle vehicle);

    Collection<Rank> getRanks(Job job) throws LoadingException;
    void insertRank(Rank rank);
    void removeRank(Rank rank);

    TrashMissions getTrashMissions();

    void update(JobData jobData);
    void remove(JobData jobData);
    void insert(JobData jobData);
    JobData get(LtrpPlayer player);

}
