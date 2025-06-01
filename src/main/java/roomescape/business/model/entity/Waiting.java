package roomescape.business.model.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import roomescape.business.model.vo.Id;
import roomescape.business.model.vo.ReservationDate;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Getter
@Entity
@Table(name = "waiting")
public class Waiting {

    @EmbeddedId
    private final Id id;

    @ManyToOne
    private User user;

    @Embedded
    private ReservationDate date;

    @ManyToOne
    private ReservationTime time;

    @ManyToOne
    private Theme theme;

    private final LocalDateTime createdAt;

    protected Waiting() {
        id = Id.issue();
        createdAt = LocalDateTime.now();
    }

    public static Waiting create(final User user, final LocalDate date, final ReservationTime time,
                                 final Theme theme) {
        return new Waiting(Id.issue(), user, ReservationDate.create(date), time, theme, LocalDateTime.now());
    }

    public static Waiting restore(final String id, final User user, final LocalDate date,
                                  final ReservationTime time, final Theme theme, final LocalDateTime createdAt) {
        return new Waiting(Id.create(id), user, ReservationDate.restore(date), time, theme,
                createdAt.truncatedTo(ChronoUnit.MILLIS));
    }

    public Reservation convertToReservation() {
        return Reservation.create(user, date.value(), time, theme);
    }
}