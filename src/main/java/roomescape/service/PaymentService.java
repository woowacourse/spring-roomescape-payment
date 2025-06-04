package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.PaymentHistory;
import roomescape.domain.PaymentResult;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.exception.aspect.ReservationLogging;
import roomescape.external.payment.PaymentClient;
import roomescape.repository.PaymentHistoryRepository;
import roomescape.repository.PaymentResultRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentClient paymentClient;
    private final PaymentResultRepository paymentResultRepository;

    public PaymentService(PaymentHistoryRepository paymentHistoryRepository, PaymentClient paymentClient,
                          PaymentResultRepository paymentResultRepository) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.paymentClient = paymentClient;
        this.paymentResultRepository = paymentResultRepository;
    }

    @ReservationLogging
    public PaymentResult pay(PaymentHistoryCreationContent content) {
        return requestPay(content);
    }

    public void writePaymentHistory(PaymentHistoryCreationContent content) {
        PaymentHistory paymentHistory = PaymentHistory.createWithoutId(content.orderId(),
                content.paymentKey(), content.paymentType());
        paymentHistoryRepository.save(paymentHistory);
    }

    @ReservationLogging
    private PaymentResult requestPay(PaymentHistoryCreationContent content) {
        PaymentResult payResult = paymentClient.pay(content.paymentKey(), content.orderId(), content.amount(),
                content.paymentType());
        return paymentResultRepository.save(payResult);
    }
}
