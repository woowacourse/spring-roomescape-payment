package roomescape.registration.domain.waiting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.data.annotation.CreatedDate;
import roomescape.registration.domain.reservation.domain.Reservation;

import java.time.LocalDateTime;

@Entity
public class Waiting {

    private static final int NULL_ID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", referencedColumnName = "id", nullable = false)
    private Reservation reservation;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public Waiting() {
    }

    public Waiting(Reservation reservation, LocalDateTime createdAt) {
        this.id = NULL_ID;
        this.reservation = reservation;
        this.createdAt = createdAt;
    }

    public Waiting(long id, Reservation reservation, LocalDateTime createdAt) {
        this.id = id;
        this.reservation = reservation;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
