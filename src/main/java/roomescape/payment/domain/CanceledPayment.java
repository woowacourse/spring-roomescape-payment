package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Entity
public class CanceledPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;
    private String cancelReason;
    private Long cancelAmount;
    private OffsetDateTime approvedAt;
    private OffsetDateTime canceledAt;

    public CanceledPayment() {
    }

    public CanceledPayment(String paymentKey, String cancelReason, Long cancelAmount, OffsetDateTime approvedAt,
                           OffsetDateTime canceledAt) {
        validateDate(approvedAt, canceledAt);
        this.paymentKey = paymentKey;
        this.cancelReason = cancelReason;
        this.cancelAmount = cancelAmount;
        this.approvedAt = approvedAt;
        this.canceledAt = canceledAt;
    }

    private void validateDate(OffsetDateTime approvedAt, OffsetDateTime canceledAt) {
        if (canceledAt.isBefore(approvedAt)) {
            throw new RoomEscapeException(ErrorType.CANCELED_BEFORE_PAYMENT,
                    String.format("[approvedAt: %s, canceledAt: %s]", approvedAt, canceledAt),
                    HttpStatus.CONFLICT);
        }
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public Long getCancelAmount() {
        return cancelAmount;
    }

    public OffsetDateTime getApprovedAt() {
        return approvedAt;
    }

    public OffsetDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(OffsetDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }
}
