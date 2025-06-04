package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.dto.TossPaymentErrorResponse;
import roomescape.payment.infrastructure.dto.TossPaymentRequest;
import roomescape.payment.application.service.PaymentClient;
import roomescape.payment.infrastructure.dto.TossPaymentResponse;
import roomescape.reservation.presentation.dto.ReservationRequest;

public class TossPaymentClient implements PaymentClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TossPaymentClient.class);

    @Value("${payment.secret-key.test}")
    private String SECRET_KEY;

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public TossPaymentResponse approve(ReservationRequest reservationRequest) {
        TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(
                reservationRequest.getAmount(),
                reservationRequest.getOrderId(),
                reservationRequest.getPaymentKey()
        );

        return restClient.post()
                .uri("/payments/confirm")
                .header("Authorization", encodeSecretKey())
                .body(tossPaymentRequest)
                .retrieve()
                .onStatus(
                        status -> status.value() != 200,
                        (req, res) -> {
                            InputStream body = res.getBody();
                            TossPaymentErrorResponse errorResponse = objectMapper.readValue(body, TossPaymentErrorResponse.class);
                            handleErrorResponse(res.getStatusCode(), errorResponse);
                        }
                )
                .body(TossPaymentResponse.class);
    }

    private String encodeSecretKey() {
        String credentials = this.SECRET_KEY + ":";
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + base64Credentials;
    }

    private void handleErrorResponse(HttpStatusCode statusCode, TossPaymentErrorResponse errorResponse) {
        if (TossPaymentErrorMessage.contains(errorResponse.getCode())) {
            LOGGER.warn(errorResponse.getMessage());
            throw new PaymentException(statusCode, "시스템 오류로 인해 결제가 실패하였습니다. 고객센터에 문의해주세요.");
        }
        throw new PaymentException(statusCode, errorResponse.getMessage());
    }
}
