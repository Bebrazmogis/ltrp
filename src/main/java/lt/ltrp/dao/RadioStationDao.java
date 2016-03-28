package lt.ltrp.dao;

import lt.ltrp.LoadingException;
import lt.ltrp.radio.RadioStation;

import java.util.List;

/**
 * @author Bebras
 *         2016.02.16.
 */
public interface RadioStationDao {

    List<RadioStation> get() throws LoadingException;
    void update(RadioStation station);
    void insert(RadioStation station);
    void remove(RadioStation station);

}
