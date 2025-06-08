package roomescape.external.tosspayment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.custom.reason.payment.PaymentException;
import roomescape.external.tosspayment.dto.TossErrorResponse;

import java.io.IOException;
import java.net.URI;

@Slf4j
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
                        log.warn("[{}] EXTERNAL_API_ERROR: TOSSPAYMENT_BUSINESS_FAILURE - 실패 사유: {}",
                                MDC.get("requestId"),
                                errorResponse.message());
                        if (errorCode.isUserVisible()) {
                            throw new PaymentException("결제 승인 실패: " + errorResponse.message());
                        }
                        throw new PaymentException("결제 승인에 실패하였습니다.");
                    }, () -> {
                        log.error("[{}] EXTERNAL_API_ERROR: TOSSPAYMENT_BUSINESS_FAILURE - 정의되지 않은 토스 응답 코드", MDC.get("requestId"));
                        throw new PaymentException("예상치 못한 오류로 인해 결제 승인에 실패하였습니다.");
                    });
        } catch (IOException e) {
            log.error("[{}] EXTERNAL_API_ERROR: TOSSPAYMENT_BUSINESS_FAILURE - 토스 응답 변환 실패", MDC.get("requestId"), e);
            throw new PaymentException("결제 승인에 실패하였습니다.", e);
        }
    }
}
