package roomescape.helper.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.service.payment.PaymentStatus;

@Component
public class PaymentFixture {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment createPayment(Reservation reservation) {
        Payment payment = new Payment("paymentKey", "orderId", "orderName", 0,
                PaymentStatus.DONE, reservation);
        return paymentRepository.save(payment);
    }
}
