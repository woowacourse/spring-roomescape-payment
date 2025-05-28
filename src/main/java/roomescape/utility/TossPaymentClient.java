package roomescape.utility;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.dto.business.PaymentExceptionContent;
import roomescape.dto.business.PaymentResult;
import roomescape.exception.PaymentException;

public class TossPaymentClient implements PaymentClient {

    private static final String PAYMENT_CONFIRM_URL = "/v1/payments/confirm";

    private final RestClient restClient;
    private final String secretKey;
    private final ObjectMapper statusParser = new ObjectMapper();

    public TossPaymentClient(
            RestClient restClient,
            String secretKey
    ) {
        this.restClient = restClient;
        this.secretKey = secretKey;
    }

    @Override
    public PaymentResult pay(String paymentKey, String orderId, long amount) {
        Map<String, Object> requestBody = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        return restClient.post()
                .uri(PAYMENT_CONFIRM_URL)
                .body(requestBody)
                .header("Authorization", createAuthHeaderConcise())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    PaymentExceptionContent paymentExceptionContent = statusParser.readValue(response.getBody(),
                            PaymentExceptionContent.class);
                    throw new PaymentException(paymentExceptionContent.message());
                }))
                .body(PaymentResult.class);
    }

    public String createAuthHeaderConcise() {
        return "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}

