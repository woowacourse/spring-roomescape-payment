package roomescape.config.payment;

import static roomescape.exception.RoomescapeErrorCode.INTERNAL_SERVER_ERROR;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.dto.payment.TossPaymentErrorResponse;
import roomescape.exception.RoomescapeException;
import roomescape.exception.TossPaymentErrorCode;

public class TossPaymentResponseErrorHandler implements ResponseErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
    public void handleError(final ClientHttpResponse response) {
        try {
            var errorResponse = objectMapper.readValue(response.getBody(), TossPaymentErrorResponse.class);
            logger.error(errorResponse.toString());
            throw new RoomescapeException(TossPaymentErrorCode.from(errorResponse));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RoomescapeException(INTERNAL_SERVER_ERROR);
        }
    }
}
