package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.exception.PaymentException;
import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.request.ReservationCancelRequest;
import roomescape.service.dto.request.ReservationCreateRequest;
import roomescape.service.dto.response.ReservationResponse;

@Service
public class ReservationPaymentFacadeService {
    private final ReservationManageService reservationManageService;
    private final PaymentService paymentService;
    private final PaymentClient paymentClient;

    public ReservationPaymentFacadeService(ReservationManageService reservationManageService,
                                           PaymentService paymentService,
                                           PaymentClient paymentClient) {
        this.reservationManageService = reservationManageService;
        this.paymentService = paymentService;
        this.paymentClient = paymentClient;
    }

    public ReservationResponse addReservation(ReservationCreateRequest request) {
        Reservation reservation = reservationManageService.addReservation(request);
        paymentService.addPayment(request.toPaymentCreateRequest(reservation));
        try {
            paymentClient.pay(request.toPaymentConfirmRequest());
            return ReservationResponse.from(reservation);
        } catch (PaymentException e) {
            paymentService.deleteByReservation(reservation);
            reservationManageService.delete(reservation);
            throw e;
        }
    }

    public void cancelReservation(ReservationCancelRequest request) {
        Reservation canceledReservation = reservationManageService.cancel(request.id());
        Payment deletedPayment = paymentService.deleteByReservation(canceledReservation);
        if (deletedPayment.isNotAccountTransfer()) {
            PaymentCancelRequest paymentCancelRequest = request.toPaymentCancelRequest(deletedPayment.getPaymentKey());
            invokeClient(paymentCancelRequest, canceledReservation, deletedPayment);
        }
    }

    private void invokeClient(PaymentCancelRequest request, Reservation cenceledReservation, Payment deletedPayment) {
        try {
            paymentClient.cancel(request);
        } catch (PaymentException e) {
            reservationManageService.rollbackCancellation(cenceledReservation);
            paymentService.rollbackDelete(deletedPayment);
            throw e;
        }
    }
}
