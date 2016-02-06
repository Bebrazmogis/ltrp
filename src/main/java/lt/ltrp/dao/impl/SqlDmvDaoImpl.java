package lt.ltrp.dao.impl;

import lt.ltrp.Util.Sql;
import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.dao.DmvDao;
import lt.ltrp.dmv.*;
import lt.ltrp.vehicle.FuelTank;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.constant.RaceCheckpointType;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.object.Checkpoint;
import net.gtaun.shoebill.object.RaceCheckpoint;
import net.gtaun.shoebill.object.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static lt.ltrp.dmv.DmvQuestion.*;

/**
 * @author Bebras
 *         2015.12.16.
 */
public class SqlDmvDaoImpl implements DmvDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlDmvDaoImpl.class);

    private DataSource dataSource;

    public SqlDmvDaoImpl(DataSource ds) {
        this.dataSource = ds;
    }




    private Dmv getDmv(int id) {
        String sql = "SELECT * FROM dmv WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                Dmv dmv = new DmvImpl(id, result.getString("name"),
                        new Location(result.getFloat("x"), result.getFloat("y"), result.getFloat("z"), result.getInt("interior"),
                                result.getInt("virtual_world")));
                dmv.setVehicles(getVehicles(dmv));
                return dmv;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void getQuestionDmv(QuestionDmv dmv) {

    }

    @Override
    public void getCheckpointDmv(CheckpointDmv dmv) {

    }

    @Override
    public void getQuestionCheckpointDmv(QuestionCheckpointDmv dmv) {

    }

    @Override
    public List<DmvQuestion> getQuestions(Dmv dmv) {
        try (
                Connection connection = dataSource.getConnection();
                ) {
            return getQuestions(dmv, connection);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<DmvQuestion> getQuestions(Dmv dmv, Connection connection) {
        String sql = "SELECT * FROM dmv_question WHERE dmv_id = ?";
        List<DmvQuestion> questions = new ArrayList<>();
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, dmv.getId());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                DmvQuestion question = new DmvQuestion();
                question.setId(result.getInt("id"));
                question.setQuestion(result.getString("question"));
                // Load all the answers for that question
                PreparedStatement answerStmt = connection.prepareStatement("SELECT * FROM dmv_question_answer WHERE question_id = ?");
                answerStmt.setInt(1, question.getId());
                ResultSet answers =answerStmt.executeQuery();
                List<DmvQuestion.DmvAnswer> answerList = new ArrayList<>();
                while(answers.next()) {
                    DmvQuestion.DmvAnswer answer = question. new DmvAnswer(answers.getInt("id"), answers.getString("answer"), answers.getBoolean("correct"));
                    answerList.add(answer);
                }
                question.setAnswers(answerList.toArray(new DmvQuestion.DmvAnswer[0]));
                questions.add(question);
                answerStmt.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    @Override
    public void insert(Dmv dmv, DmvQuestion question) {
        String questionSql = "INSERT INTO dmv_question (dmv_id, question) VALUES(?, ?)";
        String answerSql = "INSERT INTO dmv_question_answer (question_id, answer, correct) VALUES(?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement questionStmt = con.prepareStatement(questionSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement answerStmt = con.prepareStatement(answerSql, Statement.RETURN_GENERATED_KEYS);
                ) {
            questionStmt.setInt(1, dmv.getId());
            questionStmt.setString(2, question.getQuestion());
            questionStmt.execute();
            ResultSet keys = questionStmt.getGeneratedKeys();
            if(keys.next()) {
                int id = keys.getInt(1);
                question.setId(id);
                for(DmvQuestion.DmvAnswer answer : question.getAnswers()) {
                    answerStmt.setInt(1, id);
                    answerStmt.setString(2, answer.getAnswer());
                    answerStmt.setBoolean(3, answer.isCorrect());
                    answerStmt.execute();
                    keys = answerStmt.getGeneratedKeys();
                    if(keys.next()) {
                        answer.setId(keys.getInt(1));
                    }
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(DmvQuestion question) {
        String questionSql = "UPDATE dmv_question SET question = ? WHERE id = ?";
        String answerSql = "UPDATe dmv_question_answer SET quesetion_id = ?, answer = ?, correct = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement questinStmt = connection.prepareStatement(questionSql);
                PreparedStatement answerStmt = connection.prepareStatement(answerSql);
                ) {
            questinStmt.setString(1, question.getQuestion());
            questinStmt.setInt(2, question.getId());
            questinStmt.execute();
            for(DmvQuestion.DmvAnswer answer : question.getAnswers()) {
                answerStmt.setInt(1, question.getId());
                answerStmt.setString(2, answer.getAnswer());
                answerStmt.setBoolean(3, answer.isCorrect());
                answerStmt.setInt(4, answer.getId());
                answerStmt.execute();
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DmvQuestion question) {
        String questionSql = "DELETE FROM dmv_question WHERE id = ?";
        String answerSql = "DELETE FROM dmv_question_answer WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement questionStmt = con.prepareStatement(questionSql);
                PreparedStatement answerStmt = con.prepareStatement(answerSql);
                ) {
            questionStmt.setInt(1, question.getId());
            questionStmt.execute();
            for(DmvQuestion.DmvAnswer answer : question.getAnswers()) {
                answerStmt.setInt(1, answer.getId());
                answerStmt.execute();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public List<DmvCheckpoint> getCheckpoints(Dmv dmv) {
        try (
                Connection con = dataSource.getConnection();
                ) {
            return getCheckpoints(dmv, con);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<DmvCheckpoint> getCheckpoints(Dmv dmv, Connection con) throws SQLException {
        String sql = "SELECT * FROM dmv_checkpoint WHERE dmv_id = ?";
        try (
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, dmv.getId());
            List<DmvCheckpoint> checkpoints = new ArrayList<>();
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                DmvCheckpoint dmvCheckpoint = new DmvCheckpoint(result.getInt("id"), new Radius(result.getFloat("x"), result.getFloat("y"), result.getFloat("z"), result.getFloat("radius")));
                checkpoints.add(dmvCheckpoint);
            }

            return checkpoints;
        }
    }

    @Override
    public void insert(Dmv dmv, DmvCheckpoint checkpoint) {
        String sql = "INSERT INTO dmv_checkpoint (dmv_id, x, y, z, radius) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ) {
            stmt.setInt(1, dmv.getId());
            stmt.setFloat(2, checkpoint.getRadius().getX());
            stmt.setFloat(3, checkpoint.getRadius().getY());
            stmt.setFloat(4, checkpoint.getRadius().getZ());
            stmt.setFloat(5, checkpoint.getRadius().getRadius());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next()) {
                checkpoint.setId(keys.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(DmvCheckpoint checkpoint) {
        String sql = "UPDATE dmv_checkpoint SET x = ?, y = ?, z = ?, radius = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setFloat(1, checkpoint.getRadius().getX());
            stmt.setFloat(2, checkpoint.getRadius().getY());
            stmt.setFloat(3, checkpoint.getRadius().getZ());
            stmt.setFloat(4, checkpoint.getRadius().getRadius());
            stmt.setInt(5, checkpoint.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<LtrpVehicle> getVehicles(Dmv dmv) {
        String sql = "SELECT * FROM dmv_vehicle WHERE dmv_id = ?";
        List<LtrpVehicle> vehicles = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            stmt.setInt(1, dmv.getId());
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                int modelid = result.getInt("modelid");
                Vehicle vehicle = Vehicle.create(modelid, new AngledLocation(result.getFloat("x"), result.getFloat("y"), result.getFloat("z"), result.getFloat("angle")), result.getInt("color1"), result.getInt("color2"), -1, false);
                FuelTank fuelTank = new FuelTank(LtrpVehicleModel.getFuelTankSize(modelid), LtrpVehicleModel.getFuelTankSize(modelid));
                vehicles.add(new LtrpVehicle(result.getInt("id"), vehicle, fuelTank));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        logger.debug("Loaded " + vehicles.size() + " vehicles for dmv " + dmv.getId());
        return vehicles;
    }

    @Override
    public void insert(Dmv dmv, LtrpVehicle vehicle) {
        String sql = "INSERT INTO dmv_vehicle (dmv_id, modelid, x, y, z, angle, color1, color2) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, dmv.getId());
            stmt.setInt(2, vehicle.getModelId());
            stmt.setFloat(3, vehicle.getLocation().getX());
            stmt.setFloat(4, vehicle.getLocation().getY());
            stmt.setFloat(5, vehicle.getLocation().getZ());
            stmt.setFloat(6, vehicle.getLocation().getAngle());
            stmt.setInt(7, vehicle.getColor1());
            stmt.setInt(8, vehicle.getColor2());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next()) {
                vehicle.setId(keys.getInt(1));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(LtrpVehicle vehicle) {
        String sql = "UPDATE dmv_vehicle SET modelid = ?, x = ?, y = ?, z = ?, angle = ?, color1 = ?, color2 = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, vehicle.getModelId());
            stmt.setFloat(2, vehicle.getLocation().getX());
            stmt.setFloat(3, vehicle.getLocation().getY());
            stmt.setFloat(4, vehicle.getLocation().getZ());
            stmt.setFloat(5, vehicle.getLocation().getAngle());
            stmt.setInt(6, vehicle.getColor1());
            stmt.setInt(7, vehicle.getColor2());
            stmt.setInt(8, vehicle.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
