package roomescape.payment.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.payment.dto.PaymentErrorResponse;
import roomescape.payment.dto.RestClientPaymentCancelRequest;

public class PaymentRestClient {

    public static final String BASIC = "Basic ";
    public static final String INVALID_ORDER_ID_CODE = "INVALID_ORDER_ID";
    public static final String INVALID_API_KEY_CODE = "INVALID_API_KEY";
    public static final String UNAUTHORIZED_KEY_CODE = "UNAUTHORIZED_KEY";
    public static final String INCORRECT_BASIC_AUTH_FORMAT_CODE = "INCORRECT_BASIC_AUTH_FORMAT";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient;
    private final String secretKey;

    public PaymentRestClient(RestClient restClient, String secretKey) {
        this.restClient = restClient;
        this.secretKey = new String(Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
    }

    public void approvePayment(PaymentCreateRequest paymentCreateRequest) {
        String authorizations = BASIC + secretKey;

        restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentCreateRequest.createRestClientPaymentApproveRequest())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) ->
                        handleErrorMessage(response)
                ))
                .toBodilessEntity();
    }

    public void cancelPayment(String paymentKey) {
        String authorizations = BASIC + secretKey;
        restClient.post()
                .uri("/v1/payments/" + paymentKey + "/cancel")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestClientPaymentCancelRequest(CancelReason.CHANGE_MIND))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> handleErrorMessage(response))
                .toBodilessEntity();
    }

    private void handleErrorMessage(ClientHttpResponse httpResponse) {
        try {
            convertException(httpResponse);
        } catch (IOException | NullPointerException e) {
            throw new RoomEscapeException(
                    "서버에 문제가 발생해 결제가 실패했습니다. 관리자에게 문의해 주세요.", ExceptionTitle.INTERNAL_SERVER_ERROR);
        }
    }

    private void convertException(ClientHttpResponse httpResponse) throws IOException {
        PaymentErrorResponse response = objectMapper.readValue(httpResponse.getBody(), PaymentErrorResponse.class);
        String code = Objects.requireNonNull(response.code());
        String message = Objects.requireNonNull(response.message());

        if (code.equals(INVALID_ORDER_ID_CODE) || code.equals(INVALID_API_KEY_CODE) ||
            code.equals(UNAUTHORIZED_KEY_CODE) || code.equals(INCORRECT_BASIC_AUTH_FORMAT_CODE)) {
            throw new RoomEscapeException(
                    "서버에 문제가 발생해 결제가 실패했습니다. 관리자에게 문의해 주세요.", ExceptionTitle.INTERNAL_SERVER_ERROR);
        }
        throw new RoomEscapeException(message, ExceptionTitle.ILLEGAL_USER_REQUEST);
    }

    public String getSecretKey() {
        return secretKey;
    }
}
