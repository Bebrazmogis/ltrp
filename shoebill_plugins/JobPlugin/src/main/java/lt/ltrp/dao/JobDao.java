package lt.ltrp.dao;


import lt.ltrp.LoadingException;
import lt.ltrp.object.Job;
import lt.ltrp.object.Rank;

import java.util.Collection;

/**
 * @author Bebras
 *         2015.12.06.
 */
public interface JobDao {
/*
    DrugDealerJob getDrugDealerJob(int id) throws LoadingException;
    MechanicJob getMechanicJob(int id) throws LoadingException;
    MedicFaction getMedicJob(int id) throws LoadingException;
    PoliceFaction getOfficerJob(int id) throws LoadingException;
    TrashManJob getTrashmanJob(int id) throws LoadingException;
   // VehicleThiefJob getVehicleThiefJob(int id) throws LoadingException;
*/


    Collection<Rank> getRanks(Job job) throws LoadingException;
    void insertRank(Rank rank);
    void removeRank(Rank rank);

    //TrashMissions getTrashMissions();

    Job get(int id);
    boolean isValid(int jobId);
    void update(Job job);

}
