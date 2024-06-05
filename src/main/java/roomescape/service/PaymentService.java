package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentInfo;
import roomescape.dto.request.MemberReservationRequest;
import roomescape.infrastructure.TossPaymentProperties;

@Service
public class PaymentService {

    private final RestClient restClient;
    private final TossPaymentProperties tossPaymentProperties;

    public PaymentService(RestClient restClient, TossPaymentProperties tossPaymentProperties) {
        this.restClient = restClient;
        this.tossPaymentProperties = tossPaymentProperties;
    }

    public void payment(MemberReservationRequest memberReservationRequest) {
        Long amount = memberReservationRequest.amount();
        String orderId = memberReservationRequest.orderId();
        String paymentKey = memberReservationRequest.paymentKey();

        PaymentInfo paymentInfo = new PaymentInfo(amount, orderId, paymentKey);

        restClient.post()
                .uri(tossPaymentProperties.url().confirm())
                .body(paymentInfo)
                .retrieve()
                .toBodilessEntity();
    }
}
