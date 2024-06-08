package roomescape.core.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull(message = "paymentKey는 비어있을 수 없습니다.")
    private String paymentKey;

    @NotNull(message = "orderId는 비어있을 수 없습니다.")
    private String orderId;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @NotNull(message = "결제 금액은 비어있을 수 없습니다.")
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "결제 상태는 비어있을 수 없습니다.")
    private PaymentStatus status;

    public Payment() {
    }

    public Payment(final Reservation reservation, final Member member, final String paymentKey, final String orderId,
                   final Integer amount, final PaymentStatus status) {
        this(null, reservation, member, paymentKey, orderId, amount, status);
    }

    public Payment(final Long id, final Reservation reservation, final Member member, final String paymentKey,
                   final String orderId, final Integer amount, final PaymentStatus status) {
        this.id = id;
        this.reservation = reservation;
        this.member = member;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Member getMember() {
        return member;
    }

    public @NotNull(message = "paymentKey는 비어있을 수 없습니다.") String getPaymentKey() {
        return paymentKey;
    }

    public @NotNull(message = "orderId는 비어있을 수 없습니다.") String getOrderId() {
        return orderId;
    }

    public @Min(0) @Max(Integer.MAX_VALUE) @NotNull(message = "결제 금액은 비어있을 수 없습니다.") Integer getAmount() {
        return amount;
    }

    public @NotNull(message = "결제 상태는 비어있을 수 없습니다.") PaymentStatus getStatus() {
        return status;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
    }
}
