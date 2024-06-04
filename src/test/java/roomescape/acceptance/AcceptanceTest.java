package roomescape.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import roomescape.auth.TokenProvider;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.fixture.*;
import roomescape.util.DatabaseCleanerExtension;
import roomescape.util.PaymentClientTestConfiguration;

@ExtendWith(DatabaseCleanerExtension.class)
@Import(PaymentClientTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    protected Member guest;
    protected Member admin;
    protected String guestToken;
    protected String adminToken;
    protected ReservationDetail reservationDetail;
    @LocalServerPort
    private int port;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;

        admin = memberRepository.save(MemberFixture.createAdmin());
        guest = memberRepository.save(MemberFixture.createGuest());

        adminToken = tokenProvider.create(admin);
        guestToken = tokenProvider.create(guest);

        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        reservationDetail = ReservationDetailFixture.create(theme, schedule);
    }
}
