package roomescape.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.dto.response.PaymentSuccessResponse;

@Service
public class PaymentApproveClient {

    private final RestClient restClient;
    private final String widgetSecretKey;

    public PaymentApproveClient(
            RestClient.Builder restClientBuilder,
            @Value("${toss.payments.base-url}") String baseUrl,
            @Value("${toss.payments.widget-secret-key}") String widgetSecretKey
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.widgetSecretKey = widgetSecretKey;
    }

    public PaymentSuccessResponse approvePayment(String paymentKey, String orderId, int amount) {
        final String authorizations = getAuthorizations();
        final Map<String, Object> requestBody = Map.of(
                "amount", amount,
                "orderId", orderId,
                "paymentKey", paymentKey
        );

        return restClient.post().uri("/v1/payments/confirm")
                .header("Authorization", authorizations)
                .body(requestBody)
                .retrieve()
                .onStatus(status -> status != HttpStatus.OK,
                        (req, resp) -> {
                            final String errMessage = extractMessage(resp, "message");
                            throw new IllegalStateException("[ERROR] " + errMessage);
                        }
                ).body(PaymentSuccessResponse.class);
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    public String extractMessage(ClientHttpResponse response, String key) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(inputStream);
        return jsonNode.get(key).asText();
    }
}
