package roomescape.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import roomescape.common.exception.PaymentException;

public class PaymentErrorHandler extends DefaultResponseErrorHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }

        final String body = new String(getResponseBody(response), StandardCharsets.UTF_8);
        final PaymentError paymentError = mapper.readValue(body, PaymentError.class);
        throw new PaymentException(response.getStatusCode(), paymentError);
    }
}
