package roomescape.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import roomescape.BaseTest;

@Sql("/test-data.sql")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class ServiceBaseTest extends BaseTest {
}
