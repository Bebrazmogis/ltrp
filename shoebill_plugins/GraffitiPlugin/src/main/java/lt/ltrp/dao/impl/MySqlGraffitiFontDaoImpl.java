package lt.ltrp.business.dao.impl;

import lt.ltrp.dao.GraffitiFontDao;
import lt.ltrp.data.GraffitiFont;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class MySqlGraffitiFontDaoImpl implements GraffitiFontDao {

    private DataSource dataSource;

    public MySqlGraffitiFontDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    protected GraffitiFont toFont(ResultSet r) throws SQLException {
        return new GraffitiFont(
                r.getInt("id"),
                r.getString("name"),
                r.getInt("size")
        );
    }

    @Override
    public List<GraffitiFont> get() {
        List<GraffitiFont> fonts = new ArrayList<>();
        String sql = "SELECT * FROM graffiti_fonts";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet r = stmt.executeQuery();
            while(r.next())
                fonts.add(toFont(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fonts;
    }

    @Override
    public GraffitiFont get(int uuid) {
        String sql = "SELECT * FROM graffiti_fonts WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1 , uuid);
            ResultSet r = stmt.executeQuery();
            if(r.next())
                return toFont(r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(GraffitiFont font) {
        String sql = "UPDATE graffiti_fonts SET name = ?, size = ? WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, font.getName());
            stmt.setInt(2, font.getSize());
            stmt.setInt(3, font.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(GraffitiFont font) {
        String sql = "DELETE FROM graffiti_fonts WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, font.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(GraffitiFont font) {
        String sql = "INSERT INTO graffiti_fonts (name, size) VALUES (?, ?)";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setString(1, font.getName());
            stmt.setInt(2, font.getSize());
            stmt.execute();
            ResultSet keys = stmt.getGeneratedKeys();
            if(keys.next())
                return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
