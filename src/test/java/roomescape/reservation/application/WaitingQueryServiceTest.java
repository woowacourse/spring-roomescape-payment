package roomescape.reservation.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static roomescape.TestFixture.ADMIN_NAME;
import static roomescape.TestFixture.MIA_NAME;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_DATE;
import static roomescape.TestFixture.TOMMY_NAME;
import static roomescape.TestFixture.TOMMY_RESERVATION;
import static roomescape.TestFixture.TOMMY_RESERVATION_DATE;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;
import static roomescape.reservation.domain.ReservationStatus.PENDING_PAYMENT;
import static roomescape.reservation.domain.ReservationStatus.WAITING;

class WaitingQueryServiceTest extends ReservationServiceTest {
    @Autowired
    private WaitingQueryService waitingQueryService;

    @Autowired
    private BookingManageService bookingManageService;

    @Autowired
    private WaitingManageService waitingManageService;

    @Test
    @DisplayName("대기 중인 모든 예약 목록을 조회한다.")
    void findAllWithDetails() {
        // given
        bookingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING));
        waitingManageService.create(new Reservation(tommy, MIA_RESERVATION_DATE, miaReservationTime, wootecoTheme, WAITING));

        bookingManageService.create(TOMMY_RESERVATION(miaReservationTime, wootecoTheme, tommy, BOOKING));
        waitingManageService.create(new Reservation(admin, TOMMY_RESERVATION_DATE, miaReservationTime, wootecoTheme, PENDING_PAYMENT));
        waitingManageService.create(new Reservation(mia, TOMMY_RESERVATION_DATE, miaReservationTime, wootecoTheme, WAITING));

        // when
        List<Reservation> reservations = waitingQueryService.findAll();

        // then
        assertThat(reservations).hasSize(3)
                .extracting(Reservation::getMemberName)
                .contains(TOMMY_NAME, MIA_NAME, ADMIN_NAME);
    }

    @Test
    @DisplayName("사용자의 대기 예약 목록을 이전 대기 갯수와 함께 조회한다.")
    void findAllWithPreviousCountByMember() {
        // given
        bookingManageService.create(new Reservation(tommy, MIA_RESERVATION_DATE, miaReservationTime, wootecoTheme, BOOKING));
        waitingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, WAITING));

        // when
        List<WaitingReservation> waitingReservations = waitingQueryService.findAllWithPreviousCountByMember(mia);

        // then
        assertThat(waitingReservations).hasSize(1)
                .extracting(WaitingReservation::getPreviousCount)
                .contains(0L);
    }

    @Test
    @DisplayName("예약을 삭제하면 첫 번째 대기 예약 상태가 결제 대기로 바뀐다.")
    void deleteAndChangeToBooking() {
        // given
        Reservation reservationInBooking = bookingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING));
        Reservation reservationInWaiting = waitingManageService.create(new Reservation(admin, MIA_RESERVATION_DATE, miaReservationTime, wootecoTheme, WAITING));
        bookingManageService.delete(reservationInBooking.getId(), admin);

        // when
        List<Reservation> reservations = waitingQueryService.findAll();

        // then
        assertSoftly(softly -> {
            softly.assertThat(reservations).hasSize(1)
                    .extracting(Reservation::getId)
                    .contains(reservationInWaiting.getId());
            softly.assertThat(reservations.get(0).getStatus()).isEqualTo(PENDING_PAYMENT);
        });
    }
}
