package lt.ltrp.dao;

import lt.ltrp.LoadingException;
import lt.ltrp.job.Faction;
import lt.ltrp.job.Job;
import lt.ltrp.job.Rank;
import lt.ltrp.job.drugdealer.DrugDealerJob;
import lt.ltrp.job.mechanic.MechanicJob;
import lt.ltrp.job.medic.MedicJob;
import lt.ltrp.job.policeman.OfficerJob;
import lt.ltrp.job.trashman.TrashManJob;
import lt.ltrp.job.trashman.TrashMissions;
import lt.ltrp.job.vehiclethief.VehicleThiefJob;
import lt.ltrp.vehicle.JobVehicle;

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
