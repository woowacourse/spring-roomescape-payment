package roomescape.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.Payment;
import roomescape.dto.reservation.PaymentConfirmDto;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

import java.time.Duration;
import java.util.Base64;
import java.util.Set;

@Component
public class TossPaymentClient implements PaymentClient{

    public static final String BASE_URL = "https://api.tosspayments.com";
    private static final String PAYMENT_CONFIRM_URL = "/v1/payments/confirm";
    private static final String PAYMENT_CONFIRM_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String BASIC = "Basic ";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_CODE = "code";
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

    public RestClient buildRestClient() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(3_000));
        factory.setReadTimeout(Duration.ofMillis(30_000));
        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(BASE_URL)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String message = root.path(KEY_MESSAGE).asText();
                    String code = root.path(KEY_CODE).asText();
                    if (INVISIBLE_CLIENT_ERROR_CODE.contains(code)) {
                        TossErrorResponse errorResponse = new TossErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, code, message);
                        throw new PaymentConfirmServerException(errorResponse);
                    }
                    TossErrorResponse errorResponse = new TossErrorResponse(HttpStatus.BAD_REQUEST, code, message);
                    throw new PaymentConfirmClientException(errorResponse);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String message = root.path(KEY_MESSAGE).asText();
                    String code = root.path(KEY_CODE).asText();
                    TossErrorResponse errorResponse = new TossErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, code, message);
                    throw new PaymentConfirmServerException(errorResponse);
                })
                .build();
    }

    public Payment confirmPayment(PaymentConfirmDto requestDto) {
        return restClient.post()
                .uri(PAYMENT_CONFIRM_URL)
                .header(HttpHeaders.AUTHORIZATION, BASIC +
                        Base64.getEncoder().encodeToString(PAYMENT_CONFIRM_SECRET_KEY.getBytes()))
                .body(requestDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Payment.class);
    }
}
