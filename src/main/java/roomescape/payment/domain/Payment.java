package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    Member member;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    Reservation reservation;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long amount;

    @Builder
    public Payment(final Long id,
                   @NonNull final Member member,
                   @NonNull final Reservation reservation,
                   @NonNull final String paymentKey,
                   @NonNull final String orderId,
                   @NonNull final Long amount) {
        this.id = id;
        this.member = member;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
}
