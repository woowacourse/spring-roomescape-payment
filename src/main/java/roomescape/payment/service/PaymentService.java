package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.dto.PaymentRequest;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        return paymentClient.getPaymentConfirm(paymentRequest);
    }

}
