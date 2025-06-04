package roomescape.domain.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.BaseEntity;
import roomescape.domain.member.Member;
import roomescape.infrastructure.error.exception.ReservationException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    private static final int MIN_TIME_BEFORE_RESERVATION = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id")
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public Reservation(final Member member, final LocalDate date, final ReservationTime time, final Theme theme) {
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.status = ReservationStatus.RESERVE;
    }

    public void validateReservable(final LocalDateTime currentDateTime) {
        final LocalDateTime reservationDateTime = LocalDateTime.of(date, time.getStartAt());
        if (reservationDateTime.isBefore(currentDateTime)) {
            throw new ReservationException("지난 날짜와 시간에 대한 예약은 불가능합니다.");
        }
        final Duration duration = Duration.between(currentDateTime, reservationDateTime);
        if (duration.toMinutes() < MIN_TIME_BEFORE_RESERVATION) {
            throw new ReservationException("예약 시간까지 %d분도 남지 않아 예약이 불가능합니다.".formatted(MIN_TIME_BEFORE_RESERVATION));
        }
    }

    public boolean isEqualThemeId(final Long themeId) {
        return theme.getId().equals(themeId);
    }
}
