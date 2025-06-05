package roomescape.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.custom.reason.payment.PaymentException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.TossPaymentConfirmResponse;

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
            ResponseEntity<TossPaymentConfirmResponse> response = restClient.post()
                    .uri(URL_PREFIX + "/confirm")
                    .header("Authorization", "Basic " + ENCODED_SECRET_KEY)
                    .body(request)
                    .retrieve()
                    .onStatus(tossPaymentConfirmErrorHandler)
                    .toEntity(TossPaymentConfirmResponse.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new PaymentException("결제 승인에 실패하였습니다.");
            }
        } catch (PaymentException e) {
            throw e;
        } catch (ResourceAccessException e) {
            log.warn("리소스 접근 에러 ", e);
            throw new PaymentException("결제 승인에 실패하였습니다.");
        } catch (Exception e) {
            log.warn("예기치 못한 에러 ", e);
            throw new PaymentException("결제 승인에 실패하였습니다.");
        }
    }
}
