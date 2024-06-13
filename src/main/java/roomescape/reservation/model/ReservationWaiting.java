package roomescape.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import roomescape.member.model.Member;

@Entity
@SQLDelete(sql = "UPDATE reservation_waiting SET is_deleted = true WHERE id = ?")
@SQLRestriction(value = "is_deleted = false")
public class ReservationWaiting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ReservationTime time;

    @ManyToOne
    private Theme theme;

    @ManyToOne
    private Member member;

    @Embedded
    private ReservationDate date;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isDeleted;

    public ReservationWaiting(
            final ReservationTime time,
            final Theme theme,
            final Member member,
            final ReservationDate date,
            final LocalDateTime createdAt
    ) {
        this(
                null,
                time,
                theme,
                member,
                date,
                createdAt
        );
    }

    public ReservationWaiting(
            final Long id,
            final ReservationTime time,
            final Theme theme,
            final Member member,
            final ReservationDate date,
            final LocalDateTime createdAt
    ) {
        this.id = id;
        this.date = date;
        this.member = member;
        this.theme = theme;
        this.time = time;
        this.createdAt = createdAt;
        this.isDeleted = false;
    }

    protected ReservationWaiting() {
    }

    public Reservation makeReservation() {
        return new Reservation(
                ReservationStatus.RESERVATION,
                date.getValue(),
                time,
                theme,
                member,
                PaymentStatus.WAITING
        );
    }

    public ReservationDate getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Theme getTheme() {
        return theme;
    }

    public ReservationTime getTime() {
        return time;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
