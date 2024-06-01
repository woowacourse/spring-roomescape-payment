package roomescape.core.controller;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.core.exception.PaymentException;

public class PaymentErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    // TODO: 로그 남기기
    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is5xxServerError() || response.getStatusCode().is4xxClientError()) {
            throw new PaymentException(response.getStatusCode(), "결제 승인 과정에서 문제가 발생했습니다.");
        }
    }
}
