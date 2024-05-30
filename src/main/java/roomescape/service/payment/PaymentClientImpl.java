package roomescape.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.exception.payment.PaymentConfirmErrorCode;
import roomescape.exception.payment.PaymentConfirmException;
import roomescape.service.payment.dto.PaymentConfirmFailOutput;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;

@Component
public class PaymentClientImpl implements PaymentClient {
    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    private static final String BASIC_DELIMITER = ":";
    private static final String AUTH_HEADER_PREFIX = "Basic ";
    private final String secretKey;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public PaymentClientImpl(@Value("${payment.secret-key}") String secretKey,
                             ObjectMapper objectMapper) {
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().build();
    }

    @Override
    public PaymentConfirmOutput confirmPayment(PaymentConfirmInput confirmRequest) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + BASIC_DELIMITER).getBytes(StandardCharsets.UTF_8));
        String authorizations = AUTH_HEADER_PREFIX + new String(encodedBytes);

        return restClient.method(HttpMethod.POST)
                .uri(BASE_URL + "/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .body(confirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new PaymentConfirmException(getPaymentConfirmErrorCode(response));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new PaymentConfirmException(getPaymentConfirmErrorCode(response));
                })
                .body(PaymentConfirmOutput.class);
    }

    private PaymentConfirmErrorCode getPaymentConfirmErrorCode(final ClientHttpResponse response) throws IOException {
        PaymentConfirmFailOutput confirmFailResponse = objectMapper.readValue(
                response.getBody(), PaymentConfirmFailOutput.class);
        return PaymentConfirmErrorCode.findByName(confirmFailResponse.code());
    }
}
