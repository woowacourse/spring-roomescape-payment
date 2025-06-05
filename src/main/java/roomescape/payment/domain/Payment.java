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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.reservation.domain.Reservation;

@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "payment")
    @NotNull
    private Reservation reservation;

    @Column(name = "order_id")
    @NotNull
    private String orderId;

    @Column(name = "payment_key")
    @NotNull
    private String paymentKey;

    @Column(name = "amount")
    @NotNull
    private Long amount;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    public Payment(Reservation reservation, String orderId, String paymentKey, Long amount, PaymentType type) {
        this.reservation = reservation;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.type = type;
    }
}
