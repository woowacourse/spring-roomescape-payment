package roomescape.payment.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.global.util.Encoder;
import roomescape.payment.TossPaymentProperties;
import roomescape.payment.service.dto.PaymentErrorResponse;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.payment.service.dto.PaymentResponse;
import roomescape.payment.exception.PaymentException;

@Component
public class PaymentClient {

    private final RestClient restClient;

    private final Encoder encoder;

    private final TossPaymentProperties tossPaymentProperties;

    public PaymentClient(RestClient restClient, Encoder encoder, TossPaymentProperties tossPaymentProperties) {
        this.restClient = restClient;
        this.encoder = encoder;
        this.tossPaymentProperties = tossPaymentProperties;
    }

    public ResponseEntity<PaymentResponse> confirm(PaymentRequest paymentRequest) {
        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header("Authorization", getEncodeKey())
                    .body(paymentRequest)
                    .retrieve().toEntity(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResponse paymentResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            throw new PaymentException(paymentResponse);
        }
    }

    public ResponseEntity<PaymentResponse> cancel(String paymentKey) {
        Map<String, String> params = new HashMap<>();
        String cancelReason = "단순 변심";
        params.put("cancelReason", cancelReason);
        
        try {
            return restClient.post()
                    .uri(String.format("https://api.tosspayments.com/v1/payments/%s/cancel", paymentKey))
                    .header("Authorization", getEncodeKey())
                    .body(params)
                    .retrieve().toEntity(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResponse paymentResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            throw new PaymentException(paymentResponse);
        }
    }

    private String getEncodeKey() {
        return encoder.encode(tossPaymentProperties.getSecretKey());
    }
}
