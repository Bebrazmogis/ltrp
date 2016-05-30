package lt.ltrp.dao.impl;

import lt.ltrp.dao.GraffitiObjectDao;
import lt.ltrp.data.GraffitiObject;
import net.gtaun.shoebill.constant.ObjectMaterialSize;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class MySqlGraffitiObjectDaoImpl implements GraffitiObjectDao {

    private DataSource dataSource;

    public MySqlGraffitiObjectDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected GraffitiObject toObject(ResultSet r) throws SQLException {
        return new GraffitiObject(
                r.getInt("id"),
                r.getInt("model_id"),
                ObjectMaterialSize.get(r.getInt("material_size"))
        );
    }

    @Override
    public List<GraffitiObject> get() {
        List<GraffitiObject> objects = new ArrayList<>();
        String sql = "SELECT * FROM graffiti_objects";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet r = stmt.executeQuery();
            while(r.next())
                objects.add(toObject(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return objects;
    }

    @Override
    public GraffitiObject get(int uuid) {
        String sql = "SELECT * FROM graffiti_objects WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1 , uuid);
            ResultSet r = stmt.executeQuery();
            if(r.next())
                return toObject(r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(GraffitiObject object) {
        String sql = "UPDATE graffiti_objects SET model_id = ?, material_size = ? WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, object.getModelId());
            stmt.setInt(2, object.getMaterialSize().getValue());
            stmt.setInt(3, object.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(GraffitiObject object) {
        String sql = "DELETE FROM graffiti_objects WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, object.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(GraffitiObject object) {
        String sql = "INSERT INTO graffiti_objects(model_id, material_size)  VALUES (?, ?)";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, object.getModelId());
            stmt.setInt(2, object.getMaterialSize().getValue());
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
