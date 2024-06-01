package roomescape.infrastructure;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.core.exception.PaymentException;

public class PaymentApproveErrorHandler implements ResponseErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(PaymentApproveErrorHandler.class);

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        logger.error("결제 승인 과정에서 문제가 발생했습니다. 상태 코드: {}, 상태 텍스트: {}",
                response.getStatusCode(), response.getStatusText());
        throw new PaymentException(response.getStatusCode(), "결제 승인 과정에서 문제가 발생했습니다.");
    }
}
