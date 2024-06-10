package roomescape.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.service.dto.resonse.PaymentErrorResponse;

public class PaymentErrorHandler implements ResponseErrorHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().is2xxSuccessful();
    }

    // 결제 승인 에러 코드 https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse errorResponse = objectMapper.readValue(response.getBody(), PaymentErrorResponse.class);
        log.error("토스 결제 중 에러 발생 : {}", errorResponse);
        TossPaymentExceptionCode translatedExceptionCode = TossPaymentExceptionCode.from(errorResponse.code());

        throw new TossPaymentException(translatedExceptionCode);
    }
}
