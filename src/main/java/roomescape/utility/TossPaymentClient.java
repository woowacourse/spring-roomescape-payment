package roomescape.utility;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.dto.business.PaymentExceptionContent;
import roomescape.dto.business.PaymentResult;
import roomescape.exception.PaymentException;

public class TossPaymentClient implements PaymentClient {

    private static final String CONNECTION_ERROR_MESSAGE = "결제 서버에 연결이 실패하였습니다. 이 현상이 지속되는 경우 어드민에게 문의해주세요.";
    private static final String CONFIRM_SERVER_FAIL_MESSAGE = "결제 연동 서버가 아파요. 관리자에게 문의해주세요.";

    private final RestClient restClient;
    private final String secretKey;
    private final ObjectMapper statusParser = new ObjectMapper();
    private final String paymentConfirmUrl;

    public TossPaymentClient(
            RestClient restClient,
            String secretKey,
            String paymentConfirmUrl
    ) {
        this.restClient = restClient;
        this.secretKey = secretKey;
        this.paymentConfirmUrl = paymentConfirmUrl;
    }

    @Override
    public PaymentResult pay(String paymentKey, String orderId, long amount) {
        Map<String, Object> requestBody = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        try {
            return doPay(requestBody);
        } catch (RestClientException restClientException) {
            throw new RestClientException(CONNECTION_ERROR_MESSAGE);
        }
    }

    private PaymentResult doPay(Map<String, Object> requestBody) {
        return restClient.post()
                .uri(paymentConfirmUrl)
                .body(requestBody)
                .header("Authorization", createAuthHeaderConcise())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    PaymentExceptionContent paymentExceptionContent = statusParser.readValue(response.getBody(),
                            PaymentExceptionContent.class);
                    throw new PaymentException(paymentExceptionContent.message());
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new PaymentException(CONFIRM_SERVER_FAIL_MESSAGE);
                }))
                .body(PaymentResult.class);
    }

    public String createAuthHeaderConcise() {
        return "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}

