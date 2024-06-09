package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.service.dto.request.CreateReservationRequest;
import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.request.PaymentRequest;
import roomescape.service.dto.request.ReservationCancelRequest;
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

    public ReservationResponse addReservation(CreateReservationRequest reservationRequest, PaymentRequest paymentRequest) {
        Reservation reservation = reservationService.addReservation(reservationRequest);
        paymentService.addPayment(paymentRequest, reservation);
        try {
            paymentClient.pay(paymentRequest);
            return ReservationResponse.from(reservation);
        } catch (RuntimeException e) { // todo PaymentException
            paymentService.deleteByReservation(reservation);
            reservationService.deleteReservationById(reservation.getId());
            throw e;
        }
    }

    public void cancelReservation(ReservationCancelRequest request) {
        Reservation deletedReservation = reservationService.deleteReservationById(request.id());
        Payment deletedPayment = paymentService.deleteByReservation(deletedReservation);
        if (deletedPayment.isNotAccountTransfer()) {
            invokeClient(request, deletedReservation, deletedPayment);
        }
    }

    private void invokeClient(ReservationCancelRequest request, Reservation deletedReservation, Payment deletedPayment) {
        PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest(deletedPayment.getPaymentKey(), request.cancelReason());
        try {
            paymentClient.cancel(paymentCancelRequest);
        } catch (RuntimeException e) { // todo PaymentException
            reservationService.rollbackDelete(deletedReservation);
            paymentService.rollbackDelete(deletedPayment);
            throw e;
        }
    }
}
