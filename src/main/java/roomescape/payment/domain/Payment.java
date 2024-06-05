package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import roomescape.member.domain.Member;
import roomescape.registration.domain.reservation.domain.Reservation;

import java.time.LocalDateTime;

@Entity
public class Payment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String externalPaymentId;

    @Column(nullable = false)
    private String externalOrderId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected Payment() {}

    public Payment(String externalPaymentId, String externalOrderId,
                   LocalDateTime createdAt, Member member, Reservation reservation) {
        this.id = null;
        this.externalPaymentId = externalPaymentId;
        this.externalOrderId = externalOrderId;
        this.createdAt = createdAt;
        this.member = member;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
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
