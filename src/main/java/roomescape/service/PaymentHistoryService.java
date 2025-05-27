package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.PaymentHistory;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.repository.PaymentHistoryRepository;

@Service
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository) {
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    public PaymentHistory pay(long memberId, PaymentHistoryCreationContent paymentHistoryCreationContent) {

        //TODO restTemplate 등 작성
        return null;
    }
}
