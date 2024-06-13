package roomescape.reservation.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @OneToOne
    private Reservation reservation;

    public static Payment of(final String paymentKey,
                             final String orderId,
                             final BigDecimal amount,
                             final Reservation reservation) {
        checkRequiredData(paymentKey, orderId, amount, reservation);
        return new Payment(paymentKey, orderId, amount, reservation);
    }

    private static void checkRequiredData(
            final String paymentKey,
            final String orderId,
            final BigDecimal amount,
            final Reservation reservation
    ) {
        if (paymentKey == null || orderId == null || amount == null || reservation == null) {
            throw new IllegalArgumentException("시간, 테마, 회원 정보는 Null을 입력할 수 없습니다.");
        }
    }

    protected Payment() {
    }

    private Payment(String paymentKey,
                    String orderId,
                    BigDecimal amount,
                    Reservation reservation) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
