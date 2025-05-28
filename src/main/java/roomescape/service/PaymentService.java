package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.PaymentHistory;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.dto.business.PaymentResult;
import roomescape.repository.PaymentHistoryRepository;
import roomescape.utility.PaymentClient;

@Service
public class PaymentService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentHistoryRepository paymentHistoryRepository, PaymentClient paymentClient) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.paymentClient = paymentClient;
    }

    public PaymentHistory pay(PaymentHistoryCreationContent content) {
        PaymentResult paymentResult = requestPay(content);
        PaymentHistory paymentHistory = PaymentHistory.createWithoutId(paymentResult.orderId(),
                paymentResult.paymentKey(), content.paymentType());
        return paymentHistoryRepository.save(paymentHistory);
    }

    private PaymentResult requestPay(PaymentHistoryCreationContent content) {
        return paymentClient.pay(content.paymentKey(), content.orderId(), content.amount());
    }
}
