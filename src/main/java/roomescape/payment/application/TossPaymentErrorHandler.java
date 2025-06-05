package roomescape.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.common.exception.impl.DeserializationException;
import roomescape.common.exception.impl.TossPaymentErrorException;
import roomescape.payment.application.dto.TossErrorResponse;

public class TossPaymentErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public TossPaymentErrorHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(final URI url, final HttpMethod method, final ClientHttpResponse response) throws IOException {
        final HttpStatus status = (HttpStatus) response.getStatusCode();
        final TossErrorResponse error = parseErrorResponse(response.getBody());

        throw new TossPaymentErrorException(status, error.code(), error.message());
    }

    private TossErrorResponse parseErrorResponse(final InputStream bodyStream) {
        try (InputStream errorBody = bodyStream) {
            return objectMapper.readValue(errorBody, TossErrorResponse.class);
        } catch (Exception e) {
            throw new DeserializationException("Error parsing Toss error", e);
        }
    }
}
