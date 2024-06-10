package roomescape.payment.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import roomescape.global.entity.BaseEntity;
import roomescape.payment.domain.EasyPayType;
import roomescape.payment.domain.PaymentInfo;
import roomescape.reservation.entity.Reservation;

@Entity
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Reservation reservation;
    @Column(unique = true)
    private String paymentKey;
    @Column(nullable = false)
    private String orderName;
    @Column(nullable = false)
    private String requestedAt;
    @Column(nullable = false)
    private String approvedAt;
    @Column(nullable = false)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private EasyPayType easyPayType;
    @Column(nullable = false)
    private String currency;

    protected Payment() {

    }

    public Payment(
            Long id,
            Reservation reservation,
            String paymentKey,
            String orderName,
            String requestedAt,
            String approvedAt,
            BigDecimal amount,
            EasyPayType easyPayType,
            String currency) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.amount = amount;
        this.easyPayType = easyPayType;
        this.currency = currency;
    }

    public Payment(Reservation reservation, PaymentInfo paymentInfo) {
        this(
                null,
                reservation,
                paymentInfo.paymentKey(),
                paymentInfo.orderName(),
                paymentInfo.requestedAt(),
                paymentInfo.approvedAt(),
                paymentInfo.totalAmount(),
                EasyPayType.from(paymentInfo.easyPay().provider()),
                paymentInfo.currency()
        );
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public EasyPayType getEasyPayType() {
        return easyPayType;
    }

    public String getCurrency() {
        return currency;
    }
}
