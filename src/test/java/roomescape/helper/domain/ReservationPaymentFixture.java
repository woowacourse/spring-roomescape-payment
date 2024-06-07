package roomescape.helper.domain;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.domain.payment.PaymentInfo;
import roomescape.domain.payment.PaymentMethod;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;
import roomescape.domain.reservation.Reservation;

@Component
public class ReservationPaymentFixture {
    @Autowired
    private ReservationPaymentRepository reservationPaymentRepository;

    public ReservationPayment createReservationPayment(Reservation reservation) {
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
        ReservationPayment reservationPayment = new ReservationPayment(info, reservation);
        return reservationPaymentRepository.save(reservationPayment);
    }

    public List<ReservationPayment> findAllReservationPayment() {
        return reservationPaymentRepository.findAll();
    }
}
