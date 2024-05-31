package roomescape.domain.payment;

import java.io.IOException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class PaymentApiResponseErrorHandler implements ResponseErrorHandler {
    private final PaymentErrorParser errorParser;

    public PaymentApiResponseErrorHandler(PaymentErrorParser errorParser) {
        this.errorParser = errorParser;
    }

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
        throw new ApiCallException(apiError);
    }
}
