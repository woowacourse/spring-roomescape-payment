package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import roomescape.reservation.domain.Reservation;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private Long totalAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private OffsetDateTime approvedAt;

    protected Payment() {
    }

    public Payment(String orderId, String paymentKey, Long totalAmount, Reservation reservation,
                   OffsetDateTime approvedAt) {
        validate(orderId, paymentKey, totalAmount, reservation, approvedAt);
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.reservation = reservation;
        this.approvedAt = approvedAt;
    }

    private void validate(String orderId, String paymentKey, Long totalAmount, Reservation reservation,
                          OffsetDateTime approvedAt) {
        validateIsNullOrBlank(orderId, "orderId");
        validateIsNullOrBlank(paymentKey, "paymentKey");
        validateIsInvalidAmount(totalAmount);
        validateIsNull(reservation, "reservation");
        validateIsNull(approvedAt, "approvedAt");
    }

    private void validateIsNullOrBlank(String input, String fieldName) {
        if (input == null || input.isBlank()) {
            throw new RoomEscapeException(ErrorType.REQUEST_DATA_BLANK, String.format("[value : %s]", fieldName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validateIsInvalidAmount(Long totalAmount) {
        if (totalAmount == null || totalAmount < 0) {
            throw new RoomEscapeException(ErrorType.INVALID_REQUEST_DATA,
                    String.format("[totalAmount : %d]", totalAmount), HttpStatus.BAD_REQUEST);
        }
    }

    private <T> void validateIsNull(T value, String fieldName) {
        if (value == null) {
            throw new RoomEscapeException(ErrorType.REQUEST_DATA_BLANK, String.format("[value : %s]", fieldName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public OffsetDateTime getApprovedAt() {
        return approvedAt;
    }
}
