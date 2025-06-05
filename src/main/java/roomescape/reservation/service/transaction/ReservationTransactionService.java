package roomescape.reservation.service.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.service.ReservationService;

@Service
public class ReservationTransactionService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationTransactionService(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @Transactional
    public Payment createPaymentAndReservation(final ReservationCreateRequest request, final Long memberId) {
        Reservation reservation = reservationService.createReservation(request.reservation(), memberId);
        return paymentService.createPayment(request.payment(), reservation);
    }
}
