package roomescape.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentConfirmResponse confirm(final PaymentConfirmRequest request) {
        return paymentClient.confirm(request);
    }
}
