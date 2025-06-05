package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.application.PaymentClient;
import roomescape.payment.application.PaymentException;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;
import roomescape.payment.infrastructure.dto.TossPaymentResponse;

@RequiredArgsConstructor
@Component
public class TossPaymentClient implements PaymentClient {

    @Qualifier("tossPaymentRestClient")
    private final RestClient restClient;

    private final ObjectMapper objectMapper;

    public PaymentResponse requestPayment(final PaymentRequest request) {
        return restClient.post()
                .uri("v1/payments/confirm")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .body(TossPaymentResponse.class);
    }

    private void handle4xxError(HttpRequest httpRequest, ClientHttpResponse clientHttpResponse) {
        try {
            JsonNode node = objectMapper.readTree(clientHttpResponse.getBody());
            String code = node.path("code").asText();
            String message = node.path("message").asText();
            if (code.equals("UNAUTHORIZED_KEY")) {
                throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "방탈출 예약 서비스 결제 시스템에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }
            throw new PaymentException(HttpStatus.BAD_REQUEST, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handle5xxError(HttpRequest httpRequest, ClientHttpResponse clientHttpResponse) {
        throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "토스 결제 시스템에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}

