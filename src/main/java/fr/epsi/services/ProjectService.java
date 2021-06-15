package fr.epsi.services;

import fr.epsi.repositories.ProjectRepository;
import fr.epsi.entities.ProjectSubcontractor;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ProjectService {
    private final ProjectRepository projectRepository = new ProjectRepository();

    public Long getNbProjectsFinished(int year, int month) throws SQLException {
        return projectRepository.getNbFinishedProject(year, month);
    }

    public Long getNbProjectsFinishedPeriod(Timestamp startDate, Timestamp endDate) throws SQLException {
        return projectRepository.getNbFinishedProjectPeriod(startDate, endDate);
    }

    public List<ProjectSubcontractor> getNbSubcontractorByProject() throws SQLException {
        return projectRepository.getNbSubcontractorByProject();
    }
}
