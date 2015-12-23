package lt.ltrp.dao;

import lt.ltrp.dmv.Dmv;
import lt.ltrp.dmv.DmvCheckpoint;
import lt.ltrp.dmv.DmvQuestion;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.object.Checkpoint;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.13.
 */
public interface DmvDao {


    List<Dmv> getDmvs();
    void update(Dmv dmv);
    void delete(Dmv dmv);

    List<DmvQuestion> getQuestions(Dmv dmv);
    void insert(Dmv dmv, DmvQuestion question);
    void update(DmvQuestion question);
    void delete(DmvQuestion question);

    List<DmvCheckpoint> getCheckpoints(Dmv dmv);
    void insert(Dmv dmv, DmvCheckpoint checkpoint);
    void update(DmvCheckpoint checkpoint);

    List<LtrpVehicle> getVehicles(Dmv dmv);
    void insert(Dmv dmv, LtrpVehicle vehicle);
    void update(LtrpVehicle vehicle);

}
