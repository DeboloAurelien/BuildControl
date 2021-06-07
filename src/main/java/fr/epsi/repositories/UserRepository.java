package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements Repository<User, Long> {
    @Override
    public User findById(Long id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM bc_user WHERE id = ?;"))
        {
            ps.setLong(1, id);
            rs = ps.executeQuery();
            User user = null;

            if (rs.next()) {
                user = this.setData(rs);
            }

            return user;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_user;"))
        {
            List<User> users = new ArrayList<>();

            while (rs.next()) {
                users.add(this.setData(rs));
            }

            return users;
        }
    }

    @Override
    public User create(User user) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC AddUser @role = ?, @lastname = ?, @firstname = ?, @professionID = ?;"))
        {
            ps.setLong(1, getHigherRoleId(user));
            ps.setString(2, user.getLastname());
            ps.setString(3, user.getFirstname());
            ps.setLong(4, user.getProfession().getId());

            rs = ps.executeQuery();

            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        insertRoles(user);
        insertDemands(user);
        insertSites(user);

        return findById(user.getId());
    }

    @Override
    public User update(User user) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE bc_user " +
                             "SET date_last_connection = ?, " +
                             "number_connection_attempt = ?, " +
                             "archive = ?, " +
                             "profession_id = ? " +
                             "WHERE id = ?;"))
        {
            ps.setTimestamp(1, user.getDateLastConnection());
            ps.setLong(2, user.getNumberConnectionAttempt());
            ps.setBoolean(3, user.getArchive());
            ps.setLong(4, user.getProfession().getId());
            ps.setLong(5, user.getId());

            ps.executeUpdate();

            return findById(user.getId());
        }
    }

    @Override
    public void delete(User user) throws SQLException {
        if (user == null) {
            throw new SQLException("User (id=null) not found in repository, maybe already removed ?");
        }
        if (findById(user.getId()) == null) {
            throw new SQLException("User (id=" + user.getId() + ") not found in repository, maybe already removed ?");
        }

        deleteRoles(user);
        deleteDemands(user);
        deleteSites(user);

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_user WHERE id = ?;"))
        {
            ps.setLong(1, user.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public User setData(ResultSet rs) throws SQLException {
        User user = new User()
                .setId(rs.getLong("id"))
                .setUsername(rs.getString("username"))
                .setPasswordHash(rs.getString("password_hash"))
                .setDateLastConnection(rs.getTimestamp("date_last_connection"))
                .setNumberConnectionAttempt(rs.getLong("number_connection_attempt"))
                .setFirstname(rs.getString("firstname"))
                .setLastname(rs.getString("lastname"))
                .setArchive(rs.getBoolean("archive"))
                .setProfession(new Profession().setId(rs.getLong("profession_id")));

        user.setRoles(getRoles(user));
        user.setDemands(getDemands(user));
        user.setSites(getSites(user));

        return user;
    }

    public void insertRoles(User user) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC LinkUserRole @userID = ?, @roleID = ?;"))
        {
            for (Role role : user.getRoles()) {
                if (!role.getId().equals(getHigherRoleId(user))) {
                    ps.setLong(1, user.getId());
                    ps.setLong(2, role.getId());

                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    public void deleteRoles(User user) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC RemoveUserRole @userID = ?, @roleID = ?;"))
        {
            for (Role role : user.getRoles()) {
                ps.setLong(1, user.getId());
                ps.setLong(2, role.getId());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void insertDemands(User user) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC LinkUserDemand @userID = ?, @demandID = ?;"))
        {
            for (Demand demand : user.getDemands()) {
                ps.setLong(1, user.getId());
                ps.setString(2, demand.getId());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void deleteDemands(User user) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC RemoveUserDemand @userID = ?, @demandID = ?;"))
        {
            for (Demand demand : user.getDemands()) {
                ps.setLong(1, user.getId());
                ps.setString(2, demand.getId());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void insertSites(User user) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC LinkUserSite @userID = ?, @siteID = ?;"))
        {
            for (Site site : user.getSites()) {
                ps.setLong(1, user.getId());
                ps.setLong(2, site.getId());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void deleteSites(User user) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC RemoveUserSite @userID = ?, @siteID = ?;"))
        {
            for (Site site : user.getSites()) {
                ps.setLong(1, user.getId());
                ps.setLong(2, site.getId());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public User updatePassword(User user, String oldPassword) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC ChangePassword @username = ?, @oldPassword = ?, @newPassword = ?;"))
        {
            ps.setString(1, user.getUsername());
            ps.setString(2, oldPassword);
            ps.setString(3, user.getPasswordHash());

            ps.executeUpdate();
        }
        return findById(user.getId());
    }

    private Long getHigherRoleId(User user) {
        Long intern = null;
        Long chantier = null;

        for (Role role : user.getRoles()) {
            if (role.getLabel().equals("Admin")) {
                return role.getId();
            }
            if (role.getLabel().equals("Interne")) {
               intern = role.getId();
            }
            if (role.getLabel().equals("Chantier")) {
                chantier = role.getId();
            }
        }

        if (intern != null) return intern;

        return chantier;
    }

    public List<Role> getRoles(User user) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT r.* " +
                             "FROM bc_role r " +
                             "INNER JOIN bc_user_role ur ON ur.role_id = r.id " +
                             "INNER JOIN bc_user u ON u.id = ur.user_id " +
                             "WHERE u.id = ?;"))
        {
            ps.setLong(1, user.getId());
            rs = ps.executeQuery();
            List<Role> roles = new ArrayList<>();

            while (rs.next()) {
                roles.add(new Role()
                        .setId(rs.getLong("id"))
                        .setLabel(rs.getString("label"))
                );
            }

            return roles;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public List<Demand> getDemands(User user) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT d.* " +
                             "FROM bc_demand d " +
                             "INNER JOIN bc_team t ON t.demand_id = d.id " +
                             "INNER JOIN bc_user u ON u.id = t.user_id " +
                             "WHERE u.id = ?;"))
        {
            ps.setLong(1, user.getId());
            rs = ps.executeQuery();
            List<Demand> demands = new ArrayList<>();

            while (rs.next()) {
                demands.add(new Demand()
                        .setId(rs.getString("id"))
                        .setDemandDate(rs.getTimestamp("demand_date"))
                        .setLongitude(rs.getFloat("longitude"))
                        .setLatitude(rs.getFloat("latitude"))
                        .setGant(rs.getString("gant"))
                        .setIncident(rs.getString("incident"))
                        .setStep(rs.getString("step"))
                        .setProject(new Project().setId(rs.getLong("project_id")))
                );
            }

            return demands;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public List<Site> getSites(User user) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT s.* " +
                             "FROM bc_site s " +
                             "INNER JOIN bc_user_site us ON us.site_id = s.id " +
                             "INNER JOIN bc_user u ON u.id = us.user_id " +
                             "WHERE u.id = ?;"))
        {
            ps.setLong(1, user.getId());
            rs = ps.executeQuery();
            List<Site> sites = new ArrayList<>();

            while (rs.next()) {
                sites.add(new Site()
                        .setId(rs.getLong("id"))
                        .setName(rs.getString("name"))
                );
            }

            return sites;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
