package roomescape.payment.domain;

import jakarta.persistence.*;
import roomescape.global.entity.BaseEntity;
import roomescape.global.entity.Price;
import roomescape.reservation.domain.MemberReservation;

import java.math.BigDecimal;

@Entity
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Embedded
    private Price amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_RESERVATION_ID")
    private MemberReservation memberReservation;

    public Payment(String paymentKey, PaymentType paymentType, Price amount, MemberReservation memberReservation) {
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
        this.amount = amount;
        this.memberReservation = memberReservation;
    }

    protected Payment() {
    }

    public static Payment from(String paymentKey, String paymentType, BigDecimal amount,
                               MemberReservation memberReservation) {
        return new Payment(paymentKey, PaymentType.from(paymentType), new Price(amount), memberReservation);
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

    public Price getAmount() {
        return amount;
    }

    public MemberReservation getMemberReservation() {
        return memberReservation;
    }
}

