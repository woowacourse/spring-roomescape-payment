package roomescape.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.response.TossPaymentResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class PaymentCheckClient {

    private final RestClient restClient;
    private final String widgetSecretKey;
    private final String paymentCheckUrl;

    public PaymentCheckClient(
            @Value("${toss.payments.base-url}") String baseUrl,
            @Value("${toss.payments.widget-secret-key}") String widgetSecretKey,
            @Value("${toss.payments.payment-check-url}") String paymentCheckUrl,
            TimeoutClientHttpRequestFactory requestFactory
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultStatusHandler(new PaymentApproveErrorHandler())
                .build();
        this.widgetSecretKey = widgetSecretKey;
        this.paymentCheckUrl = paymentCheckUrl;
    }

    public TossPaymentResponse.PaymentStatus checkStatus(String paymentKey) {
        TossPaymentResponse response = restClient.get().uri(paymentCheckUrl, paymentKey)
                .header("Authorization", getAuthorizations())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(TossPaymentResponse.class);
        return response.status();
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
