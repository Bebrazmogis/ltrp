package lt.ltrp.dao.impl;

import lt.ltrp.dao.AdvertisementDao;
import lt.ltrp.data.Advert;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.06.03.
 */
public class MySqlAdvertisementDaoImpl implements AdvertisementDao {

    private DataSource dataSource;

    public MySqlAdvertisementDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Advert> get() {
        ArrayList<Advert> ads = new ArrayList<>();
        String sql = "SELECT * FROM advertisements";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            ResultSet result = stmt.executeQuery();
            while(result.next())
                ads.add(toAd(result));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ads;
    }

    @Override
    public List<Advert> get(Timestamp beforeDate) {
        ArrayList<Advert> ads = new ArrayList<>();
        String sql = "SELECT * FROM advertisements WHERE created_at < ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setTimestamp(1, beforeDate);
            ResultSet result = stmt.executeQuery();
            while(result.next())
                ads.add(toAd(result));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ads;
    }

    @Override
    public Advert get(int uuid) {
        String sql = "SELECT * FROM advertisements WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, uuid);
            ResultSet result = stmt.executeQuery();
            if(result.next())
                return toAd(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Advert advert) {
        String sql = "UPDATE advertisements SET player_id = ?, phonenumber = ?, ad = ?, price = ?, created_at = ? WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, advert.getAuthorUserId());
            stmt.setInt(2, advert.getPhoneNumber());
            stmt.setString(3, advert.getAdText());
            stmt.setInt(4, advert.getPrice());
            stmt.setTimestamp(5, advert.getCreatedAt());
            stmt.setInt(6, advert.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Advert advert) {
        String sql = "DELETE FROM advertisements WHERE id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, advert.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(Advert advert) {
        String sql = "INSERT INTO advertisements (player_id, phonenumber, ad, price, created_at) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, advert.getAuthorUserId());
            stmt.setInt(2, advert.getPhoneNumber());
            stmt.setString(3, advert.getAdText());
            stmt.setInt(4, advert.getPrice());
            stmt.setTimestamp(5, advert.getCreatedAt());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next())
                return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Advert toAd(ResultSet r) throws SQLException {
        return new Advert(
                r.getInt("id"),
                r.getInt("player_id"),
                r.getInt("phonenumber"),
                r.getString("ad"),
                r.getInt("price"),
                r.getTimestamp("created_at")
        );
    }
}
