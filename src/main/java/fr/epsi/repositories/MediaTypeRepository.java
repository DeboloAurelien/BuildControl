package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.Demand;
import fr.epsi.entities.Media;
import fr.epsi.entities.MediaType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaTypeRepository implements Repository<MediaType, Long> {
    @Override
    public MediaType findById(Long id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM bc_media_type WHERE id = ?;"))
        {
            ps.setLong(1, id);
            rs = ps.executeQuery();
            MediaType mediaType = null;

            if (rs.next()) {
                mediaType = this.setData(rs);
            }

            return mediaType;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<MediaType> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_media_type;"))
        {
            List<MediaType> mediaTypes = new ArrayList<>();

            while (rs.next()) {
                mediaTypes.add(this.setData(rs));
            }

            return mediaTypes;
        }
    }

    @Override
    public MediaType create(MediaType mediaType) throws SQLException {
        try (
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO bc_media_type (label) VALUES (?)",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, mediaType.getLabel());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating mediaType failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mediaType.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating mediaType failed, no ID obtained.");
                }
            }
        }

        return findById(mediaType.getId());
    }

    @Override
    public MediaType update(MediaType mediaType) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE bc_media_type " +
                             "SET label = ?" +
                             "WHERE id = ?;"
             ))
        {
            ps.setString(1, mediaType.getLabel());

            ps.executeUpdate();

            return findById(mediaType.getId());
        }
    }

    @Override
    public void delete(MediaType mediaType) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_media_type WHERE id = ?;"))
        {
            ps.setLong(1, mediaType.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public MediaType setData(ResultSet rs) throws SQLException {
        MediaType mediaType = new MediaType()
                .setId(rs.getLong("id"))
                .setLabel(rs.getString("label"));

        mediaType.setMedias(getMedias(mediaType));

        return mediaType;
    }

    public List<Media> getMedias(MediaType mediaType) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT m.* " +
                             "FROM bc_media m " +
                             "INNER JOIN bc_media_type mt ON mt.id = m.media_type_id " +
                             "WHERE mt.id = ?;"))
        {
            ps.setLong(1, mediaType.getId());
            rs = ps.executeQuery();
            List<Media> medias = new ArrayList<>();

            while (rs.next()) {
                medias.add(new Media()
                        .setId(rs.getLong("id"))
                        .setPath(rs.getString("path"))
                        .setDemand(new Demand().setId(rs.getString("demand_id")))
                        .setMediaType(mediaType)
                );
            }

            return medias;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
