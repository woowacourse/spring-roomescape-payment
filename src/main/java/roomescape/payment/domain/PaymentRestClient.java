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
import roomescape.payment.dto.PaymentErrorResponse;
import roomescape.payment.dto.RestClientPaymentApproveRequest;
import roomescape.payment.dto.RestClientPaymentApproveResponse;
import roomescape.payment.dto.RestClientPaymentCancelRequest;

/**
 * @see <a href="https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8">토스 결제 승인 예외정의서</a>
 */
public class PaymentRestClient {

    private static final String BASIC = "Basic ";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient;
    private final String secretKey;

    public PaymentRestClient(RestClient restClient, String secretKey) {
        this.restClient = restClient;
        this.secretKey = new String(Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
    }

    public RestClientPaymentApproveResponse approvePayment(RestClientPaymentApproveRequest restClientPaymentApproveRequest) {
        String authorizations = BASIC + secretKey;
        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .header("Authorization", authorizations)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(restClientPaymentApproveRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, ((request, response) ->
                            handleErrorMessage(response)
                    ))
                    .body(RestClientPaymentApproveResponse.class);
        } catch (RoomEscapeException e) {
            throw e;
        } catch (Exception e) {
            throw new RoomEscapeException(
                    "서버에 문제가 발생해 결제가 실패했습니다. 관리자에게 문의해 주세요.", ExceptionTitle.INTERNAL_SERVER_ERROR, e);
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
        } catch (RoomEscapeException e) {
            throw e;
        } catch (Exception e) {
            throw new RoomEscapeException(
                    "서버에 문제가 발생해 결제가 실패했습니다. 관리자에게 문의해 주세요.", ExceptionTitle.INTERNAL_SERVER_ERROR, e);
        }
    }

    private void handleErrorMessage(ClientHttpResponse httpResponse) {
        try {
            convertException(httpResponse);
        } catch (IOException | NullPointerException e) {
            throw new RoomEscapeException(
                    "서버에 문제가 발생해 결제가 실패했습니다. 관리자에게 문의해 주세요.", ExceptionTitle.INTERNAL_SERVER_ERROR, e);
        }
    }

    private void convertException(ClientHttpResponse httpResponse) throws IOException {
        PaymentErrorResponse response = objectMapper.readValue(httpResponse.getBody(), PaymentErrorResponse.class);
        String code = response.code();
        String message = response.message();

        if (isNullResponse(code, message) || ServerErrorCode.isServerErrorCode(code)) {
            throw new RoomEscapeException(
                    "서버에 문제가 발생해 결제가 실패했습니다. 관리자에게 문의해 주세요.", ExceptionTitle.INTERNAL_SERVER_ERROR);
        }

        throw new RoomEscapeException(message, ExceptionTitle.ILLEGAL_USER_REQUEST);
    }

    private boolean isNullResponse(String code, String message) {
        return Objects.isNull(code) || Objects.isNull(message);
    }

    public String getSecretKey() {
        return secretKey;
    }
}
