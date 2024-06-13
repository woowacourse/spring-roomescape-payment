package roomescape.exception;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class TossPaymentErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is5xxServerError() || response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            throw new PaymentException("결제에 실패했습니다.");
        }
        if (response.getStatusCode().is5xxServerError()) {
            throw new PaymentException("결제 서버에 오류가 발생했습니다.");
        }
    }
}
