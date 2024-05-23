package roomescape.payment;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import roomescape.LoggerUtil;
import roomescape.reservation.Reservation;

@Service
public class PaymentService {
    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;
    private static final Logger logger = LoggerUtil.getLogger(PaymentService.class);

    public PaymentService(TossPaymentClient tossPaymentClient, PaymentRepository paymentRepository) {
        this.tossPaymentClient = tossPaymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment pay(PaymentConfirmRequest paymentConfirmRequest, Reservation reservation) {
        PaymentConfirmResponse paymentConfirmResponse = tossPaymentClient.requestPayment(paymentConfirmRequest);
        return paymentRepository.save(new Payment(paymentConfirmResponse, reservation));
    }
}
