package lt.ltrp.dao;

import lt.ltrp.data.Color;
import lt.ltrp.job.ContractJob;
import lt.ltrp.job.Faction;
import lt.ltrp.job.Job;
import lt.ltrp.job.trashman.TrashMissions;
import lt.ltrp.job.vehiclethief.VehicleThiefJob;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.06.
 */
public interface JobDao {

    /**
     * Reads all job data
     * @return an array with all existing jobs
     */
    List<Job> get();

    /**
     * Reads all {@link lt.ltrp.job.Faction} data
     * @return a list of existing factions
     */
    List<Faction> getFactions();

    /**
     * Reads the vehicle thief job data
     * @param id the id of the job
     * @return vehicle thief job, null if job couldn't be loaded(it doesn't exist)
     */
    VehicleThiefJob getVehicleThiefJob(int id);

    /**
     * Reads all {@link lt.ltrp.job.ContractJob} data
     * @return a list of existing contract jobs
     */
    List<ContractJob> getContractJobs();

    /**
     * Persists contract job data
     * @param job to be saved
     */
    void update(ContractJob job);

    /**
     * Persist faction job data
     * @param faction to be stored
     */
    void update(Faction faction);

    /**
     * Deletes a job permanently
     * @param job job to be deleted
     */
    void delete(Job job);

    /**
     * Generates a new unique ID for a job
     * @param name of the new job
     * @return the generated id or 0 on failure
     */
    int generateId(String name);


    /**
     * Retrieves the trash missions for thrashman job
     * @return a TrashMission instance containing all trash missions
     */
    TrashMissions getTrashMissions();

}
