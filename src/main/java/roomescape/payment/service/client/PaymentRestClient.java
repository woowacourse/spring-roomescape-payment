package roomescape.payment.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.common.exception.ConnectTimeOutException;
import roomescape.common.exception.ConnectionException;
import roomescape.common.exception.CustomException;
import roomescape.common.exception.PaymentServerException;
import roomescape.common.exception.error.PaymentErrorCode;
import roomescape.common.exception.PaymentException;
import roomescape.common.exception.ReadTimeOutException;
import roomescape.common.exception.TimeOutException;
import roomescape.payment.service.dto.PaymentClientErrorResponse;
import roomescape.payment.service.dto.PaymentClientResponse;
import roomescape.payment.service.dto.PaymentConfirmRequest;

@Component
public class PaymentRestClient implements PaymentClient {

    private static final String READ_TIME_OUT_MESSAGE = "Read timed out";
    private static final String CONNECT_TIME_OUT_MESSAGE = "Connect timed out";
    private static final Set<String> serverErrorCases = Set.of(
            "INVALID_API_KEY", "INVALID_AUTHORIZE_AUTH",
            "NOT_FOUND_TERMINAL_ID", "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    );

    private final RestClient restClient;

    private final String SECRET_KEY;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentRestClient(RestClient restClient,
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
        } catch (final RestClientException e) {
            throw getConnectionException(e);
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

    private CustomException getPaymentError(
        PaymentClientErrorResponse error, HttpStatusCode httpStatusCode) {
        if (serverErrorCases.contains(error.code())) {
            return new PaymentServerException(error.message(), PaymentErrorCode.SERVER_ERROR);
        }
        if (httpStatusCode.is4xxClientError()) {
            return new PaymentException(error.message(), PaymentErrorCode.CLIENT_ERROR);
        }
        return new PaymentException(error.message(), PaymentErrorCode.PAYMENT_SERVER_ERROR);
    }

    private ConnectionException getConnectionException(final RestClientException e) {
        final Throwable cause = e.getCause();

        if (cause instanceof SocketTimeoutException) {
            String message = cause.getMessage();
            if (READ_TIME_OUT_MESSAGE.equals(message)) {
                return new ReadTimeOutException(cause.getMessage());
            }
            if (CONNECT_TIME_OUT_MESSAGE.equals(message)) {
                return new ConnectTimeOutException(cause.getMessage());
            }
            return new TimeOutException(cause.getMessage());
        }

        if (e instanceof ResourceAccessException) {
            throw new ConnectionException(e.getMessage());
        }

        throw e;
    }
}
