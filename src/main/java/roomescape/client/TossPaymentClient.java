package roomescape.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.reservation.PaymentConfirmRequestDto;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

import java.util.Base64;
import java.util.Set;

@Component
public class TossPaymentClient implements PaymentClient{

    private static final String PAYMENT_CONFIRM_URL = "/v1/payments/confirm";
    private static final String PAYMENT_CONFIRM_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private static final String BASIC = "Basic ";
    private static final Set<String> INVISIBLE_CLIENT_ERROR_CODE = Set.of(
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    );

    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public TossPaymentClient(ObjectMapper objectMapper) {
        this.restClient = buildRestClient();
        this.objectMapper = objectMapper;
    }

    public void confirmPayment(PaymentConfirmRequestDto requestDto) {
        restClient.post()
                .uri(PAYMENT_CONFIRM_URL)
                .header(HttpHeaders.AUTHORIZATION, BASIC +
                        Base64.getEncoder().encodeToString(PAYMENT_CONFIRM_SECRET_KEY.getBytes()))
                .body(requestDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();
    }

    public RestClient buildRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(200);
        factory.setReadTimeout(30000);

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://api.tosspayments.com")
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String message = root.path("message").asText();
                    String code = root.path("code").asText();
                    if (INVISIBLE_CLIENT_ERROR_CODE.contains(code)) {
                        throw new PaymentConfirmServerException(message);
                    }
                    throw new PaymentConfirmClientException(message);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String message = root.path("message").asText();
                    throw new PaymentConfirmServerException(message);
                })
                .build();
    }
}
