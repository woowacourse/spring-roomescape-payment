package roomescape.application;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationDetail;
import roomescape.presentation.request.CreateReservationRequest;

@Service
@AllArgsConstructor
public class RoomescapeService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final UserService userService;

    @Transactional
    public Reservation reserveAndPay(final long userId, final CreateReservationRequest request) {
        var reservation = reservationService.reserve(userId, request.date(), request.timeId(), request.themeId());
        var payment = paymentService.pay(request.paymentKey(), request.orderId(), request.amount());
        reservation.updatePaymentId(payment.id());
        return reservation;
    }

    public List<ReservationDetail> getAllReservationsByUser(final long userId) {
        var reservations = userService.getMyReservations(userId);
        return reservationService.getReservationDetails(reservations);
    }
}
