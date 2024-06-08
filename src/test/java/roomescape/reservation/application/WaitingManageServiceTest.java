package roomescape.reservation.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.global.exception.ViolationException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.response.MyReservationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_DATE;
import static roomescape.TestFixture.USER_ADMIN;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;
import static roomescape.reservation.domain.ReservationStatus.PENDING_PAYMENT;
import static roomescape.reservation.domain.ReservationStatus.WAITING;

class WaitingManageServiceTest extends ReservationServiceTest {
    @Autowired
    private WaitingManageService waitingManageService;

    @Autowired
    private BookingManageService bookingManageService;

    @Autowired
    private ReservationQueryService reservationQueryService;

    @Test
    @DisplayName("동일한 테마, 날짜, 시간에 예약이 있다면 대기 예약을 한다.")
    void create() {
        // given
        bookingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING));

        Reservation waitingReservation = new Reservation(
                tommy, MIA_RESERVATION_DATE, miaReservationTime, wootecoTheme, WAITING);

        // when
        Reservation createdReservation = waitingManageService.create(waitingReservation);

        // then
        assertSoftly(softly -> {
            softly.assertThat(createdReservation.getId()).isNotNull();
            softly.assertThat(createdReservation.getStatus()).isEqualTo(WAITING);
        });
    }

    @Test
    @DisplayName("동일한 테마, 날짜, 시간에 예약이 없다면 대기할 수 없다.")
    void createInvalidWaitingReservation() {
        // given
        Reservation waitingReservation = new Reservation(
                tommy, MIA_RESERVATION_DATE, miaReservationTime, wootecoTheme, WAITING);

        // when
        Reservation createdReservation = waitingManageService.create(waitingReservation);

        // then
        assertThat(createdReservation.getStatus()).isEqualTo(BOOKING);
    }

    @Test
    @DisplayName("사용자 본인의 대기 예약을 취소한다.")
    void delete() {
        // given
        bookingManageService.create(new Reservation(tommy, MIA_RESERVATION_DATE, miaReservationTime, wootecoTheme, BOOKING));

        Reservation waitingReservation = waitingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, WAITING));

        // when
        waitingManageService.delete(waitingReservation.getId(), mia);

        // then
        List<MyReservationResponse> miaReservations = reservationQueryService.findAllMyReservations(mia);
        assertThat(miaReservations).hasSize(0);
    }

    @Test
    @DisplayName("관리자가 대기 예약을 취소한다.")
    void deleteWithAdmin() {
        // given
        bookingManageService.create(new Reservation(tommy, MIA_RESERVATION_DATE, miaReservationTime, wootecoTheme, BOOKING));
        Reservation waitingReservation = waitingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, WAITING));

        // when
        waitingManageService.delete(waitingReservation.getId(), admin);

        // then
        List<MyReservationResponse> miaReservations = reservationQueryService.findAllMyReservations(mia);
        assertThat(miaReservations).hasSize(0);
    }

    @Test
    @DisplayName("다른 사용자의 대기 예약을 취소할 수 없다.")
    void deleteWithoutOwnerShip() {
        // given
        bookingManageService.create(new Reservation(tommy, MIA_RESERVATION_DATE, miaReservationTime, wootecoTheme, BOOKING));

        Reservation miaWaitingReservation = waitingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, WAITING));

        // when & then
        assertThatThrownBy(() -> waitingManageService.delete(miaWaitingReservation.getId(), tommy))
                .isInstanceOf(ViolationException.class);
    }

    @Test
    @DisplayName("관리자가 결제 대기 예약을 승인한다.")
    void approve() {
        // given
        Reservation createdReservation = bookingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, PENDING_PAYMENT));

        // when
        waitingManageService.approve(createdReservation.getId(), USER_ADMIN());

        // then
        List<MyReservationResponse> miaReservations = reservationQueryService.findAllMyReservations(mia);
        assertThat(miaReservations).hasSize(1)
                .extracting(MyReservationResponse::reservationId)
                .contains(createdReservation.getId());
    }
}
