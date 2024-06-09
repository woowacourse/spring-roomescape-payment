package roomescape.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.service.dto.request.ReservationCreateRequest;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.ReservationFixture;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationManageServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationManageService reservationManageService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    private Member member;

    private Theme theme;

    private ReservationTime time;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MemberFixture.user());
        theme = themeRepository.save(ThemeFixture.theme());
        time = reservationTimeRepository.save(ReservationTimeFixture.ten());
    }

    @Test
    @DisplayName("예약을 추가한다.")
    void addReservation() {
        ReservationCreateRequest request = ReservationFixture.createValidRequest(member.getId(), time.getId(), theme.getId());

        Reservation reservation = reservationManageService.addReservation(request);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservation.getDate()).isEqualTo(request.date());
            softly.assertThat(reservation.getMember().getName()).isEqualTo(member.getName());
            softly.assertThat(reservation.getTheme().getRawName()).isEqualTo(theme.getRawName());
            softly.assertThat(reservation.getTime().getStartAt()).isEqualTo(time.getStartAt());
        });
    }

    @Test
    @DisplayName("예약을 취소하면 취소 상태가 된다.")
    void deleteReservationById() {
        Reservation reservation = reservationRepository.save(ReservationFixture.create(member, time, theme));
        long id = reservation.getId();

        Reservation canceledReservation = reservationManageService.cancel(id);

        assertThat(canceledReservation.isCanceled()).isTrue();
    }

    @Test
    @DisplayName("취소한 예약을 롤백하면 다시 확정 상태가 된다.")
    void rollbackReservationById() {
        Reservation reservation = reservationRepository.save(ReservationFixture.create(member, time, theme));
        long id = reservation.getId();
        reservationManageService.cancel(id);

        reservationManageService.rollbackCancellation(reservation);

        Reservation rollbackedReservation = reservationRepository.findById(id).orElseThrow();
        assertThat(rollbackedReservation.isCanceled()).isFalse();
    }
}
