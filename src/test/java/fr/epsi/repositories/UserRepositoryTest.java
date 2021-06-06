package fr.epsi.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.epsi.entities.*;
import org.junit.Test;

public class UserRepositoryTest {
    private final DemandRepository demandRepository = new DemandRepository();
    private final ProfessionRepository professionRepository = new ProfessionRepository();
    private final RoleRepository roleRepository = new RoleRepository();
    private final SiteRepository siteRepository = new SiteRepository();
    private final UserRepository userRepository = new UserRepository();

    @Test
    public void shouldReturnAllUsers() throws SQLException {
        List<User> users = userRepository.findAll();
        assertThat(users.size()).isGreaterThan(0);

        for (User user : users) {
            assertThat(user.getId()).isNotNull();
            assertThat(user.getId()).isPositive();
        }
    }

    @Test
    public void shouldReturnOneUserById() throws SQLException {
        List<User> users = userRepository.findAll();

        assertThat(users).isNotEmpty();

        for (User userFromFindAll : users) {
            User userFromFindById = userRepository.findById(userFromFindAll.getId());

            assertThat(userFromFindAll.getId()).isEqualTo(userFromFindById.getId());
            assertThat(userFromFindAll.getUsername()).isEqualTo(userFromFindById.getUsername());
        }
    }

    @Test
    public void shouldCreateUserAndLinkForeignKeys() throws SQLException {
        int year = Calendar.getInstance().get(Calendar.YEAR);

        assertThat(demandRepository.findAll().size()).isGreaterThan(0);
        List<Demand> demands = new ArrayList<>();
        demands.add(demandRepository.findAll().get(0));

        assertThat(siteRepository.findAll().size()).isGreaterThan(0);
        List<Site> sites = new ArrayList<>();
        sites.add(siteRepository.findAll().get(0));

        assertThat(professionRepository.findAll().size()).isGreaterThan(0);
        Profession profession = professionRepository.findAll().get(0);

        assertThat(roleRepository.findById(3L)).isNotNull();
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(3L)); // Chantier => C

        User newUser = userRepository.create(
                new User()
                        .setFirstname("aaaaa")
                        .setLastname("bbbbb")
                        .setPasswordHash("password")
                        .setProfession(profession)
                        .setRoles(roles)
                        .setDemands(demands)
                        .setSites(sites));

        // Check user's data
        assertThat(newUser).isNotNull();
        assertThat(newUser.getId()).isPositive();
        assertThat(newUser.getFirstname()).isEqualTo("aaaaa");
        assertThat(newUser.getLastname()).isEqualTo("bbbbb");
        assertThat(newUser.getUsername()).containsSequence("C" + year + "_BBAA");
        assertThat(newUser.getPasswordHash()).isNotNull();
        assertThat(newUser.getPasswordHash()).isNotEqualTo("password");
        assertThat(newUser.getDateLastConnection()).isNull();
        assertThat(newUser.getNumberConnectionAttempt()).isEqualTo(0);
        assertThat(newUser.getArchive()).isFalse();

