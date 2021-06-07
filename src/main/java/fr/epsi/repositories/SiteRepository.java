package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.Profession;
import fr.epsi.entities.Project;
import fr.epsi.entities.Site;
import fr.epsi.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SiteRepository implements Repository<Site, Long> {
    @Override
    public Site findById(Long id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM bc_site WHERE id = ?;"))
        {
            ps.setLong(1, id);
            rs = ps.executeQuery();
            Site site = null;

            if (rs.next()) {
                site = this.setData(rs);
            }

            return site;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<Site> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_site;"))
        {
            List<Site> sites = new ArrayList<>();

            while (rs.next()) {
                sites.add(this.setData(rs));
            }

            return sites;
        }
    }

    @Override
    public Site create(Site site) throws SQLException {
        try (
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO bc_site(name) VALUES(?);",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, site.getName());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating site failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    site.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating site failed, no ID obtained.");
                }
            }
        }

        return findById(site.getId());
    }

    @Override
    public Site update(Site site) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE bc_site SET name = ? WHERE id = ?;"))
        {
            ps.setString(1, site.getName());
            ps.setLong(2, site.getId());

            ps.executeUpdate();

            return findById(site.getId());
        }
    }

    @Override
    public void delete(Site site) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_site WHERE id = ?;"))
        {
            ps.setLong(1, site.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Site setData(ResultSet rs) throws SQLException {
        Site site = new Site()
                .setId(rs.getLong("id"))
                .setName(rs.getString("name"));

        site.setUsers(getUsers(site));
        site.setProjects(getProjects(site));

        return site;
    }

    public List<User> getUsers(Site site) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT u.* " +
                             "FROM bc_user u " +
                             "INNER JOIN bc_user_site us ON u.id = us.user_id " +
                             "INNER JOIN bc_site s ON s.id = us.site_id " +
                             "WHERE s.id = ?;"))
        {
            ps.setLong(1, site.getId());
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
                        .setProfession(new Profession().setId(rs.getLong("profession_id"))));
            }

            return users;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public List<Project> getProjects(Site site) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT p.* " +
                             "FROM bc_project p " +
                             "INNER JOIN bc_site s ON s.id = p.site_id " +
                             "WHERE s.id = ?;"))
        {
            ps.setLong(1, site.getId());
            rs = ps.executeQuery();
            List<Project> projects = new ArrayList<>();

            while (rs.next()) {
                projects.add(new Project()
                        .setId(rs.getLong("id"))
                        .setName(rs.getString("name"))
                        .setStartDate(rs.getDate("start_date"))
                        .setEndDate(rs.getDate("end_date"))
                        .setSite(site)
                );
            }

            return projects;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
