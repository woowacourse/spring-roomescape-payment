package roomescape.service.booking.reservation.module;

import org.springframework.stereotype.Service;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.infrastructure.payment.TossPaymentClient;

@Service
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;

    public PaymentService(TossPaymentClient tossPaymentClient) {
        this.tossPaymentClient = tossPaymentClient;
    }

    public PaymentResponse pay(PaymentRequest paymentRequest) {
       return tossPaymentClient.requestPayment(paymentRequest);
    }
}
