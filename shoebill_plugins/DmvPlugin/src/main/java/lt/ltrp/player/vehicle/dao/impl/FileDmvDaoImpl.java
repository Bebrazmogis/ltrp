package lt.ltrp.player.vehicle.dao.impl;

import lt.ltrp.LoadingException;
import lt.ltrp.aircraft.AircraftDmvImpl;
import lt.ltrp.boat.BoatDmvImpl;
import lt.ltrp.car.CarDmvImpl;
import lt.ltrp.dao.DmvDao;
import lt.ltrp.data.dmv.DmvCheckpoint;
import lt.ltrp.data.dmv.DmvQuestion;
import lt.ltrp.data.dmv.DmvRaceCheckpoint;
import net.gtaun.shoebill.constant.RaceCheckpointType;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FileDmvDaoImpl implements DmvDao {

    private static final FileFilter dmvDataFileFilter = file -> file.getName().endsWith(".dat");
    private static final Logger logger = LoggerFactory.getLogger(FileDmvDaoImpl.class);

    private EventManager eventManager;
    private File dataDirectory;
    private int vehicleId = 1;
    private int checkpointId = 1;
    private int questionId = 1;
    private int answerId = 1;


    public FileDmvDaoImpl(File dataDir, EventManager eventManager) {
        if(!dataDir.isDirectory()) {
            throw new IllegalArgumentException(dataDir.getName() + " must be a directory");
        } else {
            dataDirectory = new File(dataDir, "dmvs");
        }
        this.eventManager = eventManager;
    }


    @Override
    public CarDmv getCarDmv(int id) throws LoadingException {
        CarDmvImpl carDmv = new CarDmvImpl(id);
        File dmvDirectory = getDmvDirecotry(carDmv);
        if(dmvDirectory == null) {
            throw new LoadingException("No data directory found for dmv " + id);
        }

        // List all the files that are contained in the directory
        File[] dmvDataFiles = dmvDirectory.listFiles(dmvDataFileFilter);
        if(dmvDataFiles == null) {
            throw new LoadingException("No data files found for dmv " + id);
        }

        try {
            for(File dataFile : dmvDataFiles) {
                if(dataFile.getName().toLowerCase().startsWith("main")) {
                    Properties properties = new Properties();
                    properties.load(new FileReader(dataFile));
                    parseProperties(carDmv, properties);
                    carDmv.setQuestionTestPrice(Integer.parseInt(properties.getProperty("question_test_price")));
                    carDmv.setDrivingTestPrice(Integer.parseInt(properties.getProperty("driving_test_price")));
                } else if(dataFile.getName().toLowerCase().startsWith("vehicles")) {
                    carDmv.setVehicles(parseDmvVehicles(carDmv, dataFile));
                } else if(dataFile.getName().toLowerCase().startsWith("checkpoints")) {
                    carDmv.setCheckpoints(parseDmvCheckpoints(dataFile));
                } else if(dataFile.getName().toLowerCase().startsWith("questions")) {
                    carDmv.setQuestions(parseDmvQuestions(dataFile));
                }
            }
        } catch(Exception e) {
            throw new LoadingException(e);
        }
        return carDmv;
    }

    @Override
    public BoatDmv getBoatDmv(int id) throws LoadingException {
        BoatDmvImpl boatDmv = new BoatDmvImpl(id);
        File dmvDirectory = getDmvDirecotry(boatDmv);
        if(dmvDirectory == null) {
            throw new LoadingException("No data directory found for dmv " + id);
        }

        // List all the files that are contained in the directory
        File[] dmvDataFiles = dmvDirectory.listFiles(dmvDataFileFilter);
        if(dmvDataFiles == null) {
            throw new LoadingException("No data files found for dmv " + id);
        }

        try {
            for(File dataFile : dmvDataFiles) {
                if(dataFile.getName().toLowerCase().startsWith("main")) {
                    Properties properties = new Properties();
                    properties.load(new FileReader(dataFile));
                    parseProperties(boatDmv, properties);
                    boatDmv.setCheckpointTestPrice(Integer.parseInt(properties.getProperty("test_price")));
                } else if(dataFile.getName().toLowerCase().startsWith("vehicles")) {
                    boatDmv.setVehicles(parseDmvVehicles(boatDmv, dataFile));
                } else if(dataFile.getName().toLowerCase().startsWith("checkpoints")) {
                    boatDmv.setCheckpoints(parseDmvCheckpoints(dataFile));
                }
            }
        } catch(Exception e) {
            throw new LoadingException(e);
        }
        return boatDmv;
    }

    @Override
    public AircraftDmv getAircraftDmv(int id) throws LoadingException {
        AircraftDmvImpl aircraftDmv = new AircraftDmvImpl(id);
        File dmvDirectory = getDmvDirecotry(aircraftDmv);
        if(dmvDirectory == null) {
            throw new LoadingException("No data directory found for dmv " + id);
        }

        // List all the files that are contained in the directory
        File[] dmvDataFiles = dmvDirectory.listFiles(dmvDataFileFilter);
        if(dmvDataFiles == null) {
            throw new LoadingException("No data files found for dmv " + id);
        }

        try {
            for(File dataFile : dmvDataFiles) {
                if(dataFile.getName().toLowerCase().startsWith("main")) {
                    Properties properties = new Properties();
                    properties.load(new FileReader(dataFile));
                    parseProperties(aircraftDmv, properties);
                    if(properties.getProperty("test_price") != null) {
                        aircraftDmv.setCheckpointTestPrice(Integer.parseInt(properties.getProperty("test_price")));
                    } else
                        throw new LoadingException("Property test_price not found in " + properties.toString());
                } else if(dataFile.getName().toLowerCase().startsWith("vehicles")) {
                    aircraftDmv.setVehicles(parseDmvVehicles(aircraftDmv, dataFile));
                } else if(dataFile.getName().toLowerCase().startsWith("checkpoints")) {
                    aircraftDmv.setCheckpoints(parseDmvCheckpoints(dataFile));
                }
            }
        } catch(Exception e) {
            throw new LoadingException(e);
        }
        return aircraftDmv;
    }

    /**
     * Returns a list of {@link lt.ltrp.object.Dmv} vehicles.
     * One line should represent one vehicle. Values must be separated by the character ','. Format is: model id, x, y, z, color1, color2
     * Lines starting with a semicolon are interpreted as comments.
     * @param dmv dmv
     * @param file file to read the vehicles from
     * @return returns a list of vehicles associated with the specified DMV
     * @throws java.io.IOException
     */
    private List<DmvVehicle> parseDmvVehicles(Dmv dmv, File file) throws IOException {
        List<DmvVehicle> vehicles = new ArrayList<>();
        try (
                BufferedReader bf = new BufferedReader(new FileReader(file));
                ) {
            java.lang.String line;
            while((line = bf.readLine()) != null) {
                if(line.startsWith(";")) {
                    continue;
                }
                String[] parts = line.split(",");
                Vehicle vehicle = null;
                int modelId = 0;
                AngledLocation location = null;
                int color1 = 0, color2 = 0;
                try {
                    modelId = Integer.parseInt(parts[0].trim());
                    location = new AngledLocation( Float.parseFloat(parts[1].trim()),
                            Float.parseFloat(parts[2].trim()),
                            Float.parseFloat(parts[3].trim()),
                            Float.parseFloat(parts[4].trim()));
                    color1 = Integer.parseInt(parts[5].trim());
                    color2 = Integer.parseInt(parts[6].trim());

                } catch(NumberFormatException ignored) {}
                if(vehicle != null) {
                   // FuelTank fuelTank = new FuelTank(LtrpVehicleModel.getFuelTankSize(vehicle.getModelId()), LtrpVehicleModel.getFuelTankSize(vehicle.getModelId()));
                    vehicles.add(DmvVehicle.create(dmv, vehicleId++, modelId, location, color1, color2, 0f, eventManager));
                    //vehicles.add(DmvVehicle.create(dmv, vehicleId++, vehicle, fuelTank));
                }
            }
        }
        return vehicles;
    }

    /**
     * Parses a list of DMV questions. Questions and answers must be in key-value format separated by "=". To mark an answer the correct one, add "true" anywhere to the key, if it is not present, answer will be marked as false one.
     * Question keys must be named "question", answer key names are chosen freely and are ignored but it still must be in key-value format
     * @param file file to read from
     * @return returns a {@link java.util.List} of {@link lt.ltrp.data.dmv.DmvQuestion}
     * @throws IOException
     */
    private List<DmvQuestion> parseDmvQuestions(File file) throws IOException {
        List<DmvQuestion> questions = new ArrayList<>();
        try (
                BufferedReader bf = new BufferedReader(new InputStreamReader (new FileInputStream(file), "cp1257"));
        ) {
            String line;
            DmvQuestion question = null;
            List<DmvQuestion.DmvAnswer> answers = new ArrayList<>();
            while((line = bf.readLine()) != null) {
                int index = line.indexOf("=");
                if(index != -1) {
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index+1).trim();
                    if(key.equalsIgnoreCase("question")) {
                        if(question != null) {
                            question.setAnswers(answers.toArray(new DmvQuestion.DmvAnswer[0]));
                            answers.clear();
                            questions.add(question);
                        }
                        question = new DmvQuestion();
                        question.setId(questionId++);
                        question.setQuestion(value);
                    } else {
                        boolean correct = key.contains("true");
                        answers.add(question.new DmvAnswer(answerId++, value, correct));
                    }
                }
            }
            if(question != null) {
                question.setAnswers(answers.toArray(new DmvQuestion.DmvAnswer[0]));
                questions.add(question);
            }
        }
        return questions;
    }

    private DmvRaceCheckpoint parseRaceCheckpoints(List<String> data, List<DmvRaceCheckpoint> output, int index) {
        DmvRaceCheckpoint cp = null;
        if(index < data.size()) {
            String[] parts = data.get(index).split(";");
            if(parts.length >= 7) {
                cp = new DmvRaceCheckpoint(checkpointId++,
                        new Radius(
                                Float.parseFloat(parts[0].trim()),
                                Float.parseFloat(parts[1].trim()),
                                Float.parseFloat(parts[2].trim()),
                                Integer.parseInt(parts[3].trim()),
                                Integer.parseInt(parts[4].trim()),
                                Float.parseFloat(parts[5].trim())
                        ),
                        RaceCheckpointType.get(Integer.parseInt(parts[5])),
                        parseRaceCheckpoints(data, output, index + 1)
                );
                output.add(cp);
            }
        }
        return cp;
    }

    private DmvCheckpoint[] parseCheckpoints(List<String> data) {
        List<DmvCheckpoint> checkpoints = new ArrayList<>();
        for(String line : data) {
            String[] parts = line.split(";");
            DmvCheckpoint dmvCheckpoint = null;
            try {
                if(parts.length == 6) {
                    dmvCheckpoint = new DmvCheckpoint(checkpointId++,
                            new Radius(
                                    Float.parseFloat(parts[0].trim()),
                                    Float.parseFloat(parts[1].trim()),
                                    Float.parseFloat(parts[2].trim()),
                                    Integer.parseInt(parts[3].trim()),
                                    Integer.parseInt(parts[4].trim()),
                                    Float.parseFloat(parts[5].trim())
                            )
                    );
                }
            } catch(NumberFormatException ignored) {}
            if(dmvCheckpoint != null) {
                checkpoints.add(dmvCheckpoint);
            }
        }
        return checkpoints.toArray(new DmvCheckpoint[0]);
    }

    /**
     * Parses DMV checkpoints. Checkpoints consist of a location and radius. The format is: x, y, z, interior, virtual world, radius
     * @param file file to read from
     * @return returns a {@link java.util.List} of {@link lt.ltrp.data.dmv.DmvCheckpoint}
     * @throws IOException
     */
    private DmvCheckpoint[] parseDmvCheckpoints(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        if(lines.size() > 0) {
            String[] parts = lines.get(0).split(";");
            if(parts.length == 5) {
                return parseCheckpoints(lines);
            } else if(parts.length >= 7) {
                List<DmvRaceCheckpoint> checkpoints = new ArrayList<>();
                parseRaceCheckpoints(lines, checkpoints, 0);
                return checkpoints.toArray(new DmvCheckpoint[0]);
            }
        }
        return new DmvCheckpoint[0];
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
            dmv.setName(properties.getProperty("name", dmv.toString()));
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
        dmv.setLocation(location);
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
                System.out.println("Index == -1. Filename: "+ file.getName());
                return Integer.parseInt(file.getName());
            } else {
                return Integer.parseInt(file.getName().substring(0, index).trim());
            }
        } catch(NumberFormatException e) {
            return DmvDao.INVALID_ID;
        }
    }

    /**
     * Finds the DMV's data directory contained in main data directory {@link FileDmvDaoImpl#dataDirectory}
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