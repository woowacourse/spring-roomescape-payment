package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentInfo;
import roomescape.dto.request.MemberReservationRequest;

@Service
public class PaymentService {

    private final RestClient restClient;

    public PaymentService(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentInfo payment(MemberReservationRequest memberReservationRequest) {
        Long amount = memberReservationRequest.amount();
        String orderId = memberReservationRequest.orderId();
        String paymentKey = memberReservationRequest.paymentKey();

        PaymentInfo paymentInfo = new PaymentInfo(amount, orderId, paymentKey);

        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentInfo)
                .retrieve()
                .body(PaymentInfo.class);
    }
}
