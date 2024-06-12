package roomescape.infrastructure.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.domain.reservation.Status.CANCELED;
import static roomescape.domain.reservation.Status.PAYMENT_PENDING;
import static roomescape.domain.reservation.Status.WAITING;
import static roomescape.support.fixture.MemberFixture.MEMBER_JAZZ;
import static roomescape.support.fixture.MemberFixture.MEMBER_SUN;
import static roomescape.support.fixture.ThemeFixture.THEME_BED;
import static roomescape.support.fixture.TimeFixture.ONE_PM;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.infrastructure.repository.MemberRepository;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.infrastructure.repository.ReservationTimeRepository;
import roomescape.infrastructure.repository.ThemeRepository;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest
class ReservationEventHandlerTest {

    @Autowired
    private ReservationEventHandler eventHandler;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("payment-timeout", () -> 1);
    }

    Reservation reservation(Member member, Theme theme, String date, ReservationTime time, Status status) {
        return new Reservation(member, theme, LocalDate.parse(date), time, status);
    }

    @DisplayName("이벤트가 발행되면 결제 대기중인 예약의 상태를 취소로 변경하고, 다음 대기 예약을 결제 대기 상태로 변경한다.")
    @Test
    void pp() throws InterruptedException {
        Member jazz = memberRepository.save(MEMBER_JAZZ.create());
        Member sun = memberRepository.save(MEMBER_SUN.create());
        Theme bed = themeRepository.save(THEME_BED.create());
        ReservationTime onePm = timeRepository.save(ONE_PM.create());
        LocalDate date = LocalDate.now().plusDays(1);

        Reservation pending = reservationRepository.save(
                reservation(jazz, bed, date.toString(), onePm, PAYMENT_PENDING));
        Reservation waiting = reservationRepository.save(reservation(sun, bed, date.toString(), onePm, WAITING));

        eventHandler.handlePaymentPendingEvent(new PaymentPendingEvent(pending.getId()));

        TimeUnit.SECONDS.sleep(2);

        Reservation updatedPending = reservationRepository.getReservationById(pending.getId());
        Reservation updatedWaiting = reservationRepository.getReservationById(waiting.getId());

        assertAll(
                () -> assertThat(updatedPending.getStatus()).isEqualTo(CANCELED),
                () -> assertThat(updatedWaiting.getStatus()).isEqualTo(PAYMENT_PENDING)
        );
    }
}
