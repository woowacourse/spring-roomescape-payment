package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.request.ReservationRequest;
import roomescape.controller.request.ReservationWithPaymentRequest;
import roomescape.model.Member;
import roomescape.model.Reservation;

@Transactional
@Service
public class ReservationPaymentService {

    private final ReservationWriteService reservationWriteService;
    private final PaymentService paymentService;

    public ReservationPaymentService(ReservationWriteService reservationWriteService,
                                     PaymentService paymentService) {
        this.reservationWriteService = reservationWriteService;
        this.paymentService = paymentService;
    }

    public Reservation payReservation(ReservationRequest request, Member member) {
        paymentService.confirmReservationPayments(request);
        Reservation reservation = reservationWriteService.addReservation(request, member);
        paymentService.addPayment(request, reservation);
        return reservation;
    }

    public Reservation payReservationWithoutPayment(ReservationWithPaymentRequest request, Member member) {
        paymentService.confirmReservationPayments(request);
        Reservation reservation = reservationWriteService.updateReservationStatus(request, member);
        paymentService.addPayment(request, reservation);
        return reservation;
    }
}
