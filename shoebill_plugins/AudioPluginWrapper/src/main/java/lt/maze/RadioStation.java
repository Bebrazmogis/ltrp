package lt.maze;

/**
 * @author Bebras
 *         2016.02.15.
 */
public enum  RadioStation {


    Off(0),
    PlaybackFm(1),
    KRose(2),
    KDST(3),
    BounceFm(4),
    SFUR(5),
    RadioLosSantos(6),
    RadioX(7),
    CSR1039(8),
    KJahWest(9),
    MasterSounds983(10),
    WCTR(11),
    UserTrackPlayer(12);

    static RadioStation get(int id) {
        for(RadioStation station : values()) {
            if(station.getId() == id) {
                return station;
            }
        }
        return null;
    }


    int id;

    RadioStation(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
