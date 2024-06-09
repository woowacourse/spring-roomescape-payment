package roomescape.service.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentCancelRequest;
import roomescape.infrastructure.tosspayments.TossPaymentsClient;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.service.ServiceBaseTest;

class ReservationCancelServiceTest extends ServiceBaseTest {

    @Autowired
    ReservationCancelService reservationCancelService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @MockBean
    TossPaymentsClient tossPaymentsClient;

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
    void 결제된_예약을_취소할_경우_자동_결제_취소() {
        // given
        Payment payment = paymentRepository.findByReservationIdOrThrow(1L);

        PaymentCancelRequest cancelRequest = new PaymentCancelRequest(payment.getPaymentKey(), "취소 사유");
        Mockito.doNothing().when(tossPaymentsClient).requestPaymentCancel(cancelRequest);

        // when
        reservationCancelService.cancelReservation(payment.getReservationId());

        //then
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).extracting(Payment::getId)
                .isNotEmpty()
                .doesNotContain(payment.getId());
    }
}
