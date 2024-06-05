package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.exception.PaymentException;
import roomescape.payment.service.PaymentClient;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.UserReservationCreateRequest;

@Service
public class ReservationPayService {
    private final ReservationCreateService reservationCreateService;
    private final ReservationDeleteService reservationDeleteService;
    private final PaymentClient paymentClient;

    public ReservationPayService(ReservationCreateService reservationCreateService,
                                 ReservationDeleteService reservationDeleteService,
                                 PaymentClient paymentClient) {
        this.reservationCreateService = reservationCreateService;
        this.reservationDeleteService = reservationDeleteService;
        this.paymentClient = paymentClient;
    }

    public ReservationResponse createReservation(UserReservationCreateRequest request, Long memberId) {
        ReservationResponse reservation = reservationCreateService.createReservation(request, memberId);

        try {
            paymentClient.confirmPayment(PaymentConfirmRequest.from(request));
        } catch (PaymentException exception) {
            reservationDeleteService.deleteReservation(reservation.id());
            throw exception;
        }
        return reservation;
    }
}
