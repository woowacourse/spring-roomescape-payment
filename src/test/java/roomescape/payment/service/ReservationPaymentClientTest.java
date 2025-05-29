package roomescape.payment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import roomescape.payment.service.dto.ConfirmPaymentRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(ReservationPaymentClient.class)
class ReservationPaymentClientTest {
    private ReservationPaymentClient reservationPaymentClient;

//    @DisplayName("")
//    @Test
//    void postConfirmPaymentTest() {
//        String paymentKey = "";
//        String orderId = "";
//        Integer amount = ;
//        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest(
//                paymentKey,
//                orderId,
//                amount
//        );
//        reservationPaymentClient.postConfirmPayment(paymentRequest);
//    }
}
