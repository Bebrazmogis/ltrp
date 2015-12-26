package lt.ltrp.dao;

import lt.ltrp.dmv.*;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.object.Checkpoint;

import java.util.List;

/**
 * @author Bebras
 *         2015.12.13.
 */
public interface DmvDao {

    /**
     * A DMV with this ID should be consider invalid
     */
    static final int INVALID_ID = 0;


    /**
     * Loads a question DMV, Dmv must have ID set
     * @param dmv dmv
     */
    void getQuestionDmv(QuestionDmv dmv);

    /**
     * Loads a checkpoint DMV, Dmv must have ID set
     * @param dmv dmv
     */
    void getCheckpointDmv(CheckpointDmv dmv);

    /**
     * Loads a question and checkpoint DMV, Dmv must have ID set
     * @param dmv dmv
     */
    void getQuestionCheckpointDmv(QuestionCheckpointDmv dmv);

    /**
     * Returns a list of questions for a dmv
     * @param dmv
     * @return list of questions
     */
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
