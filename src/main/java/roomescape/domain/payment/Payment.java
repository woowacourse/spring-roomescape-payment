package roomescape.domain.payment;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import roomescape.domain.reservation.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    private String paymentKey;

    private String orderId;

    private Long totalAmount;

    public Payment() {
    }

    public Payment(Reservation reservation, String paymentKey, String orderId) {
        this(null, reservation, paymentKey, orderId);
    }

    public Payment(Long id, Reservation reservation, String paymentKey, String orderId) {
        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        validateReservation(reservation);
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = reservation.getPrice();
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

    private void validateReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("예약이 비어 있습니다.");
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
