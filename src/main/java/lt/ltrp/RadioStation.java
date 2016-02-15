package lt.ltrp;

import java.util.List;

/**
 * @author Bebras
 *         2016.02.15.
 */
public class RadioStation {

    private static List<RadioStation> radioStations;

    public static List<RadioStation> get() {
        if(radioStations == null) {
            try {
                radioStations = LtrpGamemode.getDao().getRadioStationDao().get();
            } catch (LoadingException e) {
                e.printStackTrace();
            }
        }
        return radioStations;
    }

    private int id;
    private String url;
    private String name;

    public RadioStation(int id, String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
