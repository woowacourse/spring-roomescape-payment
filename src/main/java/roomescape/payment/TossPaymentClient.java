package roomescape.payment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.custom.reason.payment.PaymentException;
import roomescape.payment.dto.PaymentConfirmRequest;

@Slf4j
@AllArgsConstructor
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final TossPaymentConfirmErrorHandler tossPaymentConfirmErrorHandler;
    private final TossPaymentProperties tossPaymentProperties;

    @Override
    public void confirm(final PaymentConfirmRequest request) {
        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri(tossPaymentProperties.getBaseUrl() + "/confirm")
                    .header("Authorization", "Basic " + tossPaymentProperties.getEncodedSecretKey())
                    .body(request)
                    .retrieve()
                    .onStatus(tossPaymentConfirmErrorHandler)
                    .toBodilessEntity();
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new PaymentException("결제 승인에 실패하였습니다.");
            }
        } catch (ResourceAccessException e) {
            log.warn("리소스 접근 에러 ", e);
            throw new PaymentException("결제 승인에 실패하였습니다.");
        }
    }
}
