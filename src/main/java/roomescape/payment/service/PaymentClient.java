package roomescape.payment.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.payment.service.dto.PaymentErrorResponse;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.payment.service.dto.PaymentResponse;
import roomescape.payment.exception.PaymentException;

@Component
public class PaymentClient {
    private final RestClient restClient;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<PaymentResponse> confirm(PaymentRequest paymentRequest, String encodeKey) {
        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header("Authorization", encodeKey)
                    .body(paymentRequest)
                    .retrieve().toEntity(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResponse paymentResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            throw new PaymentException(paymentResponse);
        }
    }

    public ResponseEntity<PaymentResponse> cancel(String paymentKey, String encodeKey) {
        try {
            Map<String, String> params = new HashMap<>();
            String cancelReason = "단순 변심";
            params.put("cancelReason", cancelReason);
            return restClient.post()
                    .uri(String.format("https://api.tosspayments.com/v1/payments/%s/cancel", paymentKey))
                    .header("Authorization", encodeKey)
                    .body(params)
                    .retrieve().toEntity(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResponse paymentResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            throw new PaymentException(paymentResponse);
        }
    }
}
