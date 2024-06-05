package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Objects;
import roomescape.domain.exception.DomainValidationException;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, BigDecimal amount, Reservation reservation) {
        this(null, paymentKey, orderId, amount, reservation);
    }

    public Payment(Long id, String paymentKey, String orderId, BigDecimal amount, Reservation reservation) {
        validate(paymentKey, orderId, amount, reservation);

        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
    }

    private void validate(String paymentKey, String orderId, BigDecimal amount, Reservation reservation) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new DomainValidationException("결제 키는 필수 값입니다.");
        }

        if (orderId == null || orderId.isBlank()) {
            throw new DomainValidationException("주문 ID는 필수 값입니다.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("금액은 0보다 커야 합니다.");
        }

        if (reservation == null) {
            throw new DomainValidationException("예약 정보는 필수 값입니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment payment)) {
            return false;
        }

        return this.getId() != null && Objects.equals(getId(), payment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
