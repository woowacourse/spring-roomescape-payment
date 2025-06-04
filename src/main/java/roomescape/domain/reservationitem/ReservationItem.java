package roomescape.domain.reservationitem;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.reservation.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @OneToMany(mappedBy = "reservationItem")
    private List<Reservation> reservations = new ArrayList<>();

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

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public int calculatePriorityOf(Reservation reservation) {
        return reservations.indexOf(reservation);
    }

    public Optional<Reservation> getNextReservationOf(Reservation reservation) {
        int curIndex = reservations.indexOf(reservation);
        if (curIndex >= reservations.size() - 1) {
            return Optional.empty();
        }
        return Optional.ofNullable(reservations.get(curIndex + 1));
    }
}
