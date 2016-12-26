package lt.ltrp.radio.dao;


import lt.ltrp.radio.data.RadioStation;

import java.util.List;

/**
 * @author Bebras
 *         2016.02.16.
 */
public interface RadioStationDao {

    List<RadioStation> get();
    void update(RadioStation station);
    void insert(RadioStation station);
    void remove(RadioStation station);

}
