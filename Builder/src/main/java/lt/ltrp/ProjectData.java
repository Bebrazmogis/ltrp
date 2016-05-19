package lt.ltrp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Bebras
 *         2016.04.16.
 */
public class ProjectData {

    private Collection<Project> data;

    public ProjectData() {
        data = new ArrayList<>();
    }

    public void add(String groupId, String artifactId, String version, String type, Path path) {
        if(contains(groupId, artifactId))
            remove(groupId, artifactId);
        data.add(new Project(groupId, artifactId, version, type, path));
    }

    public Collection<Project> getData() {
        return data;
    }

    public Project get(String groupId, String artifactId) {
        Optional<Project>  op = data.stream().filter(d -> d.getArtifactId().equals(artifactId) && d.getGroupId().equals(groupId)).findFirst();
        return op.isPresent() ? op.get() : null;
    }


    public boolean contains(String groupId, String artifactId) {
        return data.stream().filter(d -> d.getArtifactId().equals(artifactId) && d.getGroupId().equals(groupId)).findFirst().isPresent();
    }

    public void remove(String groupId, String artifactId) {
        if(contains(groupId, artifactId))
            data.remove(get(groupId, artifactId));
    }

    public int size() {
        return data.size();
    }
}
