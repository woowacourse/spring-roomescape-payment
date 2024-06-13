package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.repository.ReservationRepository;
import roomescape.service.ServiceBaseTest;

class WaitingApproveServiceTest extends ServiceBaseTest {

    @Autowired
    WaitingApproveService waitingApproveService;

    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void 예약_대기를_승인할_경우_결제_대기로_변경() {
        // given
        Reservation reservationToReserved = reservationRepository.findByIdOrThrow(30L);
        Reservation reservationToWaiting = reservationRepository.findByIdOrThrow(31L);
        reservationRepository.delete(reservationToReserved);

        // when
        waitingApproveService.approveWaitingReservation(reservationToWaiting.getId());

        // then
        Reservation result = reservationRepository.findByIdOrThrow(31L);
        assertThat(result.getStatus()).isEqualTo(Status.PAYMENT_PENDING);
    }
}
