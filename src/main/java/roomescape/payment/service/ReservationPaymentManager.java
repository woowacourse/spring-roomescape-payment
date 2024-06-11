package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSaveRequest;
import roomescape.reservation.service.ReservationService;

@Service
public class ReservationPaymentManager {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationPaymentManager(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public ReservationResponse saveReservationByUser(ReservationSaveRequest reservationSaveRequest,
                                                     LoginMember loginMember) {
        ReservationResponse reservationResponse =
                reservationService.saveReservationPending(reservationSaveRequest, loginMember);
        try {
            Payment payment = paymentService.createPayment(PaymentRequest.from(reservationSaveRequest),
                    reservationResponse.id());
            paymentService.confirm(payment);
            return reservationService.confirmReservation(reservationResponse.id());
        } catch (Exception exception) {
            reservationService.rollBackPendingReservation(reservationResponse.id());
            throw exception;
        }
    }

    public void cancelReservation(Long id) {
        paymentService.cancel(reservationService.findById(id));
        reservationService.cancelReservationById(id);
    }
}
