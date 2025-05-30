package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.PaymentHistory;
import roomescape.domain.PaymentResult;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.repository.PaymentHistoryRepository;
import roomescape.repository.PaymentResultRepository;
import roomescape.utility.PaymentClient;

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

    public PaymentResult pay(PaymentHistoryCreationContent content) {
        return requestPay(content);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writePaymentHistory(PaymentHistoryCreationContent content) {
        PaymentHistory paymentHistory = PaymentHistory.createWithoutId(content.orderId(),
                content.paymentKey(), content.paymentType());
        paymentHistoryRepository.save(paymentHistory);
    }

    private PaymentResult requestPay(PaymentHistoryCreationContent content) {
        PaymentResult payResult = paymentClient.pay(content.paymentKey(), content.orderId(), content.amount());
        return paymentResultRepository.save(payResult);
    }
}
