package roomescape.application;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import roomescape.TestConfig;
import roomescape.support.DatabaseCleanerExtension;

@ExtendWith(DatabaseCleanerExtension.class)
@SpringBootTest(
        classes = TestConfig.class,
        webEnvironment = WebEnvironment.NONE
)
public abstract class BaseServiceTest {
}
