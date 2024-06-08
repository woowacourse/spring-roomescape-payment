package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.domain.reservation.Status.CANCELED;
import static roomescape.domain.reservation.Status.PAYMENT_PENDING;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.domain.reservation.Status.WAITING;
import static roomescape.support.fixture.MemberFixture.MEMBER_JAZZ;
import static roomescape.support.fixture.MemberFixture.MEMBER_SUN;
import static roomescape.support.fixture.ThemeFixture.THEME_BED;
import static roomescape.support.fixture.TimeFixture.ONE_PM;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.exception.reservation.NotFoundReservationException;
import roomescape.infrastructure.repository.MemberRepository;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.infrastructure.repository.ReservationTimeRepository;
import roomescape.infrastructure.repository.ThemeRepository;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class,
})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CancelServiceTest {

    @Autowired
    private CancelService cancelService;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ReservationRepository reservationRepository;

    Reservation reservation(Member member, Theme theme, String date, ReservationTime time, Status status) {
        return new Reservation(member, theme, LocalDate.parse(date), time, status);
    }

    @DisplayName("존재하지 않는 예약을 취소하려하면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_cancel_not_exists_reservation() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);
        MemberInfo memberInfo = new MemberInfo(jazz.getId(), jazz.getName());

        reservationRepository.save(reservation(jazz, bed, date.toString(), onePm, RESERVED));

        assertThatThrownBy(() -> cancelService.cancelReservation(2L, memberInfo))
                .isInstanceOf(NotFoundReservationException.class);
    }

    @DisplayName("예약을 취소하고 다음 예약 대기가 존재할 시 예약 대기의 상태를 결제 대기 상태로 전환한다.")
    @Test
    void cancel_reservation_and_update_first_waiting_status_to_pending() {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Member sun = memberRepository.save(MEMBER_SUN.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);
        MemberInfo memberInfo = new MemberInfo(jazz.getId(), jazz.getName());

        Reservation reserved = reservationRepository.save(reservation(jazz, bed, date.toString(), onePm, RESERVED));
        reservationRepository.save(reservation(sun, bed, date.toString(), onePm, WAITING));

        cancelService.cancelReservation(reserved.getId(), memberInfo);

        Reservation canceledReservation = reservationRepository.getReservationById(1L);
        Reservation pendingReservation = reservationRepository.getReservationById(2L);

        assertAll(
                () -> assertThat(canceledReservation.getStatus()).isEqualTo(CANCELED),
                () -> assertThat(pendingReservation.getStatus()).isEqualTo(PAYMENT_PENDING)
        );
    }
}
