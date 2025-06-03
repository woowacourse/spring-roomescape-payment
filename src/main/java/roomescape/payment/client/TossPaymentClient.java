package roomescape.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
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
import roomescape.payment.exception.TossPaymentErrorCodeForServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TossPaymentClient implements PaymentClient {

    private final ObjectMapper mapper;
    private final RestClient restClient;

    public PaymentResult confirmPayment(final PaymentRequest request) {
        return executeWithExceptionHandling(() ->
                restClient.post()
                        .uri("/confirm")
                        .body(request)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, this::handleResponseError)
                        .body(PaymentResult.class)
        );
    }

    private <T> T executeWithExceptionHandling(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (ResourceAccessException e) {
            throw new PaymentNetworkException(e);
        } catch (HttpMessageNotReadableException e) {
            throw new PaymentInternalServerException(e.getMessage(), "결제 승인 응답을 처리할 수 없습니다.");
        } catch (IllegalArgumentException e) {
            throw new PaymentInternalServerException(e.getMessage(), "결제 승인 요청이 올바르지 않습니다.");
        } catch (RestClientException e) {
            throw new PaymentInternalServerException(e.getMessage());
        }
    }

    private void handleResponseError(final HttpRequest request, final ClientHttpResponse response) throws IOException {
        TossPaymentErrorResponse errorResponse = getTossPaymentErrorResponse(response);
        if (TossPaymentErrorCodeForServer.contains(errorResponse.code)) {
            throw new PaymentInternalServerException(errorResponse.code);
        }
        throw new PaymentException(errorResponse.code, errorResponse.message, response.getStatusCode());
    }

    private TossPaymentErrorResponse getTossPaymentErrorResponse(final ClientHttpResponse response) throws IOException {
        InputStream inputStream = response.getBody();
        return mapper.readValue(inputStream, TossPaymentErrorResponse.class);
    }

    private record TossPaymentErrorResponse(
            String code,
            String message
    ) {

    }
}
