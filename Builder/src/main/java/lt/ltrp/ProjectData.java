package lt.ltrp;

import java.nio.file.Path;
import java.util.*;

/**
 * @author Bebras
 *         2016.04.16.
 *
 *         Default order:
 *         <ol>
 *             <li>Api</li>
 *             <li>Other</li>
 *             <li>Runtime</li>
 *             <li>Gamemode</li>
 *         </ol>
 */
public class ProjectData {

    private static final Map<String, Integer> typeWeight = new HashMap<>();

    static {
        typeWeight.put("api", 1);
        typeWeight.put("other", 10);
        typeWeight.put("runtime", 100);
        typeWeight.put("gamemode", 1000);
    }

    private static final Comparator<Project> PROJECT_COMPARATOR = (p1, p2) -> {
        int w1 = getTypeWeight(p1.getType());
        int w2 = getTypeWeight(p2.getType());
        return Integer.compare(w1, w2);
    };

    private static int getTypeWeight(String type) {
        type = type.toLowerCase();
        return typeWeight.containsKey(type) ? typeWeight.get(type) : -1;
    }

    private List<Project> data;

    public ProjectData() {
        data = new ArrayList<>();
        /*data = new TreeSet<>((p1, p2) -> {
            int w1 = getTypeWeight(p1.getType());
            int w2 = getTypeWeight(p2.getType());
            return Integer.compare(w1, w2);
        });*/
    }

    public void add(String groupId, String artifactId, String version, String type, Path path) {
        if(contains(groupId, artifactId))
            remove(groupId, artifactId);
        Project project = new Project(groupId, artifactId, version, type, path);
        boolean s = data.add(project);
        Collections.sort(data, PROJECT_COMPARATOR);
        //System.out.println("S:" + s);
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
