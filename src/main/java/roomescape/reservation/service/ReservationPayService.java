package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.UserReservationCreateRequest;

@Service
public class ReservationPayService {
    private final ReservationCreateService reservationCreateService;
    private final ReservationDeleteService reservationDeleteService;
    private final PaymentService paymentService;

    public ReservationPayService(ReservationCreateService reservationCreateService,
                                 ReservationDeleteService reservationDeleteService,
                                 PaymentService paymentService) {
        this.reservationCreateService = reservationCreateService;
        this.reservationDeleteService = reservationDeleteService;
        this.paymentService = paymentService;
    }

    public ReservationResponse createReservation(UserReservationCreateRequest request, Long memberId) {
        ReservationResponse reservation = reservationCreateService.createReservation(request, memberId);

        try {
            paymentService.confirmPayment(PaymentConfirmRequest.from(request));
        } catch (PaymentException exception) {
            reservationDeleteService.deleteReservation(reservation.id());
            throw exception;
        }
        return reservation;
    }
}
