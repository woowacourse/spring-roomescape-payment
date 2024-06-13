package roomescape.infrastructure.payment;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.dto.PaymentCancelRequest;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentStatus;
import roomescape.service.payment.dto.TossPaymentResponse;

@Component
@Profile("!local")
public class TossPaymentClient implements PaymentClient {


    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Payment approve(PaymentRequest request) {
        TossPaymentResponse response = restClient.post()
            .uri("/v1/payments/confirm")
            .contentType(APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(TossPaymentResponse.class);

        return new Payment(response.paymentKey(),
            Long.parseLong(response.totalAmount()),
            response.requestedAt(),
            response.approvedAt(),
            PaymentStatus.from(response.status())
        );
    }

    @Override
    public void cancel(String paymentKey) {
        PaymentCancelRequest request = new PaymentCancelRequest("예약 최소");

        restClient.post()
            .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
            .contentType(APPLICATION_JSON)
            .body(request)
            .retrieve();
    }
}
