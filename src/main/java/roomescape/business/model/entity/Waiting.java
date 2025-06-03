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
    private Member member;

    @Embedded
    private ReservationDate date;

    @ManyToOne
    private TimeSlot time;

    @ManyToOne
    private Theme theme;

    private final LocalDateTime createdAt;

    protected Waiting() {
        id = Id.issue();
        createdAt = LocalDateTime.now();
    }

    public static Waiting create(final Member member, final LocalDate date, final TimeSlot time,
                                 final Theme theme) {
        return new Waiting(Id.issue(), member, ReservationDate.create(date), time, theme, LocalDateTime.now());
    }

    public static Waiting restore(final String id, final Member member, final LocalDate date,
                                  final TimeSlot time, final Theme theme, final LocalDateTime createdAt) {
        return new Waiting(Id.create(id), member, ReservationDate.restore(date), time, theme,
                createdAt.truncatedTo(ChronoUnit.MILLIS));
    }

    public Reservation convertToReservation() {
        return Reservation.create(member, date.value(), time, theme);
    }
}