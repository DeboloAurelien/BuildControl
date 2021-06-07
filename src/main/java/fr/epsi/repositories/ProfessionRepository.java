package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.Profession;
import fr.epsi.entities.Subcontractor;
import fr.epsi.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessionRepository implements Repository<Profession,Long> {
    @Override
    public Profession findById(Long id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM bc_profession WHERE id = ?;"))
        {
            ps.setLong(1, id);
            rs = ps.executeQuery();
            Profession profession = new Profession();

            if (rs.next()) {
                profession = this.setData(rs);
            }

            return profession;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<Profession> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_profession;"))
        {
            List<Profession> professions = new ArrayList<>();

            while (rs.next()) {
                professions.add(this.setData(rs));
            }

            return professions;
        }
    }

    @Override
    public Profession create(Profession profession) throws SQLException {
        try (
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO bc_profession(label) VALUES (?);",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, profession.getLabel());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating profession failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    profession.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating profession failed, no ID obtained.");
                }
            }
        }

        return findById(profession.getId());
    }

    @Override
    public Profession update(Profession profession) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE bc_profession SET label = ? WHERE id = ?;"))
        {
            ps.setString(1, profession.getLabel());
            ps.setLong(2, profession.getId());

            ps.executeUpdate();

            return findById(profession.getId());
        }
    }

    @Override
    public void delete(Profession profession) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_profession WHERE id =?;"))
        {
            ps.setLong(1, profession.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Profession setData(ResultSet rs) throws SQLException {
        Profession profession = new Profession()
                .setId(rs.getLong("id"))
                .setLabel(rs.getString("label"));

        profession.setUsers(getUsers(profession));
        profession.setSubcontractors(getSubcontractors(profession));

        return profession;
    }

    public List<User> getUsers(Profession profession) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT u.* " +
                             "FROM bc_user u " +
                             "INNER JOIN bc_profession p ON p.id = u.profession_id " +
                             "WHERE p.id = ?;"))
        {
            ps.setLong(1, profession.getId());
            rs = ps.executeQuery();
            List<User> users = new ArrayList<>();

            while (rs.next()) {
                users.add(new User()
                        .setId(rs.getLong("id"))
                        .setUsername(rs.getString("username"))
                        .setPasswordHash(rs.getString("password_hash"))
                        .setDateLastConnection(rs.getTimestamp("date_last_connection"))
                        .setNumberConnectionAttempt(rs.getLong("number_connection_attempt"))
                        .setFirstname(rs.getString("firstname"))
                        .setLastname(rs.getString("lastname"))
                        .setArchive(rs.getBoolean("archive"))
                        .setProfession(profession)
                );
            }

            return users;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public List<Subcontractor> getSubcontractors(Profession profession) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT s.* " +
                             "FROM bc_subcontractor s " +
                             "INNER JOIN bc_subcontractor_profession sp ON sp.subcontractor_id = s.id " +
                             "INNER JOIN bc_profession p ON p.id = sp.profession_id " +
                             "WHERE p.id = ?;"))
        {
            ps.setLong(1, profession.getId());
            rs = ps.executeQuery();
            List<Subcontractor> subcontractors = new ArrayList<>();

            while (rs.next()) {
                subcontractors.add(new Subcontractor()
                        .setId(rs.getLong("id"))
                        .setName(rs.getString("name"))
                        .setAddress(rs.getString("address"))
                        .setPostal(rs.getString("postal"))
                        .setCity(rs.getString("city"))
                );
            }

            return subcontractors;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
