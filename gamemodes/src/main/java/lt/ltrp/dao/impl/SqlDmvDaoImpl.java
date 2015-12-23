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

    @Override
    public List<Dmv> getDmvs() {
        List<Dmv> dmvs = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                Statement stmt = con.createStatement()
                ) {
            ResultSet result = stmt.executeQuery("SELECT * FROM dmv");
            while(result.next()) {
                String type = result.getString("type");
                Location location = new Location(result.getFloat("x"), result.getFloat("y"), result.getFloat("z"), result.getInt("interior"), result.getInt("virtual_world"));
                Dmv dmv;
                switch(type) {
                    case "DmvCar":
                        DmvCar car = new DmvCar(result.getString("name"));
                        car.setId(result.getInt("id"));
                        for(DmvQuestion question : getQuestions(car, con)) {
                            car.addQuestion(question);
                        }
                        dmv = car;
                        break;
                    case "DmvAircraft":
                        dmv = new DmvAircraft();
                        break;
                    case "DmvBoat":
                        dmv = new DmvBoat();
                        break;
                    default:
                        continue;
                }
                dmv.setLocation(location);
                dmv.setId(result.getInt("id"));
                for(DmvCheckpoint cp : getCheckpoints(dmv, con)) {
                    dmv.addCheckpoint(cp);
                }
                dmv.setVehicles(getVehicles(dmv));
                dmvs.add(dmv);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return dmvs;
    }

    @Override
    public void update(Dmv dmv) {
        String sql = "UPDATE dmv SET `name` = ?, type = ?, x = ?, y = ?, z = ?, interior = ?, virtual_world = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
                ) {
            stmt.setString(1, dmv.getName());
            stmt.setString(2, dmv.getClass().getTypeName());
            stmt.setFloat(3, dmv.getLocation().getX());
            stmt.setFloat(4, dmv.getLocation().getY());
            stmt.setFloat(5, dmv.getLocation().getZ());
            stmt.setInt(6, dmv.getLocation().getInteriorId());
            stmt.setInt(7, dmv.getLocation().getWorldId());
            stmt.setInt(8, dmv.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Dmv dmv) {
        String sql = "DELETE FROM dmv WHERE id = ? LIMIT 1";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)
                ) {
            stmt.setInt(1, dmv.getId());
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
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
            List<Integer> ids = new ArrayList<>();
            List<RaceCheckpointType> types = new ArrayList<>();
            List<Radius> radiuses = new ArrayList<>();
            while(result.next()) {
                String type = result.getString("type");
                Checkpoint cp;
                // we can simply load and add simple checkpoints
                if(type.equals("simple")) {
                    DmvCheckpoint checkpoint = new DmvCheckpoint();
                    checkpoint.setId(result.getInt("id"));
                    cp = Checkpoint.create(new Radius(result.getFloat("x"), result.getFloat("y"), result.getFloat("z"), result.getFloat("radius")), null, null);
                    checkpoint.setCheckpoint(cp);
                    dmv.addCheckpoint(checkpoint);
                // As for Race checkpoints, we need to get all their data first because they rely on each other
                } else {
                    ids.add(result.getInt("id"));
                    types.add(RaceCheckpointType.valueOf(result.getString("type")));
                    radiuses.add(new Radius(result.getFloat("x"), result.getFloat("y"), result.getFloat("z"), result.getFloat("radius")));
                }
            }

           if(ids.size() > 0) {
               // now that all the data is read we can set up race checkpoints
               RaceCheckpoint cp = null;
               for(int i = ids.size() - 1; i >= 0; i--) {
                   if(cp == null) {
                       cp = RaceCheckpoint.create(radiuses.get(i), types.get(i), () -> null, null, null);
                   } else {
                       cp = RaceCheckpoint.create(radiuses.get(i), types.get(i), cp, null, null);
                       DmvCheckpoint dmvCheckpoint = new DmvCheckpoint(ids.get(i), cp);
                       dmv.addCheckpoint(dmvCheckpoint);
                   }
               }
           }
            return checkpoints;
        }
    }

    @Override
    public void insert(Dmv dmv, DmvCheckpoint checkpoint) {
        String sql = "INSERT INTO dmv_checkpoint (dmv_id, type, x, y, z, radius) VALUES (?, ?, ?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ) {
            String type;
            if ((checkpoint.getCheckpoint() instanceof RaceCheckpoint)) {
                type = ((RaceCheckpoint) checkpoint.getCheckpoint()).getType().name();
            } else {
                type = "simple";
            }
            stmt.setInt(1, dmv.getId());
            stmt.setString(2, type);
            stmt.setFloat(3, checkpoint.getCheckpoint().getLocation().getX());
            stmt.setFloat(4, checkpoint.getCheckpoint().getLocation().getY());
            stmt.setFloat(5, checkpoint.getCheckpoint().getLocation().getZ());
            stmt.setFloat(3, checkpoint.getCheckpoint().getLocation().getRadius());
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
        String sql = "UPDATE dmv_checkpoint SET type = ?, x = ?, y = ?, z = ?, radius = ? WHERE id = ?";
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ) {
            String type;
            if ((checkpoint.getCheckpoint() instanceof RaceCheckpoint)) {
                type = ((RaceCheckpoint) checkpoint.getCheckpoint()).getType().name();
            } else {
                type = "simple";
            }
            stmt.setString(1, type);
            stmt.setFloat(2, checkpoint.getCheckpoint().getLocation().getX());
            stmt.setFloat(3, checkpoint.getCheckpoint().getLocation().getY());
            stmt.setFloat(4, checkpoint.getCheckpoint().getLocation().getZ());
            stmt.setFloat(5, checkpoint.getCheckpoint().getLocation().getRadius());
            stmt.setInt(6, checkpoint.getId());
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
        String sql = "UPDATE dmv_vehicle SET modelid = ?, x = ?, y = ?, z = ?, angle = ?, color1 = ?, color2 = ? WHERRE id  ?";
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
