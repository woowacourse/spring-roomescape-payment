package roomescape.payment.global.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.payment.global.PgPaymentRestClient;
import roomescape.payment.global.domain.dto.PgPaymentRequestDto;
import roomescape.payment.global.domain.dto.PgPaymentDataDto;

@Slf4j
@Service
public class PgPaymentService {

    private final PgPaymentRestClient pgPaymentRestClient;

    public PgPaymentService(PgPaymentRestClient pgPaymentRestClient) {
        this.pgPaymentRestClient = pgPaymentRestClient;
    }

    public PgPaymentDataDto approve(PgPaymentRequestDto request) {
        log.info("결제 승인 요청 - PaymentKey: {}, OrderId: {}, 금액: {}",
                request.paymentKey(), request.orderId(), request.amount());
        PgPaymentDataDto response = pgPaymentRestClient.confirmPayment(request);
        log.info("결제 승인 완료 - 응답: {}", response);
        return response;
    }
}
