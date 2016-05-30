package lt.ltrp.dao.impl;

import lt.ltrp.dao.GraffitiDao;
import lt.ltrp.object.Graffiti;
import lt.ltrp.object.impl.GraffitiImpl;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.util.event.EventManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Bebras
 *         2016.05.30.
 */
public class MySqlGraffitiDaoImpl implements GraffitiDao {

    private DataSource dataSource;
    private EventManager eventManager;
    private MySqlGraffitiObjectDaoImpl objectDao;
    private MySqlGraffitiFontDaoImpl fontDao;
    private AbstractMySqlGraffitiColorDao colorDao;

    public MySqlGraffitiDaoImpl(DataSource dataSource, EventManager eventManager, MySqlGraffitiObjectDaoImpl objectDao, MySqlGraffitiFontDaoImpl fontDao, AbstractMySqlGraffitiColorDao colorDao) {
        this.dataSource = dataSource;
        this.eventManager = eventManager;
        this.objectDao = objectDao;
        this.fontDao = fontDao;
        this.colorDao = colorDao;
    }




    private Graffiti toGraffiti(ResultSet r) throws SQLException {
        return new GraffitiImpl(r.getInt("id"),
                r.getInt("author"),
                r.getString("text"),
                objectDao.toObject(r),
                new Vector3D(r.getFloat("pos_x"), r.getFloat("pos_y"), r.getFloat("pos_z")),
                new Vector3D(r.getFloat("rot_x"), r.getFloat("rot_y"), r.getFloat("rot_z")),
                fontDao.toFont(r),
                colorDao.toColor(r),
                r.getInt("approved_by"),
                r.getTimestamp("created_at"),
                eventManager
        );
    }

    @Override
    public Collection<Graffiti> get() {
        List<Graffiti> graffiti = new ArrayList<>();
        String sql = "SELECT * FROM graffiti LEFT JOIN graffiti_objects ON graffiti_objects.id = graffiti.object_id " +
                "LEFT JOIN graffiti_colors ON graffiti_colors.id = graffiti.color_id " +
                "LEFT JOIN graffiti_fonts ON graffiti_fonts.id = graffiti.font_id" ;
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet r = stmt.executeQuery();
            while(r.next())
                graffiti.add(toGraffiti(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return graffiti;
    }

    @Override
    public Graffiti get(int uuid) {
        String sql = "SELECT * FROM graffiti LEFT JOIN graffiti_objects ON graffiti_objects.id = graffiti.object_id " +
                "LEFT JOIN graffiti_colors ON graffiti_colors.id = graffiti.color_id " +
                "LEFT JOIN graffiti_fonts ON graffiti_fonts.id = graffiti.font_id WHERE graffiti.id = ?" ;
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1 , uuid);
            ResultSet r = stmt.executeQuery();
            if(r.next())
                return toGraffiti(r);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Graffiti graffiti) {
        String sql = "UPDATE graffiti SET author = ?, object_id = ?, text = ?, pos_x = ?, pos_y = ?, pos_z = ?, rot_x = ?, rot_y = ?, rot_z = ?, font_id = ?, color_id = ?, approved_by = ?, created_at = ? WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, graffiti.getAuthorUserId());
            stmt.setInt(2, graffiti.getObjectType().getUUID());
            stmt.setString(3, graffiti.getText());
            stmt.setFloat(4, graffiti.getPosition().getX());
            stmt.setFloat(5, graffiti.getPosition().getY());
            stmt.setFloat(6, graffiti.getPosition().getZ());
            stmt.setFloat(7, graffiti.getRotation().getX());
            stmt.setFloat(8, graffiti.getRotation().getY());
            stmt.setFloat(9, graffiti.getRotation().getZ());
            stmt.setInt(10, graffiti.getFont().getUUID());
            stmt.setInt(11, graffiti.getColor().getUUID());
            stmt.setInt(12, graffiti.getApprovedByUserId());
            stmt.setTimestamp(13, graffiti.getCreatedAt());
            stmt.setInt(14, graffiti.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Graffiti graffiti) {
        String sql = "DELETE FROM graffiti WHERE id = ?";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, graffiti.getUUID());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(Graffiti graffiti) {
        String sql = "INSERT INTO graffiti (author, object_id, text, pos_x, pos_y, pos_z, rot_x, rot_y, rot_z, font_id, color_id, approved_by, created_at) VALUES (?, ?, ?, ?, ?, ? ,? ,? ,?, ?, ?, ?, ?)";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, graffiti.getAuthorUserId());
            stmt.setInt(2, graffiti.getObjectType().getUUID());
            stmt.setString(3, graffiti.getText());
            stmt.setFloat(4, graffiti.getPosition().getX());
            stmt.setFloat(5, graffiti.getPosition().getY());
            stmt.setFloat(6, graffiti.getPosition().getZ());
            stmt.setFloat(7, graffiti.getRotation().getX());
            stmt.setFloat(8, graffiti.getRotation().getY());
            stmt.setFloat(9, graffiti.getRotation().getZ());
            stmt.setInt(10, graffiti.getFont().getUUID());
            stmt.setInt(11, graffiti.getColor().getUUID());
            stmt.setInt(12, graffiti.getApprovedByUserId());
            stmt.setTimestamp(13, graffiti.getCreatedAt());
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
