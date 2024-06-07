package roomescape.application.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import roomescape.application.ServiceTest;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Theme;
import roomescape.fixture.ReservationFixture;

@ServiceTest
@Import(ReservationFixture.class)
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Autowired
    private ReservationPaymentRepository reservationPaymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationFixture reservationFixture;

    @MockBean
    private PaymentClient paymentClient;

    @Test
    @DisplayName("결제가 정상 처리되면, 결제 정보를 저장한다.")
    void saveOnPurchaseSuccess() {
        Reservation reservation = reservationFixture.saveReservation();
        Theme theme = reservation.getTheme();
        PaymentRequest request = new PaymentRequest("orderId", theme.getPrice(), "paymentKey");
        given(paymentClient.requestPurchase(any(PaymentRequest.class)))
                .willReturn(new Payment("paymentKey", "orderId", "DONE", theme.getPrice()));

        paymentService.purchase(reservation.getId(), request);

        ReservationPayment reservationPayment = reservationPaymentRepository.getById("orderId");
        assertThat(reservationPayment.getPaymentKey()).isEqualTo("paymentKey");
    }
}
