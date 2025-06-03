package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createTimeAt_10;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.dto.WaitingReservationRequest;
import roomescape.reservation.dto.WaitingReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
@Import({WaitingReservationService.class, DBHelper.class})
class WaitingReservationServiceTest {

    @Autowired
    private WaitingReservationRepository waitingReservationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private WaitingReservationService service;

    @Autowired
    DBHelper dbHelper;

    @Test
    void 대기_예약이_정상적으로_저장된다() {
        // given
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        WaitingReservationRequest request = new WaitingReservationRequest(
                DEFAULT_DATE,
                time.getId(),
                theme.getId()
        );
        Member member = dbHelper.insertMember(createDefaultMember_1());
        LoginMember loginMember = LoginMember.from(member);

        // when
        WaitingReservationResponse response = service.registerWaitingReservation(request, loginMember);

        // then
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(response.id()).isNotNull();
            soft.assertThat(response.date()).isEqualTo(request.date());
            soft.assertThat(response.member().name()).isEqualTo(member.getName());
            soft.assertThat(response.theme().name()).isEqualTo(theme.getName());
        });
    }

    @Test
    void 존재하지_않는_회원으로_대기_예약시_예외발생() {
        // given
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        WaitingReservationRequest request = new WaitingReservationRequest(
                DEFAULT_DATE,
                time.getId(),
                theme.getId()
        );

        LoginMember nonExistentMember = new LoginMember(9999L, "존재안함", "none@example.com", MemberRole.MEMBER);

        // when & then
        assertThatThrownBy(() -> service.registerWaitingReservation(request, nonExistentMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 멤버입니다.");
    }
} 
