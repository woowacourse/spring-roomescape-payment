package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    private String paymentKey;

    private String orderId;

    private Integer amount;

    private LocalDateTime paymentAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private Payment(Long id, String paymentKey, String orderId, Integer amount, LocalDateTime paymentAt,
                    Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentAt = paymentAt;
        this.reservation = reservation;
    }

    protected Payment() {
    }

    public static Payment createPaymentWithoutId(String paymentKey, String orderId, Integer amount,
                                                 LocalDateTime paymentAt,
                                                 Reservation reservation) {
        return new Payment(null, paymentKey, orderId, amount, paymentAt, reservation);
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

    public Integer getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
