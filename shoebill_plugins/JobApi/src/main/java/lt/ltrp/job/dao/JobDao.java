package lt.ltrp.job.dao;


import lt.ltrp.api.LoadingException;
import lt.ltrp.job.JobVehicle;
import lt.ltrp.job.data.TrashMissions;
import lt.ltrp.job.object.*;

import java.util.Collection;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.06.
 */
public interface JobDao {

    DrugDealerJob getDrugDealerJob(int id) throws LoadingException;
    MechanicJob getMechanicJob(int id) throws LoadingException;
    MedicJob getMedicJob(int id) throws LoadingException;
    OfficerJob getOfficerJob(int id) throws LoadingException;
    TrashManJob getTrashmanJob(int id) throws LoadingException;
    VehicleThiefJob getVehicleThiefJob(int id) throws LoadingException;

    void addLeader(Faction faction, int userid);
    void removeLeader(Faction faction, int userid);

    Map<String, Rank> getEmployeeList(Job job);


    Collection<JobVehicle> getVehicles(Job job) throws LoadingException;
    void insertVehicle(JobVehicle vehicle);
    void removeVehicle(JobVehicle vehicle);
    void updateVehicle(JobVehicle vehicle);

    Collection<Rank> getRanks(Job job) throws LoadingException;
    void insertRank(Rank rank);
    void removeRank(Rank rank);

    TrashMissions getTrashMissions();

}
