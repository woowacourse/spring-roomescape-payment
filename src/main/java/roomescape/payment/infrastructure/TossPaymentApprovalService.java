package roomescape.payment.infrastructure;

import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.payment.application.PaymentApprovalService;
import roomescape.payment.infrastructure.client.TossRestClient;

//TODO: 에러 핸들링 하기!!  (2025-05-28, 수, 15:5)
@Component
@AllArgsConstructor
public class TossPaymentApprovalService implements PaymentApprovalService {

    private final TossRestClient tossRestClient;

    @Override
    public void approvePayment(String orderId, BigDecimal amount, String paymentKey) {
        Map<String, Object> body = Map.of(
                "orderId", orderId,
                "amount", amount,
                "paymentKey", paymentKey
        );

        tossRestClient.getRestClient().post()
                .uri("/v1/payments/confirm")
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
