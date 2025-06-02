package roomescape.domain.reservationitem;

import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.INVALID_RESERVATION_ITEM;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.global.exception.roomescape.RoomEscapeException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ReservationItem {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "time_id")
    private ReservationTime time;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private ReservationTheme theme;

    @Builder
    public ReservationItem(LocalDate date, ReservationTime time, ReservationTheme theme) {
        validateLocalDate(date, time);
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    private void validateLocalDate(final LocalDate date, final ReservationTime time) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime dateTime = LocalDateTime.of(date, time.getStartAt());
        if (dateTime.isBefore(now) || dateTime.isEqual(now)) {
            throw new RoomEscapeException(INVALID_RESERVATION_ITEM);
        }
    }
}
