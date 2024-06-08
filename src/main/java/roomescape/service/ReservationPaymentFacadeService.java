package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.service.dto.request.CreateReservationRequest;
import roomescape.service.dto.request.PaymentRequest;
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
        Payment payment = paymentService.addPayment(paymentRequest, reservation);
        try {
            paymentClient.pay(paymentRequest);
            return ReservationResponse.from(reservation);
        } catch (RuntimeException e) { // todo PaymentException
            paymentService.deletePaymentById(payment.getId());
            reservationService.deleteReservationById(reservation.getId());
            throw e;
        }
    }
}
