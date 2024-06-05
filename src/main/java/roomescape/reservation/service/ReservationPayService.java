package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.service.PaymentCreateService;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.UserReservationCreateRequest;

@Service
public class ReservationPayService {
    private final ReservationCreateService reservationCreateService;
    private final ReservationDeleteService reservationDeleteService;
    private final PaymentCreateService paymentCreateService;

    public ReservationPayService(ReservationCreateService reservationCreateService,
                                 ReservationDeleteService reservationDeleteService,
                                 PaymentCreateService paymentCreateService) {
        this.reservationCreateService = reservationCreateService;
        this.reservationDeleteService = reservationDeleteService;
        this.paymentCreateService = paymentCreateService;
    }

    public ReservationResponse createReservation(UserReservationCreateRequest request, Long memberId) {
        ReservationResponse reservation = reservationCreateService.createReservation(request, memberId);
        PaymentConfirmRequest paymentConfirmRequest = PaymentConfirmRequest.from(request, reservation.id());
        try {
            paymentCreateService.confirmPayment(paymentConfirmRequest);
        } catch (PaymentException exception) {
            reservationDeleteService.deleteReservation(reservation.id());
            throw exception;
        }
        return reservation;
    }
}
