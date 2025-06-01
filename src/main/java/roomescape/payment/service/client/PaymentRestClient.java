package roomescape.payment.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.ConnectionException;
import roomescape.common.exception.PaymentErrorCode;
import roomescape.common.exception.PaymentException;
import roomescape.payment.service.dto.PaymentClientErrorResponse;
import roomescape.payment.service.dto.PaymentClientResponse;
import roomescape.payment.service.dto.PaymentConfirmRequest;

@Component
public class PaymentRestClient implements PaymentClient {

    private static final Set<String> serverErrorCases = Set.of(
            "INVALID_API_KEY", "INVALID_AUTHORIZE_AUTH",
            "NOT_FOUND_TERMINAL_ID", "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    );

    private final RestClient restClient;

    private final String SECRET_KEY;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentRestClient(@Qualifier("tossPaymentsRestClient") RestClient restClient,
                             @Value("${payment.key}") String SECRET_KEY) {
        this.restClient = restClient;
        this.SECRET_KEY = SECRET_KEY;
    }

    public PaymentClientResponse confirm(PaymentConfirmRequest paymentConfirmRequest) {
        try {
            return restClient.post().uri("/confirm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + Base64.getEncoder()
                            .encodeToString((SECRET_KEY + ':').getBytes(StandardCharsets.UTF_8)))
                    .body(paymentConfirmRequest)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (req, res) -> handlePaymentError(res)
                    )
                    .body(PaymentClientResponse.class);
        } catch (final ResourceAccessException e) {
            throw new ConnectionException(e.getCause().getMessage());
        }
    }

    private void handlePaymentError(ClientHttpResponse res) {
        try {
            byte[] body = res.getBody().readAllBytes();
            PaymentClientErrorResponse error = objectMapper.readValue(body,
                PaymentClientErrorResponse.class);
            throw getPaymentError(error, res.getStatusCode());
        } catch (IOException e) {
            throw new RuntimeException("응답 바디 파싱에 실패하였습니다.", e);
        }
    }

    private PaymentException getPaymentError(
        PaymentClientErrorResponse error, HttpStatusCode httpStatusCode) {
        if (serverErrorCases.contains(error.code())) {
            throw new PaymentException(error.message(), PaymentErrorCode.SERVER_ERROR);
        }
        if (httpStatusCode.is4xxClientError()) {
            throw new PaymentException(error.message(), PaymentErrorCode.CLIENT_ERROR);
        }
        throw new PaymentException(error.message(), PaymentErrorCode.PAYMENT_SERVER_ERROR);
    }
}
