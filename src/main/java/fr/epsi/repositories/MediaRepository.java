package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.Demand;
import fr.epsi.entities.Media;
import fr.epsi.entities.MediaType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaRepository implements Repository<Media, Long> {
    @Override
    public Media findById(Long id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM bc_media WHERE id = ?;"))
        {
            ps.setLong(1, id);
            rs = ps.executeQuery();
            Media media = null;

            if (rs.next()) {
                media = this.setData(rs);
            }

            return media;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<Media> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_media;"))
        {
            List<Media> medias = new ArrayList<>();

            while (rs.next()) {
                medias.add(this.setData(rs));
            }

            return medias;
        }
    }

    @Override
    public Media create(Media media) throws SQLException {
        try (
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO bc_media(path, demand_id, media_type_id) VALUES (?,?,?);",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, media.getPath());
            ps.setString(2, media.getDemand().getId());
            ps.setLong(3, media.getMediaType().getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating media failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    media.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating media failed, no ID obtained.");
                }
            }
        }

        return findById(media.getId());
    }

    @Override
    public Media update(Media media) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE bc_media " +
                             "SET path = ?," +
                             "demand_id = ?," +
                             "media_type_id = ? " +
                             "WHERE id = ?;"))
        {
            ps.setString(1, media.getPath());
            ps.setString(2, media.getDemand().getId());
            ps.setLong(3, media.getMediaType().getId());
            ps.setLong(4, media.getId());

            ps.executeUpdate();

            return findById(media.getId());
        }
    }

    @Override
    public void delete(Media media) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_media WHERE id = ?;"))
        {
            ps.setLong(1, media.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Media setData(ResultSet rs) throws SQLException {
        return new Media()
                .setId(rs.getLong("id"))
                .setPath(rs.getString("path"))
                .setDemand(new Demand().setId(rs.getString("demand_id")))
                .setMediaType(new MediaType().setId(rs.getLong("media_type_id")));
    }
}
