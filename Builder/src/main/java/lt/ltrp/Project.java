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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Project && groupId.equals(((Project) obj).getGroupId()) && artifactId.equals(((Project) obj).getArtifactId());
    }

    @Override
    public int hashCode() {
        int hash = 31;
        hash += 112 * (artifactId != null ? artifactId.hashCode() : 0);
        hash += 112 * (groupId != null ? groupId.hashCode() : 0);
        return hash;
    }
}
