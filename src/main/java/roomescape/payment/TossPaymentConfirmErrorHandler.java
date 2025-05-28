package roomescape.payment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.custom.reason.payment.PaymentException;
import roomescape.payment.dto.TossErrorResponse;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
public class TossPaymentConfirmErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final URI url, final HttpMethod method, final ClientHttpResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            TossErrorResponse errorResponse = objectMapper.readValue(response.getBody(), TossErrorResponse.class);
            TossErrorCode.fromCode(errorResponse.code())
                    .ifPresentOrElse(errorCode -> {
                        if (errorCode.isUserVisible()) {
                            throw new PaymentException("결제 승인 실패: " + errorResponse.message());
                        }
                        log.info("결제 승인 실패: {}", errorResponse);
                        throw new PaymentException("결제 승인에 실패하였습니다.");
                    }, () -> {
                        log.warn("결제 승인 실패 (예상치 못한 에러): {}", errorResponse);
                        throw new PaymentException("예상치 못한 오류로 인해 결제 승인에 실패하였습니다.");
                    });
        } catch (IOException ioException) {
            throw new PaymentException("결제 승인에 실패하였습니다.", ioException);
        }
    }
}
