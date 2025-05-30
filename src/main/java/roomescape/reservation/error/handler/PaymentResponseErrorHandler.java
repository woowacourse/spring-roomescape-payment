package roomescape.reservation.error.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.reservation.dto.response.PaymentErrorResponse;
import roomescape.reservation.error.exception.PaymentClientException;
import roomescape.reservation.error.exception.PaymentServerException;

@Component
public class PaymentResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        InputStream inputStream = response.getBody();
        PaymentErrorResponse paymentErrorResponse = parseErrorResponse(inputStream);
        if (response.getStatusCode().is4xxClientError()) {
            throw new PaymentClientException(response.getStatusCode(), paymentErrorResponse);
        }
        if (response.getStatusCode().is5xxServerError()) {
            throw new PaymentServerException(response.getStatusCode(), paymentErrorResponse);
        }
    }

    private PaymentErrorResponse parseErrorResponse(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(inputStream, PaymentErrorResponse.class);
    }
}
