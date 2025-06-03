package roomescape.payment.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.exception.PaymentClientException;
import roomescape.payment.exception.PaymentForbiddenException;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.exception.PaymentUnauthorizedException;
import roomescape.payment.presentation.dto.response.TossErrorResponse;

@Component
public class PaymentExceptionHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public PaymentExceptionHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is5xxServerError() ||
                response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(final URI url, final HttpMethod method, final ClientHttpResponse response)
            throws IOException {
        TossErrorResponse errorResponse = objectMapper.readValue(response.getBody(), TossErrorResponse.class);
        String message = errorResponse.message();
        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.is4xxClientError()) {
            switch (statusCode.value()) {
                case 401 -> throw new PaymentUnauthorizedException(message);
                case 403 -> throw new PaymentForbiddenException(message);
                default -> throw new PaymentClientException(message);
            }
        }
        throw new PaymentServerException(message);
    }
}
