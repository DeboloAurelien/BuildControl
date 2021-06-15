package fr.epsi.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

import fr.epsi.entities.*;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DemandRepositoryTest {
    private final DemandRepository demandRepository = new DemandRepository();
    private final MediaRepository mediaRepository = new MediaRepository();
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final SubcontractorRepository subcontractorRepository = new SubcontractorRepository();
    private final UserRepository userRepository = new UserRepository();

    @Test
    public void shouldReturnAllDemands() throws SQLException {
        List<Demand> demands = demandRepository.findAll();

        assertThat(demands.size()).isGreaterThan(0);

        for (Demand demand : demands) {
            assertThat(demand.getId()).isNotNull() ;
        }
    }

    @Test
    public void shouldReturnOneDemandById() throws SQLException {
        List<Demand> demands = demandRepository.findAll();

        assertThat(demands).isNotEmpty();

        assertThat(demandRepository.findById(demands.get(0).getId())).isNotNull();
    }

    @Test
    public void shouldCreateDemandAndLinkForeignKeys() throws SQLException {
        assertThat(userRepository.findAll().size()).isGreaterThan(2);
        assertThat(projectRepository.findAll().size()).isGreaterThan(1);
        assertThat(subcontractorRepository.findAll().size()).isGreaterThan(1);

        Project project = projectRepository.findAll().get(0);

        List<User> users = new ArrayList<>();
        users.add(userRepository.findAll().get(0));
        users.add(userRepository.findAll().get(1));

        List<Subcontractor> subcontractors = new ArrayList<>();
        subcontractors.add(subcontractorRepository.findAll().get(0));

        String id = "#94099";
        GregorianCalendar cal = new GregorianCalendar(2021, Calendar.OCTOBER, 28);
        long millis = cal.getTimeInMillis();
        Timestamp date = new Timestamp(millis);

        Demand demand = new Demand()
                .setId(id)
                .setDemandDate(date)
                .setLongitude(179.123456F)
                .setLatitude(-179.123456F)
                .setGant("Initialisation")
                .setIncident("Perte matérielle")
                .setStep("Branchage")
                .setProject(project)
                .setSubcontractors(subcontractors)
                .setUsers(users);

        Demand demandCreated = demandRepository.create(demand);

        // Check demand data
        assertThat(demandCreated).isNotNull();
        assertThat(demandCreated.getId()).isEqualTo(demand.getId());
        assertThat(demandCreated.getDemandDate()).isEqualTo(demand.getDemandDate());
        assertThat(demandCreated.getLongitude()).isEqualTo(demand.getLongitude());
        assertThat(demandCreated.getLatitude()).isEqualTo(demand.getLatitude());
        assertThat(demandCreated.getGant()).isEqualTo(demand.getGant());
        assertThat(demandCreated.getIncident()).isEqualTo(demand.getIncident());
        assertThat(demandCreated.getStep()).isEqualTo(demand.getStep());

        // Check foreign keys
        assertThat(demandCreated.getProject().getId()).isEqualTo(demand.getProject().getId());
        assertThat(demandCreated.getUsers().size()).isEqualTo(demand.getUsers().size());
        assertThat(demandCreated.getSubcontractors().size()).isEqualTo(demand.getSubcontractors().size());
        assertThat(demandCreated.getMedias().size()).isEqualTo(0);
    }

    @Test
    public void shouldReturnUpdatedDemand() throws SQLException {
        Demand initialDemand = null;

        for (Demand demand : demandRepository.findAll()) {
            if (demand.getUsers() != null && demand.getSubcontractors() != null && demand.getMedias() != null) {
                initialDemand = demand;
                break;
            }
        }

        assertThat(initialDemand).isNotNull();

        GregorianCalendar cal = new GregorianCalendar(2021, Calendar.JUNE, 1);
        long millis = cal.getTimeInMillis();
        Timestamp date = new Timestamp(millis);

        Demand demandToUpdate = new Demand()
                .setId(initialDemand.getId())
                .setDemandDate(date)
                .setLongitude(140.1225F)
                .setLatitude(-3F)
                .setGant("New Gant")
                .setIncident("Branchage câbles ethernet")
                .setStep("Branchage");

        for (Project project : projectRepository.findAll()) {
            if (!project.getId().equals(initialDemand.getProject().getId())) {
                demandToUpdate.setProject(project);
                break;
            }
        }

        Demand demandUpdated = demandRepository.update(demandToUpdate);

        assertThat(demandUpdated).isNotNull();
        assertThat(demandUpdated.getId()).isEqualTo(initialDemand.getId());
        assertThat(demandUpdated.getDemandDate()).isEqualTo(date);
        assertThat(demandUpdated.getLongitude()).isEqualTo(demandToUpdate.getLongitude());
        assertThat(demandUpdated.getLatitude()).isEqualTo(demandToUpdate.getLatitude());
        assertThat(demandUpdated.getGant()).isEqualTo(demandToUpdate.getGant());
        assertThat(demandUpdated.getIncident()).isEqualTo(demandToUpdate.getIncident());
        assertThat(demandUpdated.getStep()).isEqualTo(demandToUpdate.getStep());
        assertThat(demandUpdated.getProject().getId()).isEqualTo(demandToUpdate.getProject().getId());

        assertThat(demandUpdated.getUsers().size()).isEqualTo(initialDemand.getUsers().size());
        assertThat(demandUpdated.getMedias().size()).isEqualTo(initialDemand.getMedias().size());
        assertThat(demandUpdated.getSubcontractors().size()).isEqualTo(initialDemand.getSubcontractors().size());
    }

    @Test
    public void shouldRemoveDemand() throws SQLException {
        Demand demand = null;

        for (Demand demandFromRepo : demandRepository.findAll()) {
            if (demandFromRepo.getUsers() != null && demandFromRepo.getSubcontractors() != null && demandFromRepo.getMedias() != null) {
                demand = demandFromRepo;
                break;
            }
        }

        assertThat(demand).isNotNull();

        demandRepository.delete(demand);

        assertThat(demandRepository.findById(demand.getId())).isNull();

        for (User user : userRepository.findAll()) {
            if (user.getDemands() != null) {
                for (Demand demandFromUser : user.getDemands()) {
                    assertThat(demandRepository.findById(demandFromUser.getId()).getId()).isNotEqualTo(demand.getId());
                }
            }
        }

        for (Subcontractor subcontractor : subcontractorRepository.findAll()) {
            if (subcontractor.getDemands() != null) {
                for (Demand demandFromUser : subcontractor.getDemands()) {
                    assertThat(demandRepository.findById(demandFromUser.getId()).getId()).isNotEqualTo(demand.getId());
                }
            }
        }

        for (Media media : mediaRepository.findAll()) {
            assertThat(media.getDemand().getId()).isNotEqualTo(demand.getId());
        }
    }
}
