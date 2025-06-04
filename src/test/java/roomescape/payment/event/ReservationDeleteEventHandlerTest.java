package roomescape.payment.event;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import roomescape.DBHelper;
import roomescape.TestFixture;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.ReservationService;

@SpringBootTest
@Transactional
@Commit
class ReservationDeleteEventHandlerTest {

    @Autowired
    DBHelper dbHelper;

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @MockitoBean
    private TossRestClient tossRestClient;

    @DisplayName("예약 취소 시 결제 취소 api 호출 후 취소됐다면 결제 정보를 환불완료로 업데이트한다.")
    @Test
    void deleteReservation_then_cancelPayment() {
        // given
        Reservation reservation = dbHelper.insertReservation(TestFixture.createReservation_1());
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        TossPaymentResponse mockResponse = mock(TossPaymentResponse.class);

        given(mockResponse.status()).willReturn("CANCELED");

        String paymentKey = payment.getPaymentKey();
        given(tossRestClient.cancel(eq(paymentKey), any()))
                .willReturn(mockResponse);

        // when
        reservationService.deleteById(reservation.getId());

        // then
        Payment updated = paymentRepository.findById(payment.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        verify(tossRestClient).cancel(eq(paymentKey), any());
    }
}
