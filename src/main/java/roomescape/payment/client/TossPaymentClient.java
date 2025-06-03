package roomescape.payment.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResult;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.exception.PaymentInternalServerException;
import roomescape.payment.exception.PaymentNetworkException;
import roomescape.payment.exception.PaymentUnauthorizedException;

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
        } catch (ResourceAccessException e) {
            throw new PaymentNetworkException(e);
        } catch (HttpMessageNotReadableException e) {
            throw new PaymentInternalServerException(e.getMessage(), "결제 응답을 처리할 수 없습니다.");
        } catch (IllegalArgumentException e) {
            throw new PaymentInternalServerException(e.getMessage(), "결제 요청이 올바르지 않습니다.");
        } catch (RestClientException e) {
            throw new PaymentInternalServerException(e.getMessage(), "결제 처리 중 오류가 발생했습니다.");
        }
    }

    private void handle4xxError(final HttpRequest request, final ClientHttpResponse response) {
        try {
            String responseBody = new String(response.getBody().readAllBytes());
            JsonNode jsonNode = MAPPER.readTree(responseBody);
            String errorMessage = jsonNode.get("message").asText();

            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new PaymentUnauthorizedException(errorMessage);
            }
            throw new PaymentException(responseBody, errorMessage, response.getStatusCode());
        } catch (IOException e) {
            throw new RuntimeException("파싱에 실패했습니다." + e.getMessage());
        }
    }

    private void handle5xxError(final HttpRequest request, final ClientHttpResponse response) {
        throw new PaymentInternalServerException("서버 에러가 발생했습니다.", "결제 요청 시간을 초과했습니다.");
    }
}
