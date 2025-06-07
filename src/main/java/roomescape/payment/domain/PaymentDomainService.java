package roomescape.payment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentClient paymentClient;

    public void approvePayment(final Payment payment) {
        paymentClient.approvePayment(payment);
    }
}
