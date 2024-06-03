package roomescape.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class RepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    @AfterEach
    void applyDatabase() {
        entityManager.flush();
    }
}
