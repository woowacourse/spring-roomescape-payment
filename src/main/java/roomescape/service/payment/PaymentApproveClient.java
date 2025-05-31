package roomescape.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.dto.response.PaymentSuccessResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class PaymentApproveClient {

    private final RestClient restClient;
    private final String widgetSecretKey;
    private final String paymentApproveUrl;

    public PaymentApproveClient(
            @Value("${toss.payments.base-url}") String baseUrl,
            @Value("${toss.payments.widget-secret-key}") String widgetSecretKey,
            @Value("${toss.payments.payment-approve-url}") String paymentApproveUrl,
            MyClientHttpRequestFactory requestFactory
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultStatusHandler(new PaymentApproveErrorHandler())
                .build();
        this.widgetSecretKey = widgetSecretKey;
        this.paymentApproveUrl = paymentApproveUrl;
    }

    public PaymentSuccessResponse approvePayment(String paymentKey, String orderId, int amount) {
        final String authorizations = getAuthorizations();
        final Map<String, Object> requestBody = Map.of(
                "amount", amount,
                "orderId", orderId,
                "paymentKey", paymentKey
        );

        return restClient.post().uri(paymentApproveUrl)
                .header("Authorization", authorizations)
                .accept(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(PaymentSuccessResponse.class);
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
