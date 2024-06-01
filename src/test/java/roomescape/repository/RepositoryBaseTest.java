package roomescape.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.BaseTest;

@Sql("/test-data.sql")
@DataJpaTest
public abstract class RepositoryBaseTest extends BaseTest {
}
