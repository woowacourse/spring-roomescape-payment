package roomescape.core.domain;

import jakarta.persistence.Column;
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
    protected static final String PAYMENT_CANCEL_NOT_YOURS_EXCEPTION_MESSAGE = "본인의 결제만 취소할 수 있습니다.";

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
    @Column(nullable = false)
    private String paymentKey;

    @NotNull(message = "orderId는 비어있을 수 없습니다.")
    @Column(nullable = false)
    private String orderId;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @NotNull(message = "결제 금액은 비어있을 수 없습니다.")
    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "결제 상태는 비어있을 수 없습니다.")
    @Column(nullable = false)
    private PaymentStatus status;

    protected Payment() {
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

    public void cancel(final Member requester) {
        if (requester.isNotAdmin() && !member.equals(requester)) {
            throw new IllegalArgumentException(PAYMENT_CANCEL_NOT_YOURS_EXCEPTION_MESSAGE);
        }
        this.status = PaymentStatus.CANCELED;
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

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
