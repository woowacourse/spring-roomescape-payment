package roomescape.payment;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import roomescape.LoggerUtil;
import roomescape.reservation.Reservation;

@Service
public class PaymentServiceV2 {
    private final TossPaymentClientV2 tossPaymentClientV2;
    private final PaymentRepository paymentRepository;
    private static final Logger logger = LoggerUtil.getLogger(PaymentServiceV2.class);

    public PaymentServiceV2(TossPaymentClientV2 tossPaymentClientV2, PaymentRepository paymentRepository) {
        this.tossPaymentClientV2 = tossPaymentClientV2;
        this.paymentRepository = paymentRepository;
    }

    public Payment pay(PaymentConfirmRequest paymentConfirmRequest, Reservation reservation) {
        PaymentConfirmResponse paymentConfirmResponse = tossPaymentClientV2.requestPayment(paymentConfirmRequest);
        return paymentRepository.save(new Payment(paymentConfirmResponse, reservation));
    }
}
