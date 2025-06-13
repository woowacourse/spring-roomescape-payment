package roomescape.payment.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.custom.PaymentClientException;
import roomescape.common.exception.custom.PaymentServerException;
import roomescape.payment.domain.PaymentMethod;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.TossPaymentErrorResponse;
import roomescape.payment.error.InternalServerErrorCode;

@Component
public class TossPaymentGateway implements PaymentGateway {

    private static final String PAYMENTS_CONFIRM_ENDPOINT = "https://api.tosspayments.com/v1/payments/confirm";

    private final String secretKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentGateway(@Value("${toss.payment.secret-key}") final String secretKey,
                              @Qualifier("tossRestClient") final RestClient restClient,
                              final ObjectMapper objectMapper) {
        this.secretKey = secretKey;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.TOSS;
    }

    @Override
    public void confirm(PaymentRequest request) {
        String secretKeyWithColon = secretKey + ":";
        byte[] secretKeyBytes = secretKeyWithColon.getBytes(StandardCharsets.UTF_8);

        restClient.post()
                .uri(PAYMENTS_CONFIRM_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(secretKeyBytes))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> handleError(res))
                .toBodilessEntity();
    }

    private void handleError(final ClientHttpResponse res) {
        try (InputStream is = res.getBody()) {
            TossPaymentErrorResponse errorResponse = objectMapper.readValue(is, TossPaymentErrorResponse.class);
            String errorCode = errorResponse.code();
            if (InternalServerErrorCode.contains(errorCode)) {
                throw new PaymentServerException(errorResponse.message());
            }
            throw new PaymentClientException(errorResponse.message());
        } catch (IOException e) {
            throw new RuntimeException("결제 에러 응답 파싱 실패", e);
        }
    }
}