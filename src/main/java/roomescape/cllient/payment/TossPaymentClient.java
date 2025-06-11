package roomescape.cllient.payment;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.domain.payment.dto.PaymentExceptionContent;
import roomescape.domain.payment.dto.PaymentResult;
import roomescape.exception.ExternalApiConnectionException;
import roomescape.exception.PaymentException;

public class TossPaymentClient implements PaymentClient {

    private static final String CONNECTION_ERROR_MESSAGE = "토스 결제 서버에 연결이 실패하였습니다.";
    private static final String PAYMENT_SERVER_ERROR_MESSAGE = "토스 결제 서버에서 예상치 못한 예외가 발생했습니다.";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient;
    private final String secretKey;
    private final String paymentAuthorizationUrl;

    public TossPaymentClient(
            RestClient restClient,
            String secretKey,
            String paymentAuthorizationUrl
    ) {
        this.restClient = restClient;
        this.secretKey = secretKey;
        this.paymentAuthorizationUrl = paymentAuthorizationUrl;
    }

    @Override
    public PaymentResult authorizePayment(String paymentKey, String orderId, long amount) {
        Map<String, Object> requestBody = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount);
        try {
            return doAuthorizePayment(requestBody);
        } catch (RestClientException restClientException) {
            throw new ExternalApiConnectionException(CONNECTION_ERROR_MESSAGE);
        }
    }

    private PaymentResult doAuthorizePayment(Map<String, Object> requestBody) {
        return restClient.post()
                .uri(paymentAuthorizationUrl)
                .body(requestBody)
                .header("Authorization", createAuthorizationHeaderContent(secretKey))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    PaymentExceptionContent paymentExceptionContent =
                            objectMapper.readValue(response.getBody(), PaymentExceptionContent.class);
                    throw new PaymentException(paymentExceptionContent.message());
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new PaymentException(PAYMENT_SERVER_ERROR_MESSAGE);
                }))
                .body(PaymentResult.class);
    }

    private String createAuthorizationHeaderContent(String secretKey) {
        return "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}

