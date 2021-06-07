package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.Privilege;
import fr.epsi.entities.Profession;
import fr.epsi.entities.Role;
import fr.epsi.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository implements Repository<Role, Long> {
    @Override
    public Role findById(Long id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM bc_role WHERE id = ?;"))
        {
            ps.setLong(1, id);
            rs = ps.executeQuery();
            Role role = null;

            if (rs.next()) {
                role = this.setData(rs);
            }

            return role;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<Role> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_role;"))
        {
            List<Role> roles = new ArrayList<>();

            while (rs.next()) {
                roles.add(this.setData(rs));
            }

            return roles;
        }
    }

    @Override
    public Role create(Role role) throws SQLException {
        try (
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO bc_role(label) VALUES(?);",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, role.getLabel());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating role failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    role.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating role failed, no ID obtained.");
                }
            }
        }

        return findById(role.getId());
    }

    @Override
    public Role update(Role role) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE bc_role SET label = ? WHERE id = ?;"))
        {
            ps.setString(1, role.getLabel());

            ps.executeUpdate();

            return findById(role.getId());
        }
    }

    @Override
    public void delete(Role role) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_role WHERE id = ?;"))
        {
            ps.setLong(1, role.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Role setData(ResultSet rs) throws SQLException {
        Role role = new Role()
                .setId(rs.getLong("id"))
                .setLabel(rs.getString("label"));

        role.setUsers(getUsers(role));
        role.setPrivileges(getPrivileges(role));

        return role;
    }

    public List<User> getUsers(Role role) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT u.* " +
                             "FROM bc_user u " +
                             "INNER JOIN bc_user_role ur ON u.id = ur.user_id " +
                             "INNER JOIN bc_role r ON r.id = ur.role_id " +
                             "WHERE r.id = ?;"))
        {
            ps.setLong(1, role.getId());
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

    public List<Privilege> getPrivileges(Role role) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT p.* " +
                             "FROM bc_privilege p " +
                             "INNER JOIN bc_role_privilege rp ON p.id = rp.privilege_id " +
                             "INNER JOIN bc_role r ON r.id = rp.role_id " +
                             "WHERE r.id = ?;"))
        {
            ps.setLong(1, role.getId());
            rs = ps.executeQuery();
            List<Privilege> privileges = new ArrayList<>();

            while (rs.next()) {
                privileges.add(new Privilege()
                        .setId(rs.getLong("id"))
                        .setLabel(rs.getString("label"))
                );
            }

            return privileges;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
