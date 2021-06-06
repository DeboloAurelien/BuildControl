package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.Privilege;
import fr.epsi.entities.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrivilegeRepository implements Repository<Privilege, Long> {
    @Override
    public Privilege findById(Long id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM bc_privilege WHERE id = ?;"))
        {
            ps.setLong(1, id);
            rs = ps.executeQuery();
            Privilege privilege = null;

            if (rs.next()) {
                privilege = this.setData(rs);
            }

            return privilege;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<Privilege> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_privilege;"))
        {
            List<Privilege> privileges = new ArrayList<>();

            while (rs.next()) {
                privileges.add(this.setData(rs));
            }

            return privileges;
        }
    }

    @Override
    public Privilege create(Privilege privilege) throws SQLException {
        try (
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO bc_privilege(label) VALUES (?);",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, privilege.getLabel());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating privilege failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    privilege.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating privilege failed, no ID obtained.");
                }
            }
        }

        return findById(privilege.getId());
    }

    @Override
    public Privilege update(Privilege privilege) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE bc_privilege SET label = ? WHERE id = ?;"))
        {
            ps.setString(1, privilege.getLabel());
            ps.setLong(2, privilege.getId());

            ps.executeUpdate();

            return findById(privilege.getId());
        }
    }

    @Override
    public void delete(Privilege privilege) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_privilege WHERE id = ?;"))
        {
            ps.setLong(1, privilege.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Privilege setData(ResultSet rs) throws SQLException {
        Privilege privilege = new Privilege()
                .setId(rs.getLong("id"))
                .setLabel(rs.getString("label"));

        privilege.setRoles(getRoles(privilege));

        return privilege;
    }

    public List<Role> getRoles(Privilege privilege) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT r.* " +
                             "FROM bc_role r " +
                             "INNER JOIN bc_role_privilege rp ON r.id = rp.role_id " +
                             "INNER JOIN bc_privilege p ON p.id = rp.privilege_id " +
                             "WHERE p.id = ?;"))
        {
            ps.setLong(1, privilege.getId());
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
}
