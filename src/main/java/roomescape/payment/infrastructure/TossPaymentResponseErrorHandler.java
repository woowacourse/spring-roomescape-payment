package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.payment.PaymentException;

@RequiredArgsConstructor
public class TossPaymentResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        final HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.is4xxClientError() || statusCode.is5xxServerError();
    }

    @Override
    public void handleError(
            final URI url,
            final HttpMethod method,
            final ClientHttpResponse response
    ) throws IOException {
        InputStream body = response.getBody();
        TossPaymentErrorResponse errorResponse
                = objectMapper.readValue(body, TossPaymentErrorResponse.class);

        throw new PaymentException(
                response.getStatusCode(),
                errorResponse.code(),
                errorResponse.message()
        );
    }
}
