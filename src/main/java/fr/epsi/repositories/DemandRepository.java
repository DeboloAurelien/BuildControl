package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandRepository implements Repository<Demand, String> {
    @Override
    public Demand findById(String id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM bc_demand WHERE id = ?;"))
        {
            ps.setString(1, id);
            rs = ps.executeQuery();
            Demand demand = null;

            if (rs.next()) {
                demand = this.setData(rs);
            }

            return demand;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<Demand> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_demand;"))
        {
            List<Demand> demands = new ArrayList<>();

            while (rs.next()) {
                demands.add(this.setData(rs));
            }

            return demands;
        }
    }

    @Override
    public Demand create(Demand demand) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO bc_demand VALUES (?,?,?,?,?,?,?,?);",
                     Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, demand.getId());
            ps.setTimestamp(2, demand.getDemandDate());
            ps.setFloat(3, demand.getLongitude());
            ps.setFloat(4, demand.getLatitude());
            ps.setString(5, demand.getGant());
            ps.setString(6, demand.getIncident());
            ps.setString(7, demand.getStep());
            ps.setLong(8, demand.getProject().getId());

            int rowAffected = ps.executeUpdate();

            if (rowAffected == 0) {
                throw new SQLException("Creating demand failed, no rows affected.");
            }
        }

        insertUsers(demand);
        insertSubcontractors(demand);

        return findById(demand.getId());
    }

    @Override
    public Demand update(Demand demand) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE bc_demand " +
                             "SET demand_date = ?, " +
                             "longitude = ?, " +
                             "latitude = ?, " +
                             "gant = ?, " +
                             "incident = ?, " +
                             "step = ?, " +
                             "project_id = ? " +
                             "WHERE id = ?;"))
        {
            ps.setTimestamp(1, demand.getDemandDate());
            ps.setFloat(2, demand.getLongitude());
            ps.setFloat(3, demand.getLatitude());
            ps.setString(4, demand.getGant());
            ps.setString(5, demand.getIncident());
            ps.setString(6, demand.getStep());
            ps.setLong(7, demand.getProject().getId());
            ps.setString(8, demand.getId());

            ps.executeUpdate();

            return findById(demand.getId());
        }
    }

    @Override
    public void delete(Demand demand) throws SQLException {
        if (demand == null) {
            throw new SQLException("Demand (id=null) not found in repository, maybe already removed ?");
        }
        if (findById(demand.getId()) == null) {
            throw new SQLException("Demand (id=" + demand.getId() + ") not found in repository, maybe already removed ?");
        }

        deleteUsers(demand);
        deleteMedias(demand);
        deleteSubcontractors(demand);

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_demand WHERE id = ?;"))
        {
            ps.setString(1, demand.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Demand setData(ResultSet rs) throws SQLException {
        Demand demand = new Demand()
                .setId(rs.getString("id"))
                .setDemandDate(rs.getTimestamp("demand_date"))
                .setLongitude(rs.getFloat("longitude"))
                .setLatitude(rs.getFloat("latitude"))
                .setGant(rs.getString("gant"))
                .setIncident(rs.getString("incident"))
                .setStep(rs.getString("step"))
                .setProject(new Project().setId(rs.getLong("project_id")));

        demand.setUsers(getUsers(demand));
        demand.setSubcontractors(getSubcontractors(demand));
        demand.setMedias(getMedias(demand));

        return demand;
    }

    public List<Demand> getDemandsAfterDate(Date date) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "EXEC GetDemandsAfterDate @date = ?;"))
        {
            ps.setString(1, date.toString());
            rs = ps.executeQuery();
            List<Demand> demands = new ArrayList<>();

            while (rs.next()) {
                demands.add(this.setData(rs));
            }

            return demands;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public List<User> getUsers(Demand demand) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT u.* " +
                             "FROM bc_user u " +
                             "INNER JOIN bc_team t ON u.id = t.user_id " +
                             "INNER JOIN bc_demand d ON t.demand_id = d.id " +
                             "WHERE d.id = ?;"))
        {
            ps.setString(1, demand.getId());
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

    public void insertUsers(Demand demand) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC LinkUserDemand @userID = ?, @demandID = ?;"))
        {
            for (User user : demand.getUsers()) {
                ps.setLong(1, user.getId());
                ps.setString(2, demand.getId());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void deleteUsers(Demand demand) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC RemoveUserDemand @userID = ?, @demandID = ?;"))
        {
            for (User user : demand.getUsers()) {
                ps.setLong(1, user.getId());
                ps.setString(2, demand.getId());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Subcontractor> getSubcontractors(Demand demand) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT s.* " +
                             "FROM bc_subcontractor s " +
                             "INNER JOIN bc_demand_subcontractor ds ON s.id = ds.subcontractor_id " +
                             "INNER JOIN bc_demand d ON ds.demand_id = d.id " +
                             "WHERE d.id = ?;"))
        {
            ps.setString(1, demand.getId());
            rs = ps.executeQuery();
            List<Subcontractor> subcontractors = new ArrayList<>();

            while (rs.next()) {
                subcontractors.add(new Subcontractor()
                        .setId(rs.getLong("id"))
                        .setName(rs.getString("name"))
                        .setAddress(rs.getString("address"))
                        .setPostal(rs.getString("postal"))
                        .setCity(rs.getString("city")));
            }

            return subcontractors;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public void insertSubcontractors(Demand demand) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC LinkDemandSubcontractor @demandID = ?, @subcontractorID = ?;"))
        {
            for (Subcontractor subcontractor : demand.getSubcontractors()) {
                ps.setString(1, demand.getId());
                ps.setLong(2, subcontractor.getId());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void deleteSubcontractors(Demand demand) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC RemoveDemandSubcontractor @demandID = ?, @subcontractorID = ?;"))
        {
            for (Subcontractor subcontractor : demand.getSubcontractors()) {
                ps.setString(1, demand.getId());
                ps.setLong(2, subcontractor.getId());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Media> getMedias(Demand demand) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT m.* " +
                             "FROM bc_media m " +
                             "INNER JOIN bc_demand d ON d.id = m.demand_id " +
                             "WHERE d.id = ?;"))
        {
            ps.setString(1, demand.getId());
            rs = ps.executeQuery();
            List<Media> medias = new ArrayList<>();

            while (rs.next()) {
                medias.add(new Media()
                        .setId(rs.getLong("id"))
                        .setPath(rs.getString("path"))
                        .setDemand(demand)
                        .setMediaType(new MediaType().setId(rs.getLong("media_type_id")))
                );
            }

            return medias;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public void deleteMedias(Demand demand) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_media WHERE demand_id = ?;"))
        {
            ps.setString(1, demand.getId());

            ps.executeUpdate();
        }
    }
}
