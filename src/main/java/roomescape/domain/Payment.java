package roomescape.domain;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Reservation reservation;

    private String paymentKey;

    private String orderId;

    private Long totalAmount;

    public Payment() {
    }

    public Payment(Reservation reservation, String paymentKey, String orderId, Long totalAmount) {
        this(null, reservation, paymentKey, orderId, totalAmount);
    }

    public Payment(Long id, Reservation reservation, String paymentKey, String orderId, Long totalAmount) {
        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        validateAmount(totalAmount);
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    private void validatePaymentKey(String paymentKey) {
        if (StringUtils.isBlank(paymentKey)) {
            throw new IllegalArgumentException("paymentKey가 비어 있습니다. 값을 입력해 주세요.");
        }
    }

    private void validateOrderId(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            throw new IllegalArgumentException("orderId가 비어 있습니다. 값을 입력해 주세요.");
        }
    }

    private void validateAmount(Long amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount가 비어 있습니다. 값을 입력해 주세요.");
        }
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

    public Long getTotalAmount() {
        return totalAmount;
    }
}
