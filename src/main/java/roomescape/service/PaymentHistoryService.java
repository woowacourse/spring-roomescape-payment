package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.PaymentHistory;
import roomescape.domain.Reservation;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.dto.business.PaymentResult;
import roomescape.exception.PaymentException;
import roomescape.repository.PaymentHistoryRepository;
import roomescape.utility.PaymentClient;

@Service
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentClient paymentClient;

    public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository, PaymentClient paymentClient) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.paymentClient = paymentClient;
    }

    public PaymentHistory pay(Reservation reservation, PaymentHistoryCreationContent content) {
        PaymentResult paymentResult = requestPay(content);
        PaymentHistory paymentHistory = PaymentHistory.createWithoutId(
                reservation, paymentResult.orderId(), paymentResult.paymentKey(), paymentResult.paymentType());
        return paymentHistoryRepository.save(paymentHistory);
    }

    private PaymentResult requestPay(PaymentHistoryCreationContent content) {
        try {
            return paymentClient.pay(content.paymentKey(), content.orderId(), content.amount());
        } catch (Exception exception) {
            throw new PaymentException("결제 승인에 실패했습니다.");
        }

    }
}
