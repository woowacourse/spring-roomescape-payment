package roomescape.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import roomescape.helper.CookieProvider;
import roomescape.helper.DatabaseCleaner;
import roomescape.helper.domain.MemberFixture;
import roomescape.helper.domain.PaymentFixture;
import roomescape.helper.domain.ReservationFixture;
import roomescape.helper.domain.ReservationTimeFixture;
import roomescape.helper.domain.ThemeFixture;
import roomescape.helper.domain.WaitingFixture;
import roomescape.service.payment.PaymentClient;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTest {
    @LocalServerPort
    int port;

    @MockBean
    protected PaymentClient paymentClient;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    protected CookieProvider cookieProvider;

    @Autowired
    protected MemberFixture memberFixture;

    @Autowired
    protected ReservationFixture reservationFixture;

    @Autowired
    protected ReservationTimeFixture timeFixture;

    @Autowired
    protected ThemeFixture themeFixture;

    @Autowired
    protected WaitingFixture waitingFixture;

    @Autowired
    protected PaymentFixture paymentFixture;

    @MockBean
    protected Clock clock;

    @BeforeEach
    protected void setUp() {
        RestAssured.port = port;
        databaseCleaner.execute();
        given(clock.instant()).willReturn(Instant.parse("2000-04-07T02:00:00Z"));
        given(clock.getZone()).willReturn(ZoneOffset.UTC);
        subclassSetUp();
    }

    protected void subclassSetUp() {
    }
}
