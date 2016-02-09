package lt.ltrp.dao.impl;

import lt.ltrp.constant.LtrpVehicleModel;
import lt.ltrp.dao.DmvDao;
import lt.ltrp.dmv.*;
import lt.ltrp.vehicle.FuelTank;
import lt.ltrp.vehicle.LtrpVehicle;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.object.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FileDmvDaoImpl implements DmvDao {

    private static final FileFilter dmvDataFileFilter = file -> file.getName().endsWith(".dat");
    private static final Logger logger = LoggerFactory.getLogger(FileDmvDaoImpl.class);

    private File dataDirectory;
    private int vehicleId = 1;
    private int checkpointId = 1;
    private int questionId = 1;
    private int answerId = 1;


    public FileDmvDaoImpl(File dataDir) {
        if(!dataDir.isDirectory()) {
            throw new IllegalArgumentException(dataDir.getName() + " must be a directory");
        } else {
            dataDirectory = new File(dataDir, "dmvs");
        }
    }


    @Override
    public void getQuestionDmv(QuestionDmv dmv) {
        File dmvDirectory = getDmvDirecotry(dmv);
        File[] dmvDataFiles = dmvDirectory.listFiles(dmvDataFileFilter);
        if (dmvDataFiles != null) {
            for (File dataFile : dmvDataFiles) {
                if (dataFile.getName().toLowerCase().startsWith("main")) {
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileReader(dataFile));
                        parseProperties(dmv, properties);
                    } catch (IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " data. Error: " + e.getMessage());
                    }
                } else if (dataFile.getName().toLowerCase().startsWith("vehicles")) {
                    try {
                        dmv.setVehicles(parseDmvVehicles(dataFile));
                    } catch (IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " vehicles. Error: " + e.getMessage());
                    }
                } else if(dataFile.getName().toLowerCase().startsWith("questions")) {
                    try {
                        dmv.setQuestions(parseDmvQuestions(dataFile));
                    } catch(IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " \"questions\". Error: " + e.getMessage());
                    }
                }
            }
        }
    }


    @Override
    public void getCheckpointDmv(CheckpointDmv dmv) {
        File dmvDirectory = getDmvDirecotry(dmv);
        // List all the files that are contained in the directory
        File[] dmvDataFiles = dmvDirectory.listFiles(dmvDataFileFilter);
        if (dmvDataFiles != null) {
            for (File dataFile : dmvDataFiles) {
                if (dataFile.getName().toLowerCase().startsWith("main")) {
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileReader(dataFile));
                        parseProperties(dmv, properties);
                    } catch (IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " data. Error: " + e.getMessage());
                    }
                } else if (dataFile.getName().toLowerCase().startsWith("vehicles")) {
                    try {
                        dmv.setVehicles(parseDmvVehicles(dataFile));
                    } catch (IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " vehicles. Error: " + e.getMessage());
                    }
                } else if(dataFile.getName().toLowerCase().startsWith("checkpoints")) {
                    try {
                        dmv.setCheckpoints(parseDmvCheckpoints(dataFile));
                    } catch(IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " checkpoints. Error: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void getQuestionCheckpointDmv(QuestionCheckpointDmv dmv) {
        File dmvDirectory = getDmvDirecotry(dmv);
        // List all the files that are contained in the directory
        File[] dmvDataFiles = dmvDirectory.listFiles(dmvDataFileFilter);
        if(dmvDataFiles != null) {
            for(File dataFile : dmvDataFiles) {
                if(dataFile.getName().toLowerCase().startsWith("main")) {
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileReader(dataFile));
                        parseProperties(dmv, properties);
                    } catch(IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " data. Error: " + e.getMessage());
                    }
                } else if(dataFile.getName().toLowerCase().startsWith("vehicles")) {
                    try {
                        dmv.setVehicles(parseDmvVehicles(dataFile));
                    } catch(IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " vehicles. Error: " + e.getMessage());
                    }
                } else if(dataFile.getName().toLowerCase().startsWith("checkpoints")) {
                    try {
                        dmv.setCheckpoints(parseDmvCheckpoints(dataFile));
                    } catch(IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " checkpoints. Error: " + e.getMessage());
                    }
                } else if(dataFile.getName().toLowerCase().startsWith("questions")) {
                    try {
                        dmv.setQuestions(parseDmvQuestions(dataFile));
                    } catch(IOException e) {
                        logger.error("Could not load dmv " + dmv.getId() + " questions. Error: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Returns a list of {@link lt.ltrp.dmv.Dmv} vehicles.
     * One line should represent one vehicle. Values must be separated by the character ','. Format is: model id, x, y, z, color1, color2
     * Lines starting with a semicolon are interpreted as comments.
     * @param file file to read the vehicles from
     * @return returns a list of vehicles associated with the specified DMV
     * @throws IOException
     */
    private List<LtrpVehicle> parseDmvVehicles(File file) throws IOException {
        List<LtrpVehicle> vehicles = new ArrayList<>();
        try (
                BufferedReader bf = new BufferedReader(new FileReader(file));
                ) {
            String line;
            while((line = bf.readLine()) != null) {
                if(line.startsWith(";")) {
                    continue;
                }
                String[] parts = line.split(",");
                Vehicle vehicle = null;
                try {
                    vehicle = Vehicle.create(Integer.parseInt(parts[0].trim()),
                            Float.parseFloat(parts[1].trim()),
                            Float.parseFloat(parts[2].trim()),
                            Float.parseFloat(parts[3].trim()),
                            Float.parseFloat(parts[4].trim()),
                            Integer.parseInt(parts[5].trim()),
                            Integer.parseInt(parts[6].trim()),
                            -1
                    );
                } catch(NumberFormatException ignored) {}
                if(vehicle != null) {
                    FuelTank fuelTank = new FuelTank(LtrpVehicleModel.getFuelTankSize(vehicle.getModelId()), LtrpVehicleModel.getFuelTankSize(vehicle.getModelId()));
                    vehicles.add(new LtrpVehicle(vehicleId++, vehicle, fuelTank));
                }
            }
        }
        return vehicles;
    }

    /**
     * Parses a list of DMV questions. Questions and answers must be in key-value format separated by "=". To mark an answer the correct one, add "true" anywhere to the key, if it is not present, answer will be marked as false one.
     * Question keys must be named "question", answer key names are chosen freely and are ignored but it still must be in key-value format
     * @param file file to read from
     * @return returns a {@link java.util.List} of {@link lt.ltrp.dmv.DmvQuestion}
     * @throws IOException
     */
    private List<DmvQuestion> parseDmvQuestions(File file) throws IOException {
        List<DmvQuestion> questions = new ArrayList<>();
        try (
                BufferedReader bf = new BufferedReader(new InputStreamReader (new FileInputStream(file), "UTF8"));
        ) {
            String line;
            DmvQuestion question = null;
            List<DmvQuestion.DmvAnswer> answers = new ArrayList<>();
            while((line = bf.readLine()) != null) {
                int index = line.indexOf("=");
                if(index != -1) {
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index).trim();
                    if(key.equalsIgnoreCase("question")) {
                        if(question != null) {
                            question.setAnswers(answers.toArray(new DmvQuestion.DmvAnswer[0]));
                            questions.clear();
                            questions.add(question);
                        }
                        question = new DmvQuestion();
                        question.setId(questionId++);
                        question.setQuestion(value);
                    } else {
                        boolean correct = false;
                        if(key.contains("true")) {
                            correct = true;
                        }
                        answers.add(question.new DmvAnswer(answerId++, value, correct));
                    }
                }
            }
            if(question != null) {
                question.setAnswers(answers.toArray(new DmvQuestion.DmvAnswer[0]));
                questions.clear();
                questions.add(question);
            }
        }
        return questions;
    }

    /**
     * Parses DMV checkpoints. Checkpoints consist of a location and radius. The format is: x, y, z, interior, virtual world, radius
     * @param file file to read from
     * @return returns a {@link java.util.List} of {@link lt.ltrp.dmv.DmvCheckpoint}
     * @throws IOException
     */
    private List<DmvCheckpoint> parseDmvCheckpoints(File file) throws IOException {
        List<DmvCheckpoint> checkpoints = new ArrayList<>();
        try (
                BufferedReader bf = new BufferedReader(new FileReader(file));
        ) {
            String line;
            while((line = bf.readLine()) != null) {
                String[] parts = line.split(",");
                DmvCheckpoint checkpoint = null;
                try {
                    checkpoint = new DmvCheckpoint(checkpointId++,
                            new Radius(
                                    Float.parseFloat(parts[0].trim()),
                                    Float.parseFloat(parts[1].trim()),
                                    Float.parseFloat(parts[2].trim()),
                                    Integer.parseInt(parts[3].trim()),
                                    Integer.parseInt(parts[4].trim()),
                                    Float.parseFloat(parts[5].trim())
                            ));
                } catch(NumberFormatException ignored) {}
                if(checkpoint != null) {
                    checkpoints.add(checkpoint);
                }
            }
        }
        return checkpoints;
    }

    /**
     * Parsed the main data for the specified dmv. The properties contain:
     * <ul>
     *     <li>Name</li>
     *     <li>Location*</li>
     * </ul>
     * * location can be specified in two formats: a line of values separated by commas(x, y, z, interior, virtual world) or separate key-value pairs.
     * @param dmv to load the data for
     * @param properties A loaded properties instance with dmv properties.
     */
    private void parseProperties(Dmv dmv, Properties properties) {
        if(properties.containsKey("name")) {
            dmv.setName(properties.getProperty("name"));
        }
        Location location = new Location();
        if(properties.containsKey("location")) {
            String[] parts = properties.getProperty("location").split(",");
            if(parts.length >= 3) {
                location.setX(Float.parseFloat(parts[0]));
                location.setY(Float.parseFloat(parts[1]));
                location.setZ(Float.parseFloat(parts[2]));
            }
            if(parts.length == 5) {
                location.setInteriorId(Integer.parseInt(parts[3]));
                location.setWorldId(Integer.parseInt(parts[4]));
            }
        } else {
            location.setX(Float.parseFloat(properties.getProperty("x", "0.0")));
            location.setY(Float.parseFloat(properties.getProperty("y", "0.0")));
            location.setZ(Float.parseFloat(properties.getProperty("z", "0.0")));
            location.setInteriorId(Integer.parseInt(properties.getProperty("interior", "0")));
            location.setWorldId(Integer.parseInt(properties.getProperty("virtual_world", "0")));
        }

    }


    @Override
    public List<DmvQuestion> getQuestions(Dmv dmv) {
        throw new NotImplementedException();
    }

    @Override
    public void insert(Dmv dmv, DmvQuestion question) {
        throw new NotImplementedException();
    }

    @Override
    public void update(DmvQuestion question) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(DmvQuestion question) {
        throw new NotImplementedException();
    }

    @Override
    public List<DmvCheckpoint> getCheckpoints(Dmv dmv) {
        throw new NotImplementedException();
    }

    @Override
    public void insert(Dmv dmv, DmvCheckpoint checkpoint) {
        throw new NotImplementedException();
    }

    @Override
    public void update(DmvCheckpoint checkpoint) {
        throw new NotImplementedException();
    }

    @Override
    public List<LtrpVehicle> getVehicles(Dmv dmv) {
        throw new NotImplementedException();
    }

    @Override
    public void insert(Dmv dmv, LtrpVehicle vehicle) {
        throw new NotImplementedException();
    }

    @Override
    public void update(LtrpVehicle vehicle) {
        throw new NotImplementedException();
    }


    /**
     * Generates a filename from the DMV
     * @param dmv dmv to be used
     * @return a string representation of the dmv filename
     */
    private String toFilename(Dmv dmv){
        return String.format("%d - %s.dat", dmv.getId(), dmv.getName());
    }

    /**
     * Extracts an id from a DMV directory's name
     * @param file file name to be used
     * @return dmv id or {@link lt.ltrp.dao.DmvDao#INVALID_ID} if folder name is in invalid format
     */
    private int idFromFilename(File file) {
        int index;
        try {
            if((index = file.getName().indexOf("-")) == -1) {
                return Integer.parseInt(file.getName());
            } else {
                return Integer.parseInt(file.getName().substring(0, index).trim());
            }
        } catch(NumberFormatException e) {
            return DmvDao.INVALID_ID;
        }
    }

    /**
     * Finds the DMV's data directory contained in main data directory {@link lt.ltrp.dao.impl.FileDmvDaoImpl#dataDirectory}
     * @param dmv dmv to be found
     * @return DMV's data directory
     */
    private File getDmvDirecotry(Dmv dmv) {
        File[] folders = dataDirectory.listFiles();
        if (folders != null) {
            for (File dmvFolder : folders) {
                int id = idFromFilename(dmvFolder);
                if (id == dmv.getId()) {
                    return dmvFolder;
                }
            }
        }
        return null;
    }

}