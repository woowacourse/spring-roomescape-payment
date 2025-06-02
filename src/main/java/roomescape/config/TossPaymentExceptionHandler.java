package roomescape.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.dto.exception.PaymentServerErrorCode;
import roomescape.exception.TossPaymentClientException;
import roomescape.exception.TossPaymentServerException;

public class TossPaymentExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        TossPaymentErrorResponse errorResponse = parseToTossPaymentErrorResponse(response.getBody());
        String code = errorResponse.code();
        String message = errorResponse.message();

        if (PaymentServerErrorCode.contains(code)) {
            throw new TossPaymentServerException(message);
        } else {
            throw new TossPaymentClientException(message);
        }
    }

    private TossPaymentErrorResponse parseToTossPaymentErrorResponse(InputStream bodyStream)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(bodyStream, TossPaymentErrorResponse.class);
    }

    private record TossPaymentErrorResponse(
            String message,
            String code
    ) {
    }
}
