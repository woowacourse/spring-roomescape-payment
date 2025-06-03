package roomescape.payment.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.exception.PaymentApproveException;
import roomescape.payment.presentation.dto.response.TossErrorResponse;

@Component
public class PaymentApproveExceptionHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public PaymentApproveExceptionHandler(final ObjectMapper objectMapper) {
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
        throw new PaymentApproveException(errorResponse.message(), response.getStatusCode());
    }
}
