package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.reservation.domain.Reservation;

@Entity
@Getter
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "amount", nullable = false)
    private int amount;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, int amount, Reservation reservation, PaymentStatus paymentStatus) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.reservation = reservation;
        this.status = paymentStatus;
    }

    public static Payment of(PaymentResponseDto responseDto, Reservation reservation) {
        return new Payment(
                responseDto.paymentKey(),
                responseDto.orderId(),
                responseDto.totalAmount(),
                reservation,
                PaymentStatus.COMPLETED
        );
    }

    public void changeStatus(PaymentStatus paymentStatus) {
        status = paymentStatus;
    }
}
