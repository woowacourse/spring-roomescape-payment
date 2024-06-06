package roomescape.component;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.dto.payment.TossPaymentErrorResponse;
import roomescape.exception.RoomescapeException;
import roomescape.exception.TossPaymentErrorCode;

@Component
public class TossPaymentResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public TossPaymentResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        var errorResponse = objectMapper.readValue(response.getBody(), TossPaymentErrorResponse.class);
        throw new RoomescapeException(TossPaymentErrorCode.from(errorResponse));
    }
}
