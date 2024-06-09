package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.waiting.Waiting;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.service.ServiceBaseTest;

class ReservationCancelServiceTest extends ServiceBaseTest {

    @Autowired
    ReservationCancelService reservationCancelService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    WaitingRepository waitingRepository;

    @Test
    void 예약_취소() {
        // when
        reservationCancelService.cancelReservation(1L);

        // when
        List<Reservation> allReservations = reservationRepository.findAll();
        assertThat(allReservations).extracting(Reservation::getId)
                .isNotEmpty()
                .doesNotContain(1L);
    }

    @Test
    void 예약_취소후_대기_에약이_있을_경우_예약_상태로_자동_변경() {
        // when
        reservationCancelService.cancelReservation(30L);

        // when
        Reservation reservation = reservationRepository.findById(31L).orElseThrow();
        List<Waiting> allWaiting = waitingRepository.findAll();

        assertAll(
                () -> assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED),
                () -> assertThat(allWaiting).hasSize(1)
                        .extracting(Waiting::getWaitingOrderValue).containsOnly(1)
        );
    }

    @Test
    void 존재하지_않는_id로_취소할_경우_예외_발생() {
        // given
        Long notExistIdToFind = reservationRepository.findAll().size() + 1L;

        // when, then
        assertThatThrownBy(() -> reservationCancelService.cancelReservation(notExistIdToFind))
                .isInstanceOf(RoomEscapeException.class);
    }
}
