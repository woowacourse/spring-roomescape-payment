package roomescape.reservation.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.reservation.infrastructure.dto.PaymentRequest;
import roomescape.reservation.infrastructure.dto.PaymentResponse;
import roomescape.reservation.infrastructure.dto.TossErrorResponse;
import roomescape.reservation.infrastructure.dto.TossPaymentRequest;

@RequiredArgsConstructor
@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    @Value("${payment.toss.secret-key}")
    private String secretKey;

    public PaymentResponse pay(final PaymentRequest request) {
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", "Basic " + encodedKey)
                .body(TossPaymentRequest.from(request))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> {
                            handleException(res);
                        })
                .body(PaymentResponse.class);
    }

    private void handleException(final ClientHttpResponse res) {
        try (InputStream is = res.getBody()) {
            ObjectMapper objectMapper = new ObjectMapper();
            TossErrorResponse error = objectMapper.readValue(is, TossErrorResponse.class);
            throw new TossPaymentException(error.code(), error.message());
        } catch (IOException e) {
            throw new RuntimeException("에러 응답 파싱 실패", e);
        }
    }
}
