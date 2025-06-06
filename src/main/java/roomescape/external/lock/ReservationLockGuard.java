package roomescape.external.lock;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

@Component
public class ReservationLockGuard {

    private static final String LOCK_KEY_FORMAT = "%s_%s_%s";

    private final RoomescapeLockRepository roomescapeLockRepository;

    public ReservationLockGuard(RoomescapeLockRepository roomescapeLockRepository) {
        this.roomescapeLockRepository = roomescapeLockRepository;
    }

    private String createLockName(LocalDate date, Theme theme, ReservationTime time) {
        String themeId = String.valueOf(theme.getId());
        String reservationTimeId = String.valueOf(time.getId());
        return String.format(LOCK_KEY_FORMAT, themeId, date.toString(), reservationTimeId);
    }

    @Transactional
    public String lockCreate(LocalDate date, Theme theme, ReservationTime time) {
        String lockKey = createLockName(date, theme, time);
        RoomescapeLock lock = roomescapeLockRepository.save(new RoomescapeLock(lockKey));
        return lock.getLockId();
    }

    @Transactional
    public void doPessimisticLock(String lockKey) {
        roomescapeLockRepository.findById(lockKey);
    }
}

