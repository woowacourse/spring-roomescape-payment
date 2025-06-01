package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.exception.TossErrorResponse;
import roomescape.payment.exception.TossPaymentException;

@Slf4j
public class TossRestClient {

    private static final List<String> SERVER_ERROR_CODE_LIST = List.of(
            "INVALID_API_KEY",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_AUTHORIZE_AUTH"
    );

    private final RestClient restClient;
    private final String authHeaderValue;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TossRestClient(final RestClient restClient, final TossPaymentProperties tossPaymentProperties) {
        this.restClient = restClient;
        this.authHeaderValue = "Basic " +
                Base64.getEncoder().encodeToString((tossPaymentProperties.getWidgetSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
    }

    public TossPaymentResponse confirm(final TossPaymentRequest tossPaymentRequest) {
        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .header("Authorization", authHeaderValue)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(tossPaymentRequest)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, response) -> {
                                TossErrorResponse tossErrorResponse = extractResponseFrom(response.getBody());
                                boolean isServerError = isServerError(tossErrorResponse.code());
                                throw new TossPaymentException(
                                        response.getStatusCode(), tossErrorResponse.message(), isServerError);
                            })
                    .body(TossPaymentResponse.class);
        } catch (ResourceAccessException ex) {
            log.error("Resource Access Exception:", ex);
            if (ex.getCause() instanceof SocketTimeoutException) {
                log.error("토스 결제 confirm 요청 타임아웃");
                throw new PaymentTimeoutException("결제 시스템이 응답하지 않아 시간이 초과되었습니다.");
            }
            throw ex;
        }
    }

    private TossErrorResponse extractResponseFrom(InputStream errorStream) {
        try (errorStream) {
            String errorBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
            return objectMapper.readValue(errorBody, TossErrorResponse.class);
        } catch (IOException e) {
            boolean isServerError = true;
            throw new TossPaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "토스 오류 응답을 파싱할 수 없습니다.", isServerError);
        }
    }

    private boolean isServerError(String code) {
        return SERVER_ERROR_CODE_LIST.contains(code);
    }
}
