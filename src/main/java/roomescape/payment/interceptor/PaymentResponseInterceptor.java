package roomescape.payment.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import roomescape.payment.dto.PaymentErrorResponse;
import roomescape.payment.exception.PaymentProcessException;
import roomescape.payment.exception.PaymentServerException;

@Component
@RequiredArgsConstructor
public class PaymentResponseInterceptor implements ClientHttpRequestInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        ClientHttpResponse response = execution.execute(request, body);

        if (response.getStatusCode().isError()) {
            PaymentErrorResponse errorResponse = getPaymentErrorResponse(response);
            throwPaymentErrorResponseByErrorCode(errorResponse);
        }

        return response;
    }

    private void throwPaymentErrorResponseByErrorCode(PaymentErrorResponse errorResponse) {
        if (TossPaymentServerError.isServerError(errorResponse.code())) {
            throw new PaymentServerException("결제가 제대로 수행되지 못했습니다.");
        }

        throw new PaymentProcessException(errorResponse.message());
    }

    private PaymentErrorResponse getPaymentErrorResponse(ClientHttpResponse response) throws IOException {
        return objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
    }
}
