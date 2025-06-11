package roomescape.business.model.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import roomescape.business.model.vo.Id;
import roomescape.business.model.vo.ReservationDate;
import roomescape.business.model.vo.ReservationStatus;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@Getter
@Entity
public class Reservation {

    @EmbeddedId
    private final Id id;

    @ManyToOne
    private Member member;

    @Embedded
    private ReservationDate date;

    @ManyToOne
    private TimeSlot timeSlot;

    @ManyToOne
    private Theme theme;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    protected Reservation() {
        id = Id.issue();
    }

    public static Reservation create(final Member member, final LocalDate date, final TimeSlot time,
                                     final Theme theme) {
        return new Reservation(Id.issue(), member, ReservationDate.create(date), time, theme,
                ReservationStatus.PENDING);
    }

    public static Reservation restore(final String id, final Member member, final LocalDate date,
                                      final TimeSlot time, final Theme theme) {
        return new Reservation(Id.create(id), member, ReservationDate.restore(date), time, theme,
                ReservationStatus.DONE);
    }

    public void confirm() {
        this.status = ReservationStatus.DONE;
    }
}
