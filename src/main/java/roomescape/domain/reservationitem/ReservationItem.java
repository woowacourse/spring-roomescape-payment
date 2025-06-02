package roomescape.domain.reservationitem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ReservationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime time;

    @ManyToOne(optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
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
            throw new IllegalArgumentException("[ERROR] 예약시간은 과거일 수 없습니다.");
        }
    }
}
