package roomescape.payment.domain;

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
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Reservation reservation;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false, unique = true)
    private Long amount;

    private String method;
    private String cardNumber;
    private String cardApprovedNo;
    private String easyPayProvider;
    private String receiptUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    @Builder
    public Payment(final Long id,
                   @NonNull final Member member,
                   @NonNull final Reservation reservation,
                   @NonNull final String paymentKey,
                   @NonNull final String orderId,
                   @NonNull final Long amount,
                   @NonNull final PaymentStatus status) {
        this.id = id;
        this.member = member;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public void updateStatusTo(PaymentStatus status) {
        this.status = status;
    }

    public void updateConfirmedInfo(String method, String cardNumber, String cardApprovedNo, String easyPayProvider, String receiptUrl) {
        this.method = method;
        this.cardNumber = cardNumber;
        this.cardApprovedNo = cardApprovedNo;
        this.easyPayProvider = easyPayProvider;
        this.receiptUrl = receiptUrl;
    }
}
