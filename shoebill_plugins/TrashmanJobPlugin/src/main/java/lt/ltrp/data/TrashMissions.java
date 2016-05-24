package lt.ltrp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2015.12.17.
 */
public class TrashMissions {


    private List<TrashMission> trashMissions;

    public TrashMissions() {
        this.trashMissions = new ArrayList<>();
    }

    public void add(TrashMission mission) {
        this.trashMissions.add(mission);
    }

    public List<TrashMission> get() {
        return trashMissions;
    }

    public boolean contains(String name) {
        for(TrashMission mission : trashMissions) {
            if(mission.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int id) {
        for(TrashMission mission : trashMissions) {
            if(mission.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public TrashMission getByName(String name) {
        for(TrashMission mission : trashMissions) {
            if(mission.getName().equals(name)) {
                return mission;
            }
        }
        return null;
    }

}
