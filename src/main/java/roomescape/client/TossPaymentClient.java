package roomescape.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.Payment;
import roomescape.dto.reservation.PaymentConfirmDto;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;

@Component
public class TossPaymentClient implements PaymentClient{

    private static final String BASE_URL = "https://api.tosspayments.com";
    private static final String PAYMENT_CONFIRM_URL = "/v1/payments/confirm";
    private static final String BASIC = "Basic ";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_CODE = "code";

    @Value("${toss.payment.confirm.secretKey}")
    private String PAYMENT_CONFIRM_SECRET_KEY;
    @Value("${toss.payment.confirm.connect-timeout}")
    private int CONNECT_TIMEOUT_MILLIS;
    @Value("${toss.payment.confirm.read-timeout}")
    private int READ_TIMEOUT_MILLIS;

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

    public RestClient buildRestClient() {
        return RestClient.builder()
                .requestFactory(getRequestFactory())
                .baseUrl(BASE_URL)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, this::handleClientError)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, this::handleServerError)
                .build();
    }

    private void handleClientError(HttpRequest request, ClientHttpResponse response) throws IOException {
        TossErrorResponse errorResponse = parseErrorResponse(response);
        if (INVISIBLE_CLIENT_ERROR_CODE.contains(errorResponse.code())) {
            throw new PaymentConfirmServerException(errorResponse);
        }
        throw new PaymentConfirmClientException(errorResponse);
    }

    private void handleServerError(HttpRequest request, ClientHttpResponse response) throws IOException {
        throw new PaymentConfirmServerException(parseErrorResponse(response));
    }

    private HttpComponentsClientHttpRequestFactory getRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(CONNECT_TIMEOUT_MILLIS));
        factory.setReadTimeout(Duration.ofMillis(READ_TIMEOUT_MILLIS));
        return factory;
    }

    private TossErrorResponse parseErrorResponse(ClientHttpResponse response) throws IOException {
        JsonNode root = objectMapper.readTree(response.getBody());
        String message = root.path(KEY_MESSAGE).asText();
        String code = root.path(KEY_CODE).asText();
        return new TossErrorResponse(response.getStatusCode(), code, message);
    }
}