        // Check foreign keys
        assertThat(newUser.getProfession().getId()).isEqualTo(profession.getId());
        assertThat(newUser.getRoles().size()).isEqualTo(1);
        assertThat(newUser.getRoles().get(0).getId()).isEqualTo(3L);
        assertThat(newUser.getDemands().size()).isEqualTo(1);
        assertThat(newUser.getSites().size()).isEqualTo(1);
    }

    @Test
    public void shouldCreateUniqueUsername() throws SQLException {
        List<User> usersBeforeAddingNewUser = userRepository.findAll();

        assertThat(roleRepository.findAll().size()).isGreaterThan(0);
        assertThat(siteRepository.findAll().size()).isGreaterThan(0);

        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(3L)); // Chantier => C
        List<Demand> demands = new ArrayList<>();
        demands.add(demandRepository.findAll().get(0));
        List<Site> sites = new ArrayList<>();
        sites.add(siteRepository.findAll().get(0));

        User newUser = userRepository.create(
                new User()
                        .setFirstname("aaaaa")
                        .setLastname("bbbbb")
                        .setProfession(new Profession().setId(3L)) // Plomberie
                        .setRoles(roles)
                        .setDemands(demands)
                        .setSites(sites));

        for (User user : usersBeforeAddingNewUser) {
            assertThat(user.getUsername()).isNotEqualTo(newUser.getUsername());
        }
    }

    @Test
    public void shouldReturnUpdatedUser() throws SQLException {
        assertThat(roleRepository.findAll().size()).isGreaterThan(0);
        assertThat(demandRepository.findAll().size()).isGreaterThan(0);
        assertThat(siteRepository.findAll().size()).isGreaterThan(0);
        assertThat(professionRepository.findAll().size()).isGreaterThan(1);

        Profession profession0 = professionRepository.findAll().get(0);
        Profession profession1 = professionRepository.findAll().get(1);

        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findAll().get(0));
        List<Demand> demands = new ArrayList<>();
        demands.add(demandRepository.findAll().get(0));
        List<Site> sites = new ArrayList<>();
        sites.add(siteRepository.findAll().get(0));

        User newUser = userRepository.create(
                new User()
                        .setFirstname("aaaaa")
                        .setLastname("bbbbb")
                        .setProfession(profession0)
                        .setRoles(roles)
                        .setDemands(demands)
                        .setSites(sites));

        newUser.setUsername("TEST")
            .setDateLastConnection(new Timestamp(System.currentTimeMillis()))
            .setNumberConnectionAttempt(47L)
            .setPasswordHash("My new password")
            .setFirstname("5411561vd65v51df6v1fv6sd5v1")
            .setLastname("ffzefezfzef411545zefzerfcdza")
            .setArchive(true)
            .setProfession(profession1);

        User updatedUser = userRepository.update(newUser);
        assertThat(updatedUser.getPasswordHash()).isNotEqualTo(newUser.getPasswordHash());
        updatedUser.setPasswordHash("My new password");

        // When user created, this.pwd == this.username
        updatedUser = userRepository.updatePassword(updatedUser, newUser.getUsername());

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isNotNull();
        assertThat(updatedUser.getUsername()).isNotEqualTo("TEST");
        assertThat(updatedUser.getPasswordHash()).isNotEqualTo("nouveau mot de passe");
        assertThat(updatedUser.getPasswordHash()).isNotEqualTo("mot de passe incorrect");
        assertThat(updatedUser.getPasswordHash()).isNotEqualTo(newUser.getPasswordHash());
        assertThat(updatedUser.getNumberConnectionAttempt()).isEqualTo(47L);
        assertThat(updatedUser.getFirstname()).isNotEqualTo("5411561vd65v51df6v1fv6sd5v1");
        assertThat(updatedUser.getLastname()).isNotEqualTo("ffzefezfzef411545zefzerfcdza");
        assertThat(updatedUser.getArchive()).isEqualTo(true);
        assertThat(updatedUser.getProfession().getId()).isEqualTo(profession1.getId());
    }

    @Test
    public void shouldRemoveUser() throws SQLException {
        User user = null;

        for (User userFromRepo : userRepository.findAll()) {
            if (userFromRepo.getRoles().size() > 0 && userFromRepo.getDemands().size() > 0 && userFromRepo.getSites().size() > 0) {
                user = userFromRepo;
            }
        }

        assertThat(user).isNotNull();

        userRepository.delete(user);

        assertThat(userRepository.findById(user.getId())).isNull();

        for (Demand demand : user.getDemands()) {
            if (demand.getUsers() != null) {
                for (User userFromDemand : demand.getUsers()) {
                    assertThat(userRepository.findById(userFromDemand.getId()).getId()).isNotEqualTo(user.getId());
                }
            }
        }

        for (Role role : roleRepository.findAll()) {
            if (role.getUsers() != null) {
                for (User userFromRole : role.getUsers()) {
                    assertThat(userRepository.findById(userFromRole.getId()).getId()).isNotEqualTo(user.getId());
                }
            }
        }

        for (Site site : siteRepository.findAll()) {
            if (site.getUsers() != null) {
                for (User userFromSite : site.getUsers()) {
                    assertThat(userRepository.findById(userFromSite.getId()).getId()).isNotEqualTo(user.getId());
                }
            }
        }
    }
}
