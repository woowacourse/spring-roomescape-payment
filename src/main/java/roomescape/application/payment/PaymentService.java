package roomescape.application.payment;

import org.springframework.stereotype.Component;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;
import roomescape.domain.reservation.Reservation;

@Component
public class PaymentService {
    private final PaymentClient paymentClient;
    private final ReservationPaymentRepository reservationPaymentRepository;

    public PaymentService(PaymentClient paymentClient,
                          ReservationPaymentRepository reservationPaymentRepository) {
        this.paymentClient = paymentClient;
        this.reservationPaymentRepository = reservationPaymentRepository;
    }

    public ReservationPayment purchase(Reservation reservation, PaymentRequest request) {
        Payment payment = paymentClient.requestPurchase(request);
        ReservationPayment reservationPayment = new ReservationPayment(
                payment.orderId(), reservation, request.paymentKey(), payment.totalAmount()
        );
        return reservationPaymentRepository.save(reservationPayment);
    }
}
