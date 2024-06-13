package roomescape.service;

import static org.mockito.BDDMockito.given;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import roomescape.helper.CookieProvider;
import roomescape.helper.DatabaseCleaner;
import roomescape.helper.domain.DomainSupplier;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
abstract class ServiceTest extends DomainSupplier {
    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    protected CookieProvider cookieProvider;

    @MockBean
    protected Clock clock;

    @BeforeEach
    protected void setUp() {
        databaseCleaner.execute();
        given(clock.instant()).willReturn(Instant.parse("2000-04-07T02:00:00Z"));
        given(clock.getZone()).willReturn(ZoneOffset.UTC);
    }
}
