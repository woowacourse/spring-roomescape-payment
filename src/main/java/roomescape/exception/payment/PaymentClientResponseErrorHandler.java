package roomescape.exception.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.service.payment.dto.PaymentConfirmFailOutput;

@Component
public class PaymentClientResponseErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(PaymentClientResponseErrorHandler.class);

    public PaymentClientResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().isError()) {
            throw new PaymentConfirmException(getPaymentConfirmErrorCode(response));
        }
    }

    /**
     * @see <a href="https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8">
     * 결제 승인 API 에러 코드 문서</a>
     */
    private PaymentConfirmErrorCode getPaymentConfirmErrorCode(final ClientHttpResponse response) throws IOException {
        PaymentConfirmFailOutput paymentConfirmFailOutput = objectMapper.readValue(
                response.getBody(), PaymentConfirmFailOutput.class);
        logger.error("결제 승인 API 에러: {}", paymentConfirmFailOutput);
        return PaymentConfirmErrorCode.findByName(paymentConfirmFailOutput.code());
    }
}
