package roomescape.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.presentation.request.CreateReservationRequest;

@Service
@AllArgsConstructor
public class RoomescapeService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @Transactional
    public Reservation reserveAndPay(final long userId, final CreateReservationRequest request) {
        var reservation = reservationService.reserve(userId, request.date(), request.timeId(), request.themeId());
        paymentService.pay(request.paymentKey(), request.orderId(), request.amount(), reservation.id());
        return reservation;
    }
}
