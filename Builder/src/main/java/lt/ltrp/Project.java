package lt.ltrp;

import java.nio.file.Path;

/**
 * @author Bebras
 *         2016.04.16.
 */
public class Project {

    private String artifactId;
    private String groupId;
    private String version;
    private String type;
    private Path path;

    public Project(String groupId, String artifactId, String version, String type, Path path) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
        this.type = type;
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getType() {
        return type;
    }

    public Path getPath() {
        return path;
    }
}
