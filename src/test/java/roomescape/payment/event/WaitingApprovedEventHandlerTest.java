package roomescape.payment.event;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.createWaiting_1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import roomescape.DBHelper;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservation.service.WaitingReservationService;

@SpringBootTest
@Transactional
@Commit
class WaitingApprovedEventHandlerTest {

    @Autowired
    DBHelper dbHelper;

    @Autowired
    WaitingReservationService waitingReservationService;

    @Autowired
    WaitingReservationRepository waitingReservationRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @DisplayName("예약 승인 시 결제 정보를 결제 전으로 저장")
    @Test
    void approveWaiting_then_savePayment() {
        // given
        WaitingReservation waiting = dbHelper.insertWaiting(createWaiting_1());

        // when
        waitingReservationService.approveWaitingReservation(waiting.getId());

        // then
        Reservation approvedReservation = reservationRepository.findAll().getFirst();
        Payment payment = paymentRepository.findByReservationId(approvedReservation.getId()).orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.NOT_PAID);
    }
}
