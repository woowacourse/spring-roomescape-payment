package roomescape.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.common.exception.impl.DeserializationException;
import roomescape.common.exception.impl.TossPaymentErrorException;
import roomescape.payment.application.dto.TossErrorResponse;

public class TossPaymentResponseInterceptor implements ClientHttpRequestInterceptor {

    private final ObjectMapper objectMapper;

    public TossPaymentResponseInterceptor(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ClientHttpResponse intercept(
        final HttpRequest request,
        final byte[] body,
        final ClientHttpRequestExecution execution
    ) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        HttpStatus status = (HttpStatus) response.getStatusCode();

        if (status.isError()) {
            TossErrorResponse error = parseErrorResponse(response.getBody());
            throw new TossPaymentErrorException(status, error.code(), error.message());
        }

        return response;
    }

    private TossErrorResponse parseErrorResponse(final InputStream bodyStream) {
        try (InputStream errorBody = bodyStream) {
            return objectMapper.readValue(errorBody, TossErrorResponse.class);
        } catch (Exception e) {
            throw new DeserializationException("Error parsing Toss error", e);
        }
    }
}
