package lt.ltrp.dao.impl;

/**
 * @author Bebras
 *         2015.12.06.
 */

/*

                                                    directory
                                                        ?
                                                        ?
                                ??????????????????????????????????????
                                |                                                               |
         ?????????faction directory????????                   ??????????job directory???????????
         |                      |                     |                   |                     |                         |
         |                      |                     |                   |                     |                         |
         |                      |                     |                   |                     |                         |
     faction_1               faction_2              faction_n          job_1                   job_2                    job_n
??????????     ??????????      ??????????  ??????????      ??????????        ??????????
main vehicles ranks  main vehicles ranks   main vehicles ranks  main vehicles ranks   main vehicles  ranks      main  vehicles  ranks



* Faction main file format:
* id = int
* name = "name"
* x = float
* y = float
* z = float
* interior = int
* virtual_world = int
* leaderid = int
*
* Faction rank file format
* number = int
* name = "name"
* */
/*
    @Deprecated
 public class FileJobDaoImpl implements JobDao {

    private static final Logger logger = LoggerFactory.getLogger(FileJobDaoImpl.class);

    private static final Map<Job, File> jobToDirectory = new HashMap<>();

    private static final Pattern sectionTagPattern = Pattern.compile("\\[.+\\]");

    private File directory;
    private File factionDirectory;
    private File contractJobDirectory;
    private File trashMissionDirectory;



    private static final FilenameFilter dataFileFilter = (file, name) -> {
        return name.endsWith(".dat");
    };

    public FileJobDaoImpl(File directory) {
        if(!directory.isDirectory()) {
            throw new IllegalArgumentException(directory.getName() + " is not a directory");
        }
        this.directory = directory;
        this.factionDirectory = new File(directory + File.separator + "factions");
        if(!this.factionDirectory.exists()) {
            this.factionDirectory.mkdirs();
        }
        this.contractJobDirectory = new File(directory + File.separator + "contract_jobs");
        if(!this.contractJobDirectory.exists()) {
            this.contractJobDirectory.mkdirs();
        }

        this.trashMissionDirectory = new File(directory, "trashmissions");
        if(!trashMissionDirectory.exists()) {
            trashMissionDirectory.mkdirs();
        }
    }

    @Override
    public List<Job> get() {
        return null;
    }

    @Override
    public List<Faction> getFactions() {
        List<Faction> factions = new ArrayList<>();
        File[] factionDirectories = factionDirectory.listFiles();
        if(factionDirectories != null) {
            for(File factionDir : factionDirectories) {
                if(factionDir.isDirectory()) {
                    Faction faction = new Faction();
                    File[] dataFiles = factionDir.listFiles(dataFileFilter);
                    for(File dataFile : dataFiles) {
                        if(dataFile.getName().equals("main")) {
                            try {
                                parseFactionData(faction, dataFile);
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        } else if(dataFile.getName().equals("ranks")) {
                            try {
                                parseFactionRanks(faction, dataFile);
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        } else if(dataFile.getName().equals("vehicles")) {
                            try {
                                parseFactionVehicles(faction, dataFile);
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                        factions.add(faction);
                        jobToDirectory.put(faction, dataFile);
                    }
                }
            }
        }
        return factions;
    }

    @Override
    public VehicleThiefJob getVehicleThiefJob(int id) {
        File[] jobDirectories = contractJobDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(String.format("%d-", id));
            }
        });
        if(jobDirectories.length > 0) {
            File jobDirectory = jobDirectories[0];
            try {
                Properties properties = new Properties();
                properties.load(new FileReader(new File(jobDirectory, "main.dat")));
                VehicleThiefJob job = new VehicleThiefJob();
                parseContractJobdata(job, properties);
                job.setRequiredModelCount(Integer.parseInt(properties.getProperty("required_model_count", "0")));

                try (
                        BufferedReader bf = new BufferedReader(new FileReader(new File(jobDirectory, "spots.dat")));
                        ) {
                    String line;
                    NamedLocation location = new NamedLocation();
                    while((line = bf.readLine()) != null) {
                        String section = null;
                        if(sectionTagPattern.matcher(line).find()) {
                            section = sectionTagPattern.matcher(line).group();
                            location.setName(section);
                            job.addBuyPoint(location);
                        }
                        if(line.contains("=")) {
                            String key = line.substring(0, line.indexOf("=")).trim();
                            String value = line.substring(line.indexOf("=")+1).trim();
                            if(key.equalsIgnoreCase("x")) {
                                location.setX(Float.parseFloat(value));
                            } else if(key.equalsIgnoreCase("y")) {
                                location.setY(Float.parseFloat(value));
                            } else if(key.equalsIgnoreCase("z")) {
                                location.setZ(Float.parseFloat(value));
                            } else {
                                logger.error("Unknown key in job " + job.getName() + " spot data section " + section + " key " + key);
                            }
                        }
                    }
                }

                return job;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void parseFactionData(Faction faction, File file) throws IOException, NumberFormatException {
        Properties p = new Properties();
        p.load(new FileReader(file));
        if(!p.isEmpty()) {
            faction.setId(Integer.parseInt(p.getProperty("id", "0")));
            faction.setName(p.getProperty("name", "none"));
            Location location = new Location();
            location.setX(Float.parseFloat(p.getProperty("x", "0.0")));
            location.setY(Float.parseFloat(p.getProperty("y", "0.0")));
            location.setZ(Float.parseFloat(p.getProperty("z", "0.0")));
            location.setInteriorId(Integer.parseInt(p.getProperty("interior", "0")));
            location.setInteriorId(Integer.parseInt(p.getProperty("virtual_world", "0")));
            faction.setLocation(location);
            faction.setLeaderId(Integer.parseInt(p.getProperty("leaderid", "0")));
        }
    }

    private void parseFactionRanks(Faction faction, File file) throws IOException {
        List<FactionRank> factionRanks = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String line;
        while((line = bf.readLine()) != null) {
            if(line.contains("=")) {
                int number = Integer.parseInt(line.substring(0, line.indexOf("=")).trim());
                FactionRank rank = new FactionRank(number, line.substring(line.indexOf("=")+1).trim());
                factionRanks.add(rank);
            }
        }
        faction.setRanks(factionRanks);
    }


    @Override
    public List<ContractJob> getContractJobs() {
        List<ContractJob> jobs = new ArrayList<>();
        File[] jobDirectories = contractJobDirectory.listFiles();
        if(jobDirectories != null) {
            for(File jobDir : jobDirectories) {
                if(jobDir.isDirectory()) {
                    ContractJob job = new ContractJob();
                    File[] dataFiles = jobDir.listFiles(dataFileFilter);
                    for(File dataFile : dataFiles) {
                        if(dataFile.getName().startsWith("main")) {
                            try {
                                parseContractJobData(job, dataFile);
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        } else if(dataFile.getName().startsWith("ranks")) {
                            try {
                                parseContractJobRanks(job, dataFile);
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        } else if(dataFile.getName().startsWith("vehicles")) {
                            try {
                                parseFactionVehicles(job, dataFile);
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                        jobs.add(job);
                        jobToDirectory.put(job, dataFile);
                    }
                }
            }
        }
        return jobs;
    }

    private void parseContractJobData(ContractJob job, File file) throws IOException, NumberFormatException {
        Properties p = new Properties();
        p.load(new FileReader(file));
        this.parseContractJobdata(job, p);
    }

    private void parseContractJobdata(ContractJob job, Properties p) {
        if(!p.isEmpty()) {
            job.setId(Integer.parseInt(p.getProperty("id", "0")));
            job.setName(p.getProperty("name", "none"));
            Location location = new Location();
            location.setX(Float.parseFloat(p.getProperty("x", "0.0")));
            location.setY(Float.parseFloat(p.getProperty("y", "0.0")));
            location.setZ(Float.parseFloat(p.getProperty("z", "0.0")));
            location.setInteriorId(Integer.parseInt(p.getProperty("interior", "0")));
            location.setInteriorId(Integer.parseInt(p.getProperty("virtual_world", "0")));
            job.setContractLength(Integer.parseInt(p.getProperty("contract_length", "0")));
            job.setMinPaycheck(Integer.parseInt(p.getProperty("min_paycheck", "0")));
            job.setMaxPaycheck(Integer.parseInt(p.getProperty("max_paycheck", "0")));
            job.setLocation(location);
        }
    }

    private void parseContractJobRanks(ContractJob job, File file) throws IOException {
        List<ContractJobRank> jobRanks = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String line;
        int number = 0, hoursNeeded = -1;
        String name = null;
        while((line = bf.readLine()) != null) {
            String section = null;
            if(sectionTagPattern.matcher(line).find()) {
                section = sectionTagPattern.matcher(line).group();
                if(number == 0 || hoursNeeded == -1 || name == null) {
                    logger.error(String.format("Value not found for section %s. Number %d hoursNeeded:%d name:%s", section, number, hoursNeeded, name));
                }
                jobRanks.add(new ContractJobRank(number, hoursNeeded, name));
                number = 0;
                hoursNeeded = -1;
                name = null;
            }
            if(line.contains("=")) {
                String key = line.substring(0, line.indexOf("=")).trim();
                String value = line.substring(line.indexOf("=")+1).trim();
                if(key.equalsIgnoreCase("number")) {
                    number = Integer.parseInt(value);
                } else if(key.equalsIgnoreCase("name")) {
                    name = value;
                } else if(key.equalsIgnoreCase("hours_needed")) {
                    hoursNeeded = Integer.parseInt(value);
                } else {
                    logger.error("Unknown key in job " + job.getName() + " rank data section " + section + " key " + key);
                }
            }
        }
        job.setRanks(jobRanks);
    }

    private Map<Rank, JobVehicle> parseFactionVehicles(Job job, File file) throws IOException {
        Map<Rank, JobVehicle> jobVehicles = new HashMap<>();
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String line;
        int id = 0, model = 0, rankid = 0, color1 = 0, color2 = 0;
        AngledLocation location = new AngledLocation();

        while((line = bf.readLine()) != null) {
            Matcher matcher = sectionTagPattern.matcher(line);
            String section = null;
            if(matcher.find()) {
                section = matcher.group();
                if(model > 0) {
                    Rank rank = job.getRank(rankid);
                    JobVehicle jobVehicle = JobVehicle.create(id, job, model, location, color1, color2, rank);
                    jobVehicles.put(rank, jobVehicle);
                }
            }
            if(line.contains("=")) {
                String key = line.substring(0, line.indexOf("=")).trim().toLowerCase();
                String value = line.substring(line.indexOf("=")+1).trim();
                switch(key) {
                    case "id":
                        id = Integer.parseInt(value);
                        break;
                    case "model":
                        model = Integer.parseInt(value);
                        break;
                    case "x":
                        location.setX(Float.parseFloat(value));
                        break;
                    case "y":
                        location.setY(Float.parseFloat(value));
                        break;
                    case "z":
                        location.setZ(Float.parseFloat(value));
                        break;
                    case "angle":
                        location.setAngle(Float.parseFloat(value));
                        break;
                    case "x,y,z,angle":
                        break;
                    case "rankid":
                        rankid = Integer.parseInt(value);
                        break;
                    case "color1":
                        color1 = Integer.parseInt(value);
                        break;
                    case "color2":
                        color2 = Integer.parseInt(value);
                        break;
                    default:
                        logger.error("Invalid vehicle data key " + key + " in file " + file.getName() + " in section " + section);
                        break;

                }
            }
        }
        return jobVehicles;
    }


    @Override
    public void update(ContractJob job) {
        File jobDirectory = new File(contractJobDirectory, toFilename(job));
        if(!jobDirectory.exists()) {
            if(!jobDirectory.mkdirs())
                logger.error("Could not create directory " + jobDirectory + " for job " + job.getId());
        }
        writeContractJobData(job, new File(jobDirectory, "main.dat"));
        writeContractJobRankData(job.getRanks(), new File(jobDirectory, "ranks.dat"));
        writeJobVehicleData(job.getVehicles(), new File(jobDirectory, "vehicles.dat"));
    }

    @Override
    public void update(Faction faction) {
        File factionDirectory = new File(this.factionDirectory, toFilename(faction));
        if(!factionDirectory.exists()) {
            if(!factionDirectory.mkdirs())
                logger.error("Could not create directory " + factionDirectory + " for job " + faction.getId());
        }
        writeFactionData(faction, new File(factionDirectory, "main.dat"));
        writeFactionRankData(faction.getRanks(), new File(factionDirectory, "ranks.dat"));
        writeJobVehicleData(faction.getVehicles(), new File(factionDirectory, "vehicles.dat"));
    }

    private void writeFactionData(Faction faction, File file) {
        Properties properties = new Properties();
        properties.setProperty("id", Integer.toString(faction.getId()));
        properties.setProperty("name", faction.getName());
        properties.setProperty("x", Float.toString(faction.getLocation().getX()));
        properties.setProperty("y", Float.toString(faction.getLocation().getY()));
        properties.setProperty("z", Float.toString(faction.getLocation().getZ()));
        properties.setProperty("interior", Integer.toString(faction.getLocation().getInteriorId()));
        properties.setProperty("virtual_world", Integer.toString(faction.getLocation().getWorldId()));
        properties.setProperty("leaderid", Integer.toString( faction.getLeaderId()));
        try {
            properties.store(new FileWriter(file), "data");
        } catch(IOException e) {
            logger.error("Could not store properties for faction " + faction.getId() + " to " + file.getName() + " Error: " + e.getMessage());
        }
    }

    private void writeFactionRankData(List<FactionRank> ranks, File file) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            for(FactionRank rank : ranks) {
                writer.println(String.format("[%s-%d]", rank.getName(), rank.getNumber()));
                writer.println("number = " + rank.getNumber());
                writer.println("name = " + rank.getName());
            }
        } catch(IOException e) {
            logger.error("Could not write contract job rank data to " + file.getName() + "Error:" + e.getMessage());
        } finally {
            if(writer != null) {
                writer.close();
            }
        }
    }

    private void writeContractJobRankData(List<ContractJobRank> ranks, File file) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            for(ContractJobRank rank : ranks) {
                writer.println(String.format("[%s-%d]", rank.getName(), rank.getNumber()));
                writer.println("number = " + rank.getNumber());
                writer.println("name = " + rank.getName());
                writer.println("hours_needed = " + rank.getHoursNeeded());
            }
        } catch(IOException e) {
            logger.error("Could not write contract job rank data to " + file.getName() + "Error:" + e.getMessage());
        } finally {
            if(writer != null) {
                writer.close();
            }
        }

    }

    private void writeContractJobData(ContractJob job, File file)  {
        Properties properties = new Properties();
        properties.setProperty("id", Integer.toString(job.getId()));
        properties.setProperty("name", job.getName());
        properties.setProperty("x", Float.toString(job.getLocation().getX()));
        properties.setProperty("y", Float.toString(job.getLocation().getY()));
        properties.setProperty("z", Float.toString(job.getLocation().getZ()));
        properties.setProperty("interior", Integer.toString(job.getLocation().getInteriorId()));
        properties.setProperty("virtual_world", Integer.toString(job.getLocation().getWorldId()));
        properties.setProperty("contract_length", Integer.toString(job.getContractLength()));
        properties.setProperty("min_paycheck", Integer.toString(job.getMinPaycheck()));
        properties.setProperty("max_paycheck", Integer.toString(job.getMaxPaycheck()));
        try {
            properties.store(new FileWriter(file), "data");
        } catch(IOException e) {
            logger.error("Could not save contract job data to " + file.getName() + " Error:" + e.getMessage());
        }
    }


    private void writeJobVehicleData(Map<? extends Rank, JobVehicle> vehicles, File file) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            for(Rank rank : vehicles.keySet()) {
                JobVehicle veh = vehicles.get(rank);
                writer.println(String.format("[%s]", veh.getModelName()));
                writer.println("id = " + veh.getId());
                writer.println("model = " + veh.getModelId());
                writer.println("x = " + veh.getLocation().getX());
                writer.println("y = " + veh.getLocation().getY());
                writer.println("z = " + veh.getLocation().getZ());
                writer.println("angle = " + veh.getLocation().getAngle());
                writer.println("color1 = " + veh.getColor1());
                writer.println("color2 = " + veh.getColor2());
                writer.println("rankid = " + rank.getNumber());
            }
        } catch(IOException e) {
            logger.error("Could not write vehicle data to " + file.getName() + " Error: " + e.getMessage());
        } finally {
            if(writer != null) {
                writer.close();
            }
        }

    }

    private String toFilename(Job job) {
        return String.format("%d-%s", job.getId(), job.getName());
    }

    private int toId(File file) {
        String name = file.getName();
        int index = name.indexOf("-");
        int id = 0;
        if(index != -1) {
            try {
                id = Integer.parseInt(name.substring(0, index));
            } catch(NumberFormatException e) {
                logger.error("File " + file + " could not be parsed for id.");
            }
        }
        return id;
    }


    @Override
    public void delete(Job job) {
        if(jobToDirectory.containsKey(job)) {
            File jobDirectory = jobToDirectory.get(job);
            jobDirectory.delete();
        }
    }

    @Override
    public int generateId(String name) {
        List<Integer> usedIds = new ArrayList<>();

        File[] jobDirectories = factionDirectory.listFiles();
        if(jobDirectories != null) {
            for(File directory : jobDirectories) {
                usedIds.add(toId(directory));
            }
        }

        if(jobDirectories != null) {
            jobDirectories = contractJobDirectory.listFiles();
            for(File directory : jobDirectories) {
                usedIds.add(toId(directory));
            }
        }

        int max = 0;
        for(int id : usedIds) {
            if(id > max)
                max = id;
        }
        return ++max;
    }

    @Override
    public TrashMissions getTrashMissions() {
        TrashMissions trashMissions = new TrashMissions();
        File[] missionFiles = trashMissionDirectory.listFiles();
        if(missionFiles != null) {
            for(File missionFile : missionFiles) {
                try {
                    trashMissions.add(parseTrashMission(missionFile));
                } catch(IOException e) {
                    logger.error("Erro while reading trash mission data from " + missionFile.getName() + ". Error: "+ e.getMessage());
                }
            }
        }
        return trashMissions;
    }

    @Override
    public OfficerJob getPoliceFaction(int jobid) throws IOException {
        OfficerJob officerJob = new OfficerJob(jobid);
        File[] factionDirectories = factionDirectory.listFiles();
        if(factionDirectories != null && factionDirectories.length > 0) {
            for(File factionDir : factionDirectories) {
                int id = toId(factionDir);
                if(id == jobid) {
                    File[] dataFiles = factionDir.listFiles();
                    for(File dataFile : dataFiles) {
                        if(dataFile.getName().equalsIgnoreCase("main.dat")) {
                            parseFactionData(officerJob, dataFile);
                        } else if(dataFile.getName().equalsIgnoreCase("vehicles.dat")) {
                            parseFactionVehicles(officerJob, dataFile);
                        } else if(dataFile.getName().equalsIgnoreCase("ranks.dat")) {
                            parseFactionRanks(officerJob, dataFile);
                        }
                    }
                }
            }
        }
        return officerJob;
    }

    private TrashMission parseTrashMission(File file) throws IOException {
        String filename = file.getName();
        if(filename.contains("-")) {
            int index = filename.indexOf("-");
            int id = Integer.parseInt(filename.substring(0, index));
            String name = filename.substring(index+1);
            TrashMission mission = new TrashMission(id, name);
            try (
                    BufferedReader bf = new BufferedReader(new FileReader(file));
                    ) {
                String line;
                while ((line = bf.readLine()) != null) {
                    String[] parts = line.split(",");
                    if(parts.length == 3) {
                        Location location = new Location();
                        location.setX(Float.parseFloat(parts[0].trim()));
                        location.setY(Float.parseFloat(parts[1].trim()));
                        location.setZ(Float.parseFloat(parts[2].trim()));
                        mission.addGarbage(location);
                    }
                }
            }
            return mission;
        }
        return null;
    }
}
*/