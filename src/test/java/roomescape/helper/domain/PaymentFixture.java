package roomescape.helper.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentInfo;
import roomescape.domain.payment.PaymentMethod;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.reservation.Reservation;

@Component
public class PaymentFixture {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment createPayment(Reservation reservation) {
        PaymentInfo info = new PaymentInfo(
                "paymentKey",
                PaymentType.NORMAL,
                "orderId",
                "orderName",
                "KRW",
                PaymentMethod.SIMPLE_PAYMENT,
                1000L,
                PaymentStatus.DONE
        );
        Payment payment = new Payment(info, reservation);
        return paymentRepository.save(payment);
    }
}
