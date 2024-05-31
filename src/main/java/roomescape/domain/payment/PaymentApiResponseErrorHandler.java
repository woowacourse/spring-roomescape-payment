package roomescape.domain.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.RoomescapeException;

@Component
public class PaymentApiResponseErrorHandler implements ResponseErrorHandler {
    private final PaymentErrorParser errorParser = new PaymentErrorParser(new ObjectMapper());
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        byte[] bytes = response.getBody().readAllBytes();
        String rawResponseBody = new String(bytes);
        PaymentApiError apiError = errorParser.parse(rawResponseBody);
        logger.error("payment API call ERROR = {}", apiError);
        throw new RoomescapeException(apiError.mapToExceptionType());
    }
}
