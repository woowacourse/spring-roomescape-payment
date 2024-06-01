package roomescape.infrastructure;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.core.exception.PaymentException;

public class PaymentRefundErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is5xxServerError() || response.getStatusCode().is4xxClientError()) {
            throw new PaymentException(response.getStatusCode(), "환불 과정에서 문제가 발생했습니다.");
        }
    }
}
