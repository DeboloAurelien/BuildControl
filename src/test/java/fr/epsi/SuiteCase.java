package fr.epsi;

import fr.epsi.repositories.DemandRepositoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import fr.epsi.repositories.UserRepositoryTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserRepositoryTest.class,
        DemandRepositoryTest.class
})

public class SuiteCase {
}
