package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.request.ReservationCancelRequest;
import roomescape.service.dto.request.ReservationCreateRequest;
import roomescape.service.dto.response.ReservationResponse;

@Service
public class ReservationPaymentFacadeService {
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final PaymentClient paymentClient;

    public ReservationPaymentFacadeService(ReservationService reservationService,
                                           PaymentService paymentService,
                                           PaymentClient paymentClient) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.paymentClient = paymentClient;
    }

    public ReservationResponse addReservation(ReservationCreateRequest request) {
        Reservation reservation = reservationService.addReservation(request);
        paymentService.addPayment(request.toPaymentCreateRequest(reservation));
        try {
            paymentClient.pay(request.toPaymentConfirmRequest());
            return ReservationResponse.from(reservation);
        } catch (RuntimeException e) { // todo PaymentException
            paymentService.deleteByReservation(reservation);
            reservationService.delete(reservation);
            throw e;
        }
    }

    public void cancelReservation(ReservationCancelRequest request) {
        Reservation canceledReservation = reservationService.cancel(request.id());
        Payment deletedPayment = paymentService.deleteByReservation(canceledReservation);
        if (deletedPayment.isNotAccountTransfer()) {
            PaymentCancelRequest paymentCancelRequest = request.toPaymentCancelRequest(deletedPayment.getPaymentKey());
            invokeClient(paymentCancelRequest, canceledReservation, deletedPayment);
        }
    }

    private void invokeClient(PaymentCancelRequest request, Reservation cenceledReservation, Payment deletedPayment) {
        try {
            paymentClient.cancel(request);
        } catch (RuntimeException e) { // todo PaymentException
            reservationService.rollbackCancellation(cenceledReservation);
            paymentService.rollbackDelete(deletedPayment);
            throw e;
        }
    }
}
