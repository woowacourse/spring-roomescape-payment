package roomescape.core.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import roomescape.config.TestConfig;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@ActiveProfiles("test")
@Import(TestConfig.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceTest {
}
