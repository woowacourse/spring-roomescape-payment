package roomescape.reservation.application;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import roomescape.reservation.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;

@Component
public class ReservationScheduler {

    private final ReservationService reservationService;
    private final Duration validReservationTimePeriod;

    public ReservationScheduler(
        ReservationService reservationService,
        @Value("${scheduler.valid.period}") Duration validReservationTimePeriod
    ) {
        this.reservationService = reservationService;
        this.validReservationTimePeriod = validReservationTimePeriod;
    }

    @Scheduled(fixedRateString = "${scheduler.request.time}")
    public void checkReservationPaymentExpiredTime() {
        LocalDateTime findTime = LocalDateTime.now().minus(validReservationTimePeriod);
        List<Reservation> reservations = reservationService.findByCreateTimeAndPaymentStatus(
            findTime, PaymentStatus.PENDING);
        for (Reservation reservation : reservations) {
            reservationService.deleteById(reservation.getId());
        }
    }
}
