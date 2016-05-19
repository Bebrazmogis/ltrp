package lt.ltrp;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Bebras
 *         2016.04.16.
 *
 *
DATA FILE structure:

groupId artifactId version type dir
 */
public class DataFile {

    private File file;

    public DataFile(File file) {
        this.file = file;
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Path getPath() {
        return file.toPath();
    }

    public ProjectData readProjectData() {
        ProjectData data = new ProjectData();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
             String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if(parts.length == 5) {
                    data.add(parts[0], parts[1], parts[2], parts[3], Paths.get(parts[4]));
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void writeProjectData(ProjectData projectData) {
        try(FileWriter out = new FileWriter(file)) {
            projectData.getData().forEach(d -> {
                try {
                    out.write(String.format("%s %s %s %s %s\r\n", d.getGroupId(), d.getArtifactId(), d.getVersion(), d.getType(), d.getPath().toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
