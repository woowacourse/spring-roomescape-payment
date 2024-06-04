package roomescape.service.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationDateFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.fixture.ThemeFixture;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql({"/truncate.sql"})
public abstract class ReservationServiceTest {

    protected ReservationDetail reservationDetail;
    protected Theme theme;
    protected Member admin;
    protected Member member;
    protected Member anotherMember;
    @Autowired
    protected ReservationTimeRepository reservationTimeRepository;
    @Autowired
    protected ThemeRepository themeRepository;
    @Autowired
    protected ReservationDetailRepository reservationDetailRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        admin = memberRepository.save(MemberFixture.createAdmin());
        member = memberRepository.save(MemberFixture.createGuest());
        anotherMember = memberRepository.save(MemberFixture.createGuest("pedro", "pedro@email.com", "pedro123"));

        ReservationTime time = reservationTimeRepository.save(ReservationTimeFixture.create10AM());
        theme = themeRepository.save(ThemeFixture.create());
        reservationDetail = reservationDetailRepository.save(new ReservationDetail(new Schedule(ReservationDateFixture.create(), time), theme));
    }
}
