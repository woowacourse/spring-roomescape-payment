package roomescape.reservation.domain;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import roomescape.global.exception.BadRequestException;
import roomescape.schedule.domain.ReservationDate;
import roomescape.time.domain.ReservationTime;

@Slf4j
public class ReservationDateTime {

    private final ReservationDate reservationDate;
    private final ReservationTime reservationTime;

    public ReservationDateTime(
            final ReservationDate reservationDate,
            final ReservationTime reservationTime,
            final Clock clock
    ) {
        this.reservationDate = Objects.requireNonNull(reservationDate, "reservationDate는 null일 수 없습니다.");
        this.reservationTime = Objects.requireNonNull(reservationTime, "reservationTime은 null일 수 없습니다.");
        Objects.requireNonNull(clock, "clock은 null일 수 없습니다.");
        validatePast(this.reservationDate, this.reservationTime, clock);
    }

    private void validatePast(
            final ReservationDate reservationDate,
            final ReservationTime reservationTime,
            final Clock clock
    ) {
        LocalDateTime reservationDateTime = LocalDateTime.of(
                reservationDate.date(),
                reservationTime.getStartAt()
        );
        LocalDateTime now = LocalDateTime.now(clock);

        if (reservationDateTime.isBefore(now)) {
            throw new BadRequestException("현재 시간 이후로만 예약할 수 있습니다.");
        }
    }

    public Long getTimeId() {
        return reservationTime.getId();
    }
}
