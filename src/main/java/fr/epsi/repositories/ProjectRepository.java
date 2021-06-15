package fr.epsi.repositories;

import fr.epsi.DBConnection;
import fr.epsi.entities.Demand;
import fr.epsi.entities.Project;
import fr.epsi.entities.ProjectSubcontractor;
import fr.epsi.entities.Site;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepository implements Repository<Project, Long> {
    @Override
    public Project findById(Long id) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM bc_project WHERE id = ?;"))
        {
            ps.setLong(1, id);
            rs = ps.executeQuery();
            Project project = null;

            if (rs.next()) {
                project = this.setData(rs);
            }

            return project;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    @Override
    public List<Project> findAll() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bc_project;"))
        {
            List<Project> projects = new ArrayList<>();

            while (rs.next()) {
                projects.add(this.setData(rs));
            }

            return projects;
        }
    }

    @Override
    public Project create(Project project) throws SQLException {
        try (
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO bc_project (name, site_id, start_date, end_date) VALUES(?,?,?,?);",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, project.getName());
            ps.setLong(2, project.getSite().getId());
            ps.setDate(3, project.getStartDate());
            ps.setDate(4, project.getEndDate());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating project failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getLong(1));
                }
                else {
                    throw new SQLException("Creating project failed, no ID obtained.");
                }
            }
        }

        return findById(project.getId());
    }

    @Override
    public Project update(Project project) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE bc_project SET name = ?, site_id = ?, start_date = ?, end_date = ? WHERE id = ?;"
             ))
        {
            ps.setString(1, project.getName());
            ps.setLong(2, project.getSite().getId());
            ps.setDate(3, project.getStartDate());
            ps.setDate(4, project.getEndDate());

            ps.executeUpdate();

            return findById(project.getId());
        }
    }

    @Override
    public void delete(Project project) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM bc_project WHERE id = ?"))
        {
            ps.setLong(1, project.getId());
            ps.executeUpdate();
        }
    }

    public Long getNbFinishedProject(int year, int month) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC NbFinishedProject @year = ?, @month = ?;"))
        {
            ps.setLong(1, year);
            ps.setLong(2, month);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }

            throw new SQLException("ProjectRepository - getNbProjectsFinished - no result");
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public Long getNbFinishedProjectPeriod(Timestamp startDate, Timestamp endDate) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT dbo.getNbFinishedProjectPeriod (?,?);"))
        {
            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }

            throw new SQLException("ProjectRepository - getNbProjectsFinished - no result");
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public List<ProjectSubcontractor> getNbSubcontractorByProject() throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("EXEC NbSubcontractorByProject;");
             ResultSet rs = ps.executeQuery())
        {
            List<ProjectSubcontractor> projects = new ArrayList<>();

            while (rs.next()) {
                projects.add(new ProjectSubcontractor()
                        .setName(rs.getString("name"))
                        .setLabel(rs.getString("label"))
                        .setNbSubcontractors(rs.getLong("nombre de sous-traitant")));
            }

            return projects;
        }
    }

    @Override
    public Project setData(ResultSet rs) throws SQLException {
        Project project = new Project()
                .setId(rs.getLong("id"))
                .setName(rs.getString("name"))
                .setStartDate(rs.getDate("start_date"))
                .setEndDate(rs.getDate("end_date"))
                .setSite(new Site().setId(rs.getLong("site_id")));

        project.setDemands(getDemands(project));

        return project;
    }

    public List<Demand> getDemands(Project project) throws SQLException {
        ResultSet rs = null;

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT d.* " +
                             "FROM bc_demand d " +
                             "INNER JOIN bc_project p ON p.id = d.project_id " +
                             "WHERE p.id = ?;"))
        {
            ps.setLong(1, project.getId());
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
                        .setProject(project)
                );
            }

            return demands;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
