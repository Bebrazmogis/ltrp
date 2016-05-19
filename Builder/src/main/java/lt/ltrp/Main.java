package lt.ltrp;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/*

DATA FILE structure:

groupId artifactId type dir

 */
public class Main {

    private static final String APP_NAME = "builder";
    private static final String MAVEN_REPOSITORY = "C:\\Users\\Justas\\.m2\\repository";

    private static DataFile dataFile;

    public static void main(String[] args) {
        if(args.length == 0) {
            printHelp();
            return;
        }
        String appPath = System.getProperty("user.dir");
        int len = args.length;
        dataFile = new DataFile(new File(appPath + File.separator + "modules.ini"));

        System.out.println("Work path " + appPath);

        // Add new module
        if (args[0].equalsIgnoreCase("add")) {
            if(len != 3) {
                System.out.println("USAGE " + APP_NAME + " add [gm/api/runtime/other] [directory path]");
            } else {
                String type = args[1];
                File file = new File(args[2]);
                File pom = new File(file, "pom.xml");
                if(!type.equalsIgnoreCase("gm") && !type.equalsIgnoreCase("api") && !type.equalsIgnoreCase("runtime") && !type.equalsIgnoreCase("other")) {
                    System.out.println("Invalid type");
                } else if(!file.exists() || !file.isDirectory()) {
                    System.out.println("Invalid directory");
                } else if(!pom.exists()) {
                    System.out.println(file.getName() + " does not contain a pom.xml");
                } else {
                    MavenXpp3Reader reader = new MavenXpp3Reader();
                    Model model = null;
                    try {
                        model = reader.read(new FileReader(pom));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                    String groupId = model.getGroupId();
                    String artifactId = model.getArtifactId();
                    String version = model.getVersion();
                    if(groupId != null && artifactId != null && version != null) {
                        ProjectData data = dataFile.readProjectData();
                        data.add(groupId, artifactId, version, type, Paths.get(appPath).relativize(file.getAbsoluteFile().toPath()));
                        dataFile.writeProjectData(data);
                        System.out.println(groupId + ":" + artifactId + " successfully added");
                    } else {
                        System.out.println("Missing groupId or artifactId attribute in " + file.getName() + File.separator + pom.getName());
                    }
                }
            }
        } else if(args[0].equalsIgnoreCase("validate")) {

        } else if(args[0].equalsIgnoreCase("remove")) {
            if(len != 3) {
                System.out.println("USAGE " + APP_NAME + " add [gm/api/runtime/other] [directory path]");
            } else {
                String groupId = args[1];
                String artifactId = args[2];
                ProjectData data = dataFile.readProjectData();
                if(data.contains(groupId, artifactId)) {
                    data.remove(groupId, artifactId);
                    dataFile.writeProjectData(data);
                    System.out.println("Removed " + groupId + ":" + artifactId);
                } else
                    System.out.println(groupId + ":" + artifactId + " not found");
            }
        } else if(args[0].equalsIgnoreCase("build")) {
            ProjectData data = dataFile.readProjectData();
            System.out.println("Found " + data.size() + " projects");
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
          //mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
          //mapper.disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
          //mapper.disable(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS);
            File resourcesFile = new File(appPath + File.separator + "shoebill" + File.separator + "resources.yml");
            if(!resourcesFile.exists())
                System.out.println("Resources file not found");
            else {
                try {
                    ResourcesModel resources = mapper.readValue(resourcesFile, ResourcesModel.class);
                    System.out.println("resources:" + resources);
                    resources.getPlugins().clear();
                    data.getData().forEach(project -> {
                        System.out.println("Building " + project.getType() + " " + project.getGroupId() + ":" + project.getArtifactId() + "...");
                        try {
                            buildProject(project.getPath());
                            moveProject(project, appPath);
                            resources.getPlugins().add(project.getArtifactId() + "-" + project.getVersion());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    mapper.writeValue(resourcesFile, resources);
                    System.out.println("Finished building!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if(args[0].equalsIgnoreCase("test")) {
            File resources = new File(appPath + File.separator + "shoebill" + File.separator + "resources.yml");
            if(resources.exists()) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                try {
                    ResourcesModel model = mapper.readValue(resources, ResourcesModel.class);
                    System.out.println("TYPE:" + model.getRepositories().get(0).getClass().getName());
                   // TestModel model = mapper.readValue(resources, TestModel.class);
                    System.out.println("Model" + model.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("File " + resources.getAbsolutePath() + " does not exist.");
            }
        } else {
            printHelp();
        }
    }

    private static void buildProject(Path pathToPom) throws IOException, InterruptedException {
        String cmd = String.format("%s%cbin%cmvn.cmd",
                System.getenv("M2_HOME"), File.separatorChar, File.separatorChar);
        String params = String.format("-f%s", pathToPom);
        ProcessBuilder builder = new ProcessBuilder(cmd, params, "-e");
        builder.redirectErrorStream(true);
        Process proc = builder.start();
        MavenOutputReader outputReader = new MavenOutputReader(proc.getInputStream());
        MavenErrorReader errorReader = new MavenErrorReader(proc.getErrorStream());
        outputReader.start();
        errorReader.start();
        proc.waitFor();
        outputReader.interrupt();
        errorReader.interrupt();
        errorReader.getErrors().forEach(s -> System.out.println("ERROR:" + s));
        System.out.println("... " + (outputReader.isSuccess() ? "success" : "failure"));
    }

    private static void moveProject(Project project, String projectRoot) throws IOException, InterruptedException {
        //xcopy C:\Users\Justas\.m2\repository\lt\ltrp\player-api\1.0-SNAPSHOT\player-api-1.0-SNAPSHOT.jar F:\aaaa\ltrp\shoebill\plugins\
        Process copyProcess = Runtime.getRuntime().exec(String.format("xcopy /y %s%c%s%c%s%c%s%c%s-%s.jar %s%cshoebill%c%s%c",
                MAVEN_REPOSITORY,
                File.separatorChar,
                groupIdToPath(project.getGroupId()),
                File.separatorChar,
                project.getArtifactId(),
                File.separatorChar,
                project.getVersion(),
                File.separatorChar,
                project.getArtifactId(),
                project.getVersion(),
                projectRoot, File.separatorChar, File.separatorChar, project.getType().equals("gm") ? "gamemodes" : "plugins", File.separatorChar));
        copyProcess.waitFor();
    }

    private static void printHelp() {
        System.out.println("USAGE: " + APP_NAME + " [command] [options]");
        System.out.println("Commands:");
        System.out.println("\tadd [gm/api/runtime/other] [directory path]");
        System.out.println("\tremove [groupId] [artifactId]");
        System.out.println("\tvalidate");
        System.out.println("\tbuild ");
    }

    private static boolean containsArg(String[] args, String arg) {
        for (String s : args) {
            if(s.equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }

    private static String groupIdToPath(String groupId) {
        //return groupId.replaceAll("\\\\.", File.separator);
        char[] chars = groupId.toCharArray();
        for(int i = 0; i < chars.length; i++)
            if(chars[i] == '.')
                chars[i] = File.separatorChar;
        return new String(chars);
    }

    private static String getElement(Document doc, String tagName) {
        System.out.println("doc:" + doc.toString());
        NodeList list = doc.getElementsByTagName(tagName);
        System.out.println("List size:" + list.getLength());
        for(int i = 0 ; i < list.getLength(); i++)
            System.out.println("i:" + i + " " + list.item(i).getNodeValue());
        if(list.getLength() > 0) {
            return list.item(0).getNodeValue();
        }
        return null;
    }


    private static Document parsePom(File file) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        } catch (SAXException e) {
            System.err.println("SAXEException:" + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException:" + e.getMessage());
        } catch (ParserConfigurationException e) {
            System.err.println("ParserConfigurationException:" + e.getMessage());
        }
        return null;
    }
}
