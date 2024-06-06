package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import roomescape.member.domain.Member;
import roomescape.registration.domain.reservation.domain.Reservation;

import java.time.LocalDateTime;

@Entity
public class Payment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String externalPaymentKey;

    @Column(nullable = false)
    private String externalOrderId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected Payment() {}

    public Payment(String externalPaymentId, String externalOrderId,
                   Member member, Reservation reservation) {
        this.id = null;
        this.createdAt = null;
        this.externalPaymentKey = externalPaymentId;
        this.externalOrderId = externalOrderId;
        this.member = member;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
    }

    public String getExternalPaymentKey() {
        return externalPaymentKey;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Member getMember() {
        return member;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
