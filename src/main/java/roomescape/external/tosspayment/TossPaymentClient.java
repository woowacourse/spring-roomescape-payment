package roomescape.external.tosspayment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.custom.reason.payment.PaymentException;
import roomescape.external.tosspayment.dto.PaymentConfirmRequest;

import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class TossPaymentClient {

    private static final String TEST_WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private static final String ENCODED_SECRET_KEY = Base64.getEncoder().encodeToString(TEST_WIDGET_SECRET_KEY.getBytes());
    private static final String URL_PREFIX = "https://api.tosspayments.com/v1/payments";

    private final RestClient restClient;
    private final TossPaymentConfirmErrorHandler tossPaymentConfirmErrorHandler;

    public void confirm(final PaymentConfirmRequest request) {
        try {
            long startTime = System.currentTimeMillis();
            log.info("[EXTERNAL_API - TOSS_PAYMENT_CONFIRM] 토스 결제 승인 api 요청");
            ResponseEntity<Void> response = restClient.post()
                    .uri(URL_PREFIX + "/confirm")
                    .header("Authorization", "Basic " + ENCODED_SECRET_KEY)
                    .body(request)
                    .retrieve()
                    .onStatus(tossPaymentConfirmErrorHandler)
                    .toBodilessEntity();
            if (response.getStatusCode() != HttpStatus.OK) {
                log.warn("[EXTERNAL_API_ERROR - TOSS_PAYMENT_CONFIRM] TOSSPAYMENT_BUSINESS_FAILURE - 토스 응답 상태 코드: {}",
                        response.getStatusCode());
                throw new PaymentException("결제 승인에 실패하였습니다.");
            }
            long duration = System.currentTimeMillis() - startTime;
            log.info("[EXTERNAL_API - TOSS_PAYMENT_CONFIRM] 토스 결제 승인 api 요청 및 응답 성공, 응답까지 소요 시간: {} ms", duration);
        } catch (PaymentException e) {
            throw e;
        } catch (ResourceAccessException e) {
            log.error("[EXTERNAL_API_ERROR - TOSS_PAYMENT_CONFIRM] TOSSPAYMENT_RESOURCE_ACCESS_FAILURE", e);
            throw new PaymentException("결제 승인에 실패하였습니다.");
        } catch (Exception e) {
            log.error("[EXTERNAL_API_ERROR - TOSS_PAYMENT_CONFIRM] UNEXPECTED_ERROR", e);
            throw new PaymentException("결제 승인에 실패하였습니다.");
        }
    }
}
