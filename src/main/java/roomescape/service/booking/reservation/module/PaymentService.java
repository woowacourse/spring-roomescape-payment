package roomescape.service.booking.reservation.module;

import org.springframework.stereotype.Service;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.infrastructure.tosspayments.TossPaymentsClient;

@Service
public class PaymentService {

    private final TossPaymentsClient tossPaymentsClient;

    public PaymentService(TossPaymentsClient tossPaymentsClient) {
        this.tossPaymentsClient = tossPaymentsClient;
    }

    public PaymentResponse pay(PaymentRequest paymentRequest) {
       return tossPaymentsClient.requestPayment(paymentRequest);
    }
}
