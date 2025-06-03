package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistrationSlot {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(nullable = false)
    private LocalDate date;

    @Builder
    public RegistrationSlot(
            @NonNull ReservationTime time,
            @NonNull Theme theme,
            @NonNull LocalDate date) {
        this.time = time;
        this.theme = theme;
        this.date = date;
    }

    public boolean isPast() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bookingDateTime = LocalDateTime.of(date, time.getStartAt());
        return bookingDateTime.isBefore(now);
    }
} 
