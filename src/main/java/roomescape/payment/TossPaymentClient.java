package roomescape.payment;

import java.util.Map;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import roomescape.global.config.TossPaymentProperties;
import roomescape.payment.dto.PaymentCancelRequest;
import roomescape.payment.dto.PaymentCancelResponse;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

public class TossPaymentClient {

    private static final String CANCEL_REASON = "cancelReason";

    private final RestClient restClient;
    private final TossPaymentProperties tossPaymentProperties;

    public TossPaymentClient(RestClient restClient, TossPaymentProperties tossPaymentProperties) {
        this.restClient = restClient;
        this.tossPaymentProperties = tossPaymentProperties;
    }

    public PaymentConfirmResponse confirmPayments(PaymentConfirmRequest request) {
        return restClient.post()
                .uri(tossPaymentProperties.api().confirm())
                .body(request)
                .retrieve()
                .toEntity(PaymentConfirmResponse.class)
                .getBody();
    }

    public PaymentCancelResponse cancelPayments(PaymentCancelRequest request, String reason) {
        Map<String, String> cancelRequestBody = Map.of(CANCEL_REASON, reason);
        String cancelUri = UriComponentsBuilder.fromPath(tossPaymentProperties.api().cancel())
                .buildAndExpand(request.paymentKey())
                .toUriString();

        return restClient.post()
                .uri(cancelUri)
                .body(cancelRequestBody)
                .retrieve()
                .toEntity(PaymentCancelResponse.class)
                .getBody();
    }
}
