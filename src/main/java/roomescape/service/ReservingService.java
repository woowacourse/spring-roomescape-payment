package roomescape.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.entity.Reservation;
import roomescape.global.ReservationStatus;

@Service
public class ReservingService {
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservingService(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public ReservationResponse reserveAndPay(
            CreateReservationRequest request,
            LoginMemberRequest loginMemberRequest
    ) {
        Reservation reservation = reservationService.createReservation(
                loginMemberRequest.id(),
                request.themeId(),
                request.date(),
                request.timeId(),
                ReservationStatus.RESERVED
        );

        ConfirmPaymentRequest confirmPaymentRequest = ConfirmPaymentRequest.from(request);
        paymentService.confirmPayment(confirmPaymentRequest, reservation);

        return ReservationResponse.from(reservation);
    }
}
