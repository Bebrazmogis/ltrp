package lt.ltrp.dao;

import lt.ltrp.data.BanData;

/**
 * @author Bebras
 *         2016.05.20.
 */
public interface BanDao {

    BanData getByUser(int userId);
    BanData getByIp(String ip);
    BanData getByUserOrIp(int userId, String ip);
    void update(BanData banData);

    /**
     * Soft-delete the ban record
     * @param banData ban data to be removed
     */
    void remove(BanData banData);
    void insert(BanData banData);

}
