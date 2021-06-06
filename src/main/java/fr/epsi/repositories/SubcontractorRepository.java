package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.Demand;
import fr.epsi.entities.Profession;
import fr.epsi.entities.Project;
import fr.epsi.entities.Subcontractor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubcontractorRepository implements Repository<Subcontractor, Long> {
    @Override
    public Subcontractor findById(Long id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM bc_subcontractor WHERE id = ?;"))
        {
            ps.setLong(1, id);
            rs = ps.executeQuery();
            Subcontractor subcontractor = null;

            if (rs.next()) {
                subcontractor = this.setData(rs);
            }

            return subcontractor;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<Subcontractor> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_subcontractor;"))
        {
            List<Subcontractor> subcontractors = new ArrayList<>();

            while (rs.next()) {
                subcontractors.add(this.setData(rs));
            }

            return subcontractors;
        }
    }

    @Override
    public Subcontractor create(Subcontractor subcontractor) throws SQLException {
        try (
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO bc_subcontractor(name, address, postal, city) VALUES (?,?,?,?);",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, subcontractor.getName());
            ps.setString(2, subcontractor.getAddress());
            ps.setString(3, subcontractor.getPostal());
            ps.setString(4, subcontractor.getCity());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating subcontractor failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    subcontractor.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating subcontractor failed, no ID obtained.");
                }
            }
        }

        return findById(subcontractor.getId());
    }

    @Override
    public Subcontractor update(Subcontractor subcontractor) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE bc_subcontractor SET name = ?, address = ?, postal = ?, city = ? WHERE id = ?;"
             ))
        {
            ps.setString(1, subcontractor.getName());
            ps.setString(2, subcontractor.getAddress());
            ps.setString(3, subcontractor.getPostal());
            ps.setString(4, subcontractor.getCity());
            ps.setLong(5, subcontractor.getId());

            ps.executeUpdate();

            return findById(subcontractor.getId());
        }
    }

    @Override
    public void delete(Subcontractor subcontractor) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_subcontractor WHERE id = ?;"))
        {
            ps.setLong(1, subcontractor.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Subcontractor setData(ResultSet rs) throws SQLException {
        Subcontractor subcontractor = new Subcontractor()
                .setId(rs.getLong("id"))
                .setName(rs.getString("name"))
                .setAddress(rs.getString("address"))
                .setPostal(rs.getString("postal"))
                .setCity(rs.getString("city"));

        subcontractor.setDemands(getDemands(subcontractor));
        subcontractor.setProfessions(getProfessions(subcontractor));

        return subcontractor;
    }

    public List<Demand> getDemands(Subcontractor subcontractor) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT d.* " +
                             "FROM bc_demand d " +
                             "INNER JOIN bc_demand_subcontractor ds ON ds.demand_id = d.id " +
                             "INNER JOIN bc_subcontractor s ON ds.subcontractor_id = s.id " +
                             "WHERE s.id = ?;"))
        {
            ps.setLong(1, subcontractor.getId());
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

    public List<Profession> getProfessions(Subcontractor subcontractor) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT p.* " +
                             "FROM bc_profession p " +
                             "INNER JOIN bc_subcontractor_profession sp ON sp.profession_id = p.id " +
                             "INNER JOIN bc_subcontractor s ON sp.subcontractor_id = s.id " +
                             "WHERE s.id = ?;"))
        {
            ps.setLong(1, subcontractor.getId());
            rs = ps.executeQuery();
            List<Profession> professions = new ArrayList<>();

            while (rs.next()) {
                professions.add(new Profession()
                        .setId(rs.getLong("id"))
                        .setLabel(rs.getString("label"))
                );
            }

            return professions;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
