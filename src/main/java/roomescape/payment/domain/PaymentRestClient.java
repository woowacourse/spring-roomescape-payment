package roomescape.payment.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.payment.dto.PaymentErrorResponse;
import roomescape.payment.dto.RestClientPaymentCancelRequest;

public class PaymentRestClient {

    private static final String BASIC = "Basic ";
    private static final Set<String> INTERNAL_SERVER_ERROR_CODES = Set.of(
            "INVALID_ORDER_ID", "INVALID_API_KEY", "UNAUTHORIZED_KEY", "INCORRECT_BASIC_AUTH_FORMAT"
    );

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient;
    private final String secretKey;

    public PaymentRestClient(RestClient restClient, String secretKey) {
        this.restClient = restClient;
        this.secretKey = new String(Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
    }

    public void approvePayment(PaymentCreateRequest paymentCreateRequest) {
        String authorizations = BASIC + secretKey;
        try {
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
        } catch (RestClientException e) {
            throwInternalServerError();
        }
    }

    public void cancelPayment(String paymentKey) {
        String authorizations = BASIC + secretKey;
        try {
            restClient.post()
                    .uri("/v1/payments/" + paymentKey + "/cancel")
                    .header("Authorization", authorizations)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RestClientPaymentCancelRequest(CancelReason.CHANGE_MIND))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> handleErrorMessage(response))
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throwInternalServerError();
        }
    }

    private void handleErrorMessage(ClientHttpResponse httpResponse) {
        try {
            convertException(httpResponse);
        } catch (IOException | NullPointerException e) {
            throwInternalServerError();
        }
    }

    private void convertException(ClientHttpResponse httpResponse) throws IOException {
        PaymentErrorResponse response = objectMapper.readValue(httpResponse.getBody(), PaymentErrorResponse.class);
        String code = Objects.requireNonNull(response.code());
        String message = Objects.requireNonNull(response.message());

        if (INTERNAL_SERVER_ERROR_CODES.contains(code)) {
            throwInternalServerError();
        }
        throw new RoomEscapeException(message, ExceptionTitle.ILLEGAL_USER_REQUEST);
    }

    private void throwInternalServerError() {
        throw new RoomEscapeException(
                "서버에 문제가 발생해 결제가 실패했습니다. 관리자에게 문의해 주세요.", ExceptionTitle.INTERNAL_SERVER_ERROR);
    }

    public String getSecretKey() {
        return secretKey;
    }
}
