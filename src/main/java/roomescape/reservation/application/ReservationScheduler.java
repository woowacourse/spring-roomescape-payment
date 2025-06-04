package roomescape.reservation.application;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import roomescape.reservation.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;

@Component
public class ReservationScheduler {

    private static final int REQUEST_TIME = 60_000;
    private static final int VALID_RESERVATION_TIME_PERIOD = 15;

    private final ReservationService reservationService;

    public ReservationScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(fixedRate = REQUEST_TIME)
    public void checkReservationPaymentExpiredTime() {
        LocalDateTime findTime = LocalDateTime.now().minusMinutes(VALID_RESERVATION_TIME_PERIOD);
        List<Reservation> reservations = reservationService.findByCreateTimeAndPaymentStatus(
            Timestamp.valueOf(findTime), PaymentStatus.PENDING);
        for (Reservation reservation : reservations) {
            reservationService.deleteById(reservation.getId());
        }
    }
}
