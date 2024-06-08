package roomescape.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
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
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;

    public CanceledPayment() {
    }

    public CanceledPayment(String paymentKey, String cancelReason, Long cancelAmount, LocalDateTime approvedAt, LocalDateTime canceledAt) {
        validateDate(approvedAt, canceledAt);
        this.paymentKey = paymentKey;
        this.cancelReason = cancelReason;
        this.cancelAmount = cancelAmount;
        this.approvedAt = approvedAt;
        this.canceledAt = canceledAt;
    }

    private void validateDate(LocalDateTime approvedAt, LocalDateTime canceledAt) {
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

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }
}
