package roomescape.payment.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import roomescape.global.entity.BaseEntity;
import roomescape.reservation.domain.MemberReservation;

@Entity
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Embedded
    private PayAmount amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_RESERVATION_ID")
    private MemberReservation memberReservation;

    public Payment(String paymentKey, PaymentType paymentType, PayAmount amount, MemberReservation memberReservation) {
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
        this.amount = amount;
        this.memberReservation = memberReservation;
    }

    protected Payment() {
    }

    public static Payment from(String paymentKey, String paymentType, long amount,
                               MemberReservation memberReservation) {
        return new Payment(paymentKey, PaymentType.from(paymentType), PayAmount.from(amount), memberReservation);
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public PayAmount getAmount() {
        return amount;
    }

    public MemberReservation getMemberReservation() {
        return memberReservation;
    }
}

