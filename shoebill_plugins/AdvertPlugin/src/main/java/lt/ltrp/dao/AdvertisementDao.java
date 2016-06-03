package lt.ltrp.dao;

import lt.ltrp.data.Advert;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Bebras
 *         2016.06.01.
 */
public interface AdvertisementDao {

    List<Advert> get();
    List<Advert> get(Timestamp beforeDate);
    Advert get(int uuid);
    void update(Advert advert);
    void remove(Advert advert);
    int insert(Advert advert);
}
