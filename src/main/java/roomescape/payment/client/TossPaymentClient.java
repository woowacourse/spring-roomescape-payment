package roomescape.payment.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResult;
import roomescape.payment.exception.PaymentApiException;
import roomescape.payment.exception.PaymentApiUnauthorizedException;

@Component
@RequiredArgsConstructor
public class TossPaymentClient implements PaymentClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final RestClient restClient;

    public PaymentResult confirmPayment(final PaymentRequest request) {

        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                    .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                    .body(PaymentResult.class);
        } catch (RestClientException e) {
            throw new PaymentApiException();
        }
    }

    private void handle4xxError(HttpRequest request, ClientHttpResponse response) {
        try {
            String responseBody = new String(response.getBody().readAllBytes());
            JsonNode jsonNode = MAPPER.readTree(responseBody);
            String errorMessage = jsonNode.get("message").asText();

            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new PaymentApiUnauthorizedException(errorMessage);
            }
            throw new PaymentApiException(responseBody, errorMessage, response.getStatusCode());
        } catch (IOException e) {
            throw new RuntimeException("파싱에 실패했습니다." + e.getMessage());
        }
    }

    private void handle5xxError(HttpRequest request, ClientHttpResponse response) throws IOException {
        throw new PaymentApiException("서버 에러가 발생했습니다.", "예상치 못한 에러가 발생했습니다.", response.getStatusCode());
    }
}
