package lt.ltrp.dao.impl;

import lt.ltrp.dao.GraffitiColorDao;
import lt.ltrp.data.GraffitiColor;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class MySqlGraffitiColorDaoImpl extends AbstractMySqlGraffitiColorDao implements GraffitiColorDao {


    public MySqlGraffitiColorDaoImpl(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    public GraffitiColor get(int uuid) {
        String sql = "SELECT * FROM graffiti_colors WHERE id = ?";
        try(
                Connection connection = getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1 , uuid);
            ResultSet r = stmt.executeQuery();
            if(r.next())
                return toColor(r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<GraffitiColor> get() {
        List<GraffitiColor> colors = new ArrayList<>();
        String sql = "SELECT * FROM graffiti_colors";
        try(
                Connection connection = getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet r = stmt.executeQuery();
            while(r.next())
                colors.add(toColor(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colors;
    }

    @Override
    public void update(GraffitiColor color) {
        String sql = "UPDATE graffiti_colors SET color = ? WHERE id = ?";
        try(
                Connection connection = getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, color.getColor().getValue());
            stmt.setInt(2 , color.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(GraffitiColor color) {
        String sql = "DELETE FROM graffiti_colors WHERE id = ?";
        try(
                Connection connection = getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1 , color.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(GraffitiColor color) {
        String sql = "INSERT INTO graffiti_colors (color) VALUES (?)";
        try(
                Connection connection = getDataSource().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1 , color.getColor().getValue());
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
