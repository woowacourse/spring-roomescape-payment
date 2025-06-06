package roomescape.service;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.external.lock.ReservationLockGuard;

@Component
public class ReservationLockService {

    private final ReservationLockGuard reservationLockGuard;

    public ReservationLockService(ReservationLockGuard reservationLockGuard) {
        this.reservationLockGuard = reservationLockGuard;
    }

    @Transactional
    public void doPessimisticLock(Theme theme, ReservationTime time, LocalDate date) {
        String lockKey = reservationLockGuard.lockCreate(date, theme, time);
        reservationLockGuard.doPessimisticLock(lockKey);
    }
}
