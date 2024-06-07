package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.request.ReservationRequest;
import roomescape.model.Member;
import roomescape.model.Reservation;

@Transactional
@Service
public class ReservationPaymentService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationPaymentService(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public Reservation payReservation(ReservationRequest request, Member member) {
        paymentService.confirmReservationPayments(request);
        Reservation reservation = reservationService.addReservation(request, member);
        paymentService.addPayment(request, reservation);
        return reservation;
    }
}
