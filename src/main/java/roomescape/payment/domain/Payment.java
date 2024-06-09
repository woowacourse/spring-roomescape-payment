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
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import roomescape.common.domain.AuditedEntity;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment extends AuditedEntity {

    private static final BigDecimal MIN_AMOUNT_RANGE = BigDecimal.ZERO;
    private static final BigDecimal MAX_AMOUNT_RANGE = BigDecimal.valueOf(Integer.MAX_VALUE);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentCurrency currency;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    protected Payment() {
    }

    public Payment(
            Reservation reservation,
            String paymentKey,
            String orderId,
            PaymentStatus status,
            PaymentMethod method,
            PaymentCurrency currency,
            BigDecimal totalAmount
    ) {
        this(null, reservation, paymentKey, orderId, status, method, currency, totalAmount);
    }

    public Payment(
            Long id,
            Reservation reservation,
            String paymentKey,
            String orderId,
            PaymentStatus status,
            PaymentMethod method,
            PaymentCurrency currency,
            BigDecimal totalAmount
    ) {
        validateTotalAmountRange(totalAmount);
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.status = status;
        this.method = method;
        this.currency = currency;
        this.totalAmount = totalAmount;
    }

    private void validateTotalAmountRange(BigDecimal totalAmount) {
        if (totalAmount.compareTo(MIN_AMOUNT_RANGE) < 0 || totalAmount.compareTo(MAX_AMOUNT_RANGE) > 0) {
            throw new IllegalArgumentException(String.format("%s ~ %s 사이의 금액만 결제 가능합니다.", MIN_AMOUNT_RANGE, MAX_AMOUNT_RANGE));
        }
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
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

    public String getOrderId() {
        return orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentCurrency getCurrency() {
        return currency;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
