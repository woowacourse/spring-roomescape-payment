package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import roomescape.reservation.exception.InvalidReservationTimeException;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.User;
import roomescape.waiting.domain.Waiting;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime reservationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Reservation() {
    }

    public Reservation(Long id, LocalDate date, ReservationStatus status, ReservationTime reservationTime, Theme theme,
            User user) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.reservationTime = reservationTime;
        this.theme = theme;
        this.user = user;
    }

    public static Reservation of(LocalDate date, ReservationStatus status, ReservationTime reservationTime, Theme theme,
            User user) {
        LocalDateTime dateTime = LocalDateTime.of(date, reservationTime.getStartAt());
        validateTense(dateTime);
        return new Reservation(null, date, status, reservationTime, theme, user);
    }

    public static Reservation ofWaiting(Waiting waiting) {
        return new Reservation(null, waiting.getDate(), ReservationStatus.PAYMENT_PENDING, waiting.getTime(), waiting.getTheme(), waiting.getMember());
    }

    private static void validateTense(LocalDateTime dateTime) {
        if (isPastTense(dateTime)) {
            throw new InvalidReservationTimeException("과거시점으로 예약을 진행할 수 없습니다.");
        }
    }

    private static boolean isPastTense(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        return dateTime.isBefore(now);
    }

    public boolean isSameDateTime(Reservation compare) {
        return this.getDateTime().isEqual(compare.getDateTime());
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.of(date, reservationTime.getStartAt());
    }

    public void changeStatus(ReservationStatus reservationStatus) {
        this.status = reservationStatus;
    }
}
