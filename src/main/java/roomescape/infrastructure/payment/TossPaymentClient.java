package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import roomescape.business.dto.PaymentApproveDto;
import roomescape.exception.PaymentApproveException;

public class TossPaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String secretKey;

    public TossPaymentClient(
            RestClient restClient,
            ObjectMapper objectMapper,
            String secretKey) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
    }

    public PaymentApproveResponseDto approvePayment(PaymentApproveDto paymentApproveDto) {
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentApproveDto)
                .header("Authorization", "Basic " + encodedSecretKey)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    InputStream responseBodyStream = response.getBody();
                    String rawResponseBody = StreamUtils.copyToString(responseBodyStream, StandardCharsets.UTF_8);
                    throw objectMapper.readValue(rawResponseBody, PaymentApproveException.class);
                })
                .toEntity(PaymentApproveResponseDto.class).getBody();
    }
}
