package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import roomescape.reservation.domain.entity.MemberReservation;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
    @CreatedDate
    private LocalDateTime createdAt;
    @JoinColumn(name = "member_reservation_id")
    @OneToOne(fetch = FetchType.LAZY)
    private MemberReservation memberReservation;

    protected Payment() {
    }

    public Payment(
            Long id,
            String paymentKey,
            String orderId,
            BigDecimal amount,
            LocalDateTime createdAt,
            MemberReservation memberReservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.memberReservation = memberReservation;
    }

    public Payment(String paymentKey, String orderId, BigDecimal amount, MemberReservation memberReservation) {
        this(null, paymentKey, orderId, amount, null, memberReservation);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public MemberReservation getMemberReservation() {
        return memberReservation;
    }
}
