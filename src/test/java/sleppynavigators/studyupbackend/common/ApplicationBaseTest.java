package sleppynavigators.studyupbackend.common;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationBaseTest extends IntegrationBaseTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        // Clean the database before each test
        databaseCleaner.execute();
    }
}
